package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.brainstorm.instant.Status;

import org.brainstorm.model.MODE;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.AIUpdateDTO;
import org.brainstorm.model.dto.TaskResponseDto;
import org.brainstorm.repository.SessionRepository;
import org.brainstorm.repository.TaskRepository;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.SessionStatusService;
import org.brainstorm.service.StrategyData;
import org.brainstorm.service.ValueFromTPDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import com.opencsv.CSVWriter;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
public class SessionStatusServiceImpl implements SessionStatusService {
    @Value("${AI.CREATE.TASK.URL}")
    private String AI_CREATE_TASK_URL;

    @Value("${AI.NUMBER.OF.VALUE.NEEDED}")
    private Long NUMBER_OF_VALUE_NEEDED_AI;

    @Value("${root.directory}")
    private String ROOT_DIR;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataGenerateStrategyService strategyService;

    @Autowired
    private ValueFromTPDB valueFromTPDB;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 1L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

    private ConcurrentHashMap<Long, Integer> sessionId2unfinishedTasks = new ConcurrentHashMap<>();

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new RuntimeException(id + "not found"));
    }

    @Override
    public void triggerTasks(Session session) {
        session.setStatus(Status.RUNNING);

        List<Task> tasks = session.getTasks();
        List<Task> call_AI_task=new ArrayList<>();
        for (Task task : tasks) {
            if (task.isGeneratedByAI()|task.getStrategy()==3) {
                call_AI_task.add(task);
                //startAITask(session, task);
            }
            else {
                log.info("start generating data,not AI server taskId: {}.", task.getId());
                startTask(session, task);
            }
        }
        /*先处理不需要python ai server的task，再处理需要ai sever的task。
        目的是当需要用到customer_model的时候，需要先把其他column的数据都拿到，传给用户，再执行用户的shell脚本。
        所以需要先将其他的column先生成完毕，最后再执行customer_model的task*/
        for(Task AI_task :call_AI_task){
            log.info("start generating data, AI server taskId: {}.", AI_task.getId());
            startAITask(session,AI_task);
        }
        log.info("all tasks has been triggered.");
    }

    private void startTask(Session session, Task task) {
        //DataType defaultDataType = DefaultDataType.getDefaultDataType(StrategyEnums.values()[task.getStrategy()], (int) session.getExpectedCount());
        task.setStatus(Status.RUNNING);

        executor.execute(() -> {
            try {
                log.info("start generateData");
                StrategyData strategyData = strategyService.generateData(session,task);
                log.info(" generateData done");
                String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + task.getFileName();
                populateValueInFile(filePath, Arrays.asList(task.getColumnName()), true);
                populateValueInFile(filePath, strategyData.getData(), false);

                task.setStatus(Status.COMPLETED);
                log.info("start update task.");
                updateTask(task);
                log.info("update task done.");
            } catch (Exception e) {
                log.error("taskId: {} generate data failed, failed reason: {}", task.getId(), e.getMessage());
                task.setStatus(Status.ERROR);
                taskRepository.save(task);
                sessionRepository.updateStatusById(session.getId(), Status.ERROR); // seems in another thread entity status  will be detached
            }
        });
    }

    private void startAITask(Session session, Task task) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String path = ROOT_DIR + File.separator + session.getDirectory() + File.separator + "learning" + File.separator + task.getFileName();
        try {
            if(!task.isPretrained()) {
                generateLearningData(session.getTableName(), task.getColumnName(), path);
            }

        } catch (Exception e) {
            task.setStatus(Status.ERROR);
            return;
        }

        JSONObject taskInfo = new JSONObject();
        taskInfo.put("sessionId", session.getId());
        taskInfo.put("taskId", task.getId());
        taskInfo.put("columnName", task.getColumnName());
        taskInfo.put("expectedCount", session.getExpectedCount());
        taskInfo.put("status", task.getStatus());
        //taskInfo.put("filePath", path);

        if(task.getStrategy()==3){
            taskInfo.put("usingExistModel", true);
            String docker_shell_path = ROOT_DIR + File.separator + "models"+File.separator+"customer_model";
            //把shell文件放到docker目录下，并添加相应的taskInfo
            try{
                saveShell(task.getShellfile(),new File(docker_shell_path));
                String shell_model_id=new File(docker_shell_path).getName();
                taskInfo.put("modelId", shell_model_id);
            }
            catch (Exception e){
                log.error("taskId: {} move shell file failed, failed reason: {}",task.getId(),e.getMessage());
            }
            //将该表中其他column所生成的新数据合并成一个sql文件，放到filepath下，并添加对应的taskInfo
            try{
                String docker_generateddata_path=ROOT_DIR + File.separator + session.getTableName()+File.separator;
                List<String> column_names=valueFromTPDB.getColumnNames(session.getTableName());
                column_names.remove(task.getColumnName());
                List<File> column_files=new ArrayList<>();
                column_names.forEach(column_name->{
                    column_files.add(new File(docker_generateddata_path+column_name+".csv"));
                });
                String outputCsvFile=docker_generateddata_path+session.getTableName()+".csv";
                saveHistoryColumntoSQL(column_files,outputCsvFile);
                taskInfo.put("filePath",outputCsvFile );
            } catch (Exception e) {
                log.error("taskId: {} generate merged datafile failed, failed reason: {}",task.getId(),e.getMessage());
            }

        } else if (task.getStrategy()==0) {
            taskInfo.put("modelId", task.getModelId());
            taskInfo.put("filePath", path);
            taskInfo.put("usingExistModel", task.isPretrained());
        }

        HttpEntity<String> request = new HttpEntity<>(taskInfo.toString(), headers);

        log.info("columnName: {}, call ML service...", task.getColumnName());
        //请求ml server
        ResponseEntity<TaskResponseDto> responseEntity = restTemplate.postForEntity(AI_CREATE_TASK_URL, request, TaskResponseDto.class);
        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            log.info("columnName: {}, ML service responded with OK.", task.getColumnName());
            TaskResponseDto body = responseEntity.getBody();
            task.setStatus(body.getStatus());
        } else {
            task.setStatus(Status.ERROR);
            log.error("AI generate data failed, response error. error code: {}," +
                    "error msg: {}", responseEntity.getStatusCode(), responseEntity.getBody());
        }
        updateTask(task);
    }

    private void generateLearningData(String tableName, String columnName, String filePath) throws Exception {
        populateValueInFile(filePath, Arrays.asList("\"" + columnName + "\""), true);
        int batchCount = 1000;
        for (int i = 0; i * batchCount <= NUMBER_OF_VALUE_NEEDED_AI; i++) {
            List<String> data = valueFromTPDB.getDataByColumnMySQL(tableName, columnName, i * batchCount, (i + 1) * batchCount);
            populateValueInFile(filePath, data, false);
        }
    }


    @Override
    public Session createSessionWithTasks(Session session) {
        if (session.getTasks().isEmpty()) throw new RuntimeException("tasks can't be empty.");
        Session savedSession = sessionRepository.save(session);
        sessionId2unfinishedTasks.put(session.getId(), session.getTasks().size());
        return savedSession;
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getId() == null) throw new RuntimeException("taskId can't be null.");
        Task savedTask = taskRepository.save(task);
        Session session = savedTask.getSession();

        if (Status.ERROR == savedTask.getStatus()) {
            sessionId2unfinishedTasks.remove(session.getId());
            sessionRepository.updateStatusById(session.getId(), Status.ERROR);
        }

        //set session completed when all tasks are completed.
        if (Status.COMPLETED == savedTask.getStatus()) {
            //evert time complete a task,reset the currenthashmap value-1,and when zero,save session
            if (sessionId2unfinishedTasks.computeIfPresent(session.getId(), (k, v) -> --v) == 0) {
                try {
                    generateTestFile(session);
                    sessionRepository.updateStatusById(session.getId(), Status.COMPLETED);
                } catch (IOException | ScriptException e) {
                    log.error(e.getMessage());
                    sessionRepository.updateStatusById(session.getId(), Status.ERROR);
                }
            }
        }

        return savedTask;
    }

    @Override
    public boolean insertIntoDatabase(Session session) {
        String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + session.getId() + ".sql";
        try {
            valueFromTPDB.executeScript(filePath);
        } catch (Exception e) {
            log.error("insert into db failed!", e);
            return false;
        }
        return true;
    }


    private void generateTestFile(Session session) throws IOException {
        List<Task> tasks = session.getTasks();
        List<List<String>> values = new ArrayList<>();
        for (Task task : tasks) {
            String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + task.getFileName();
            File file = new File(filePath);
            if (!file.exists()) throw new IOException(filePath + ": does not exist.");
            values.add(FileUtils.readLines(file, Charset.defaultCharset()));
        }

        doGenerateFile(values, session, MODE.view);
        doGenerateFile(values, session, MODE.sql);
    }

    private void doGenerateFile(List<List<String>> values, Session session, MODE mode) throws IOException {
        String fileName;

        int minRow = Integer.MAX_VALUE;
        for (List<String> value : values) minRow = Math.min(minRow, value.size());

        StringBuilder sb;
        List<String> insertValues = new ArrayList<>();

        //insert tableName (col1,col2) values (val1, val2);
        StringBuilder columns = new StringBuilder();
        if (mode != MODE.view) {
            fileName = ROOT_DIR + File.separator + session.getDirectory() + File.separator + session.getId() + ".sql";

            for (List<String> value : values) {
                columns.append(value.get(0)).append(",");
            }
            columns.delete(columns.length() - 1, columns.length());

            for (int row = 1; row < minRow; row++) {
                sb = new StringBuilder();

                sb.append("insert " + session.getTheSchema() + "." + session.getTableName() + " (" + columns + ") values (");
                for (int col = 0; col < values.size(); col++) {
                    sb.append("'").append(values.get(col).get(row)).append("'").append(",");
                }
                sb.delete(sb.length() - 1, sb.length());
                sb.append(");");

                insertValues.add(sb.toString());
            }
        } else {
            fileName = ROOT_DIR + File.separator + session.getDirectory() + File.separator + session.getId() + ".csv";

            for (int row = 0; row < minRow; row++) {
                sb = new StringBuilder();
                for (int col = 0; col < values.size(); col++) {
                    sb.append(values.get(col).get(row)).append(",");
                }
                sb.delete(sb.length() - 1, sb.length());
                insertValues.add(sb.toString());
            }
        }
        populateValueInFile(fileName, insertValues, true);
    }

    @Override
    public Task updateTask(AIUpdateDTO dto) {
        if (dto.getTaskId() == null) throw new RuntimeException("taskId can't be null.");
        Task task = taskRepository.findById(dto.getTaskId()).orElseThrow(() -> new RuntimeException(dto.getTaskId() + " not found"));
        task.setStatus(dto.getStatus());
        String filePath = dto.getFilePath();
        if (StringUtils.isNotBlank(filePath))
            task.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
        return updateTask(task);
    }

    @Override
    public Session updateSession(Session session) {
        return sessionRepository.save(session);
    }

    private void populateValueInFile(String filePath, List<String> values, boolean deleteExists) throws IOException {
        File file = new File(filePath);
        if (deleteExists && file.exists()) file.delete();
        log.info("file path: {}", file.getAbsolutePath());
        FileUtils.touch(new File(filePath));
        FileUtils.writeLines(file, values, true);
    }

    private void saveShell(byte[] shell_file, File destination_parent) throws IOException{
        if(!destination_parent.exists()){
            destination_parent.mkdirs();
        }
        File shell_destination = new File(destination_parent.toString()+File.separator+"main.sh");
        shell_destination.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(shell_destination, false)) {
            fos.write(shell_file);
        }
        File modeltype_destination = new File(destination_parent.toString()+File.separator+"model_type.txt");
        try (FileOutputStream fos = new FileOutputStream(modeltype_destination, false)) {
            fos.write("CUSTOM".getBytes());
        }

    }
    private void saveHistoryColumntoSQL(List<File> column_files,String outputCsvFile)throws IOException{
         // 输出的CSV文件名
        File f=new File(outputCsvFile);
        if (f.exists()) f.delete();
        CSVWriter csvWriter = new CSVWriter(new FileWriter(outputCsvFile,false));
        List<String[]> all_column=new ArrayList<>();
        int max_column_len=0;
        //read several column files
        for (File csvFile : column_files) {
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
            String line;
            List<String> column=new ArrayList<>();
            while ((line = csvReader.readLine()) != null) {
                column.add(line);
            }
            all_column.add(column.toArray(new String[column.size()]));
            max_column_len=Math.max(column.size(),max_column_len);
        }
        //write column by row
        for(int i=0;i<max_column_len;i++) {
            String[] rowData = new String[all_column.size()];
            for (int j = 0; j < all_column.size(); j++) {
                if (i >= all_column.get(j).length) {
                    rowData[j] = null;
                } else rowData[j] = all_column.get(j)[i];
            }
            csvWriter.writeNext(rowData);
        }
        csvWriter.close();
        System.out.println("CSV files merged successfully.");
    }

}
