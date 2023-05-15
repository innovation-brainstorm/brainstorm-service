package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.brainstorm.instant.Status;
import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.DefaultDataType;
import org.brainstorm.interfaces.strategy.StrategyEnums;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class SessionStatusServiceImpl implements SessionStatusService {
    @Value("${AI.URL}")
    private String URL_AI;

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
        for (Task task : tasks) {
            log.info("start generating data, taskId: {}.", task.getId());

            if (task.isGeneratedByAI()) startAITask(session, task);
            else startTask(session, task);
        }
        log.info("all tasks has been triggered.");
    }

    private void startTask(Session session, Task task) {
        DataType defaultDataType = DefaultDataType.getDefaultDataType(StrategyEnums.values()[task.getStrategy()], (int) session.getExpectedCount());
        task.setStatus(Status.RUNNING);

        executor.execute(() -> {
            try {
                StrategyData strategyData = strategyService.generateData(defaultDataType, task.getStrategy());
                String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + task.getFileName();
                populateValueInFile(filePath, Arrays.asList(task.getColumnName()), true);
                populateValueInFile(filePath, strategyData.getData(), false);
//                TimeUnit.SECONDS.sleep(2);

                task.setStatus(Status.COMPLETED);
                updateTask(task);
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
            generateLearningData(session.getTableName(), task.getColumnName(), path);
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
        taskInfo.put("filePath", path);
        HttpEntity<String> request = new HttpEntity<>(taskInfo.toString(), headers);

        ResponseEntity<TaskResponseDto> responseEntity = restTemplate.postForEntity(URL_AI, request, TaskResponseDto.class);
        if (HttpStatus.OK == responseEntity.getStatusCode()) {
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

        //set session completed when all tasks are completed.
        if (Status.COMPLETED.equals(savedTask.getStatus())) {
            Session session = savedTask.getSession();
            synchronized (this) {
                sessionId2unfinishedTasks.computeIfPresent(session.getId(), (k, v) -> --v);
                if (sessionId2unfinishedTasks.getOrDefault(session.getId(), -1) == 0) {
                    try {
                        generateTestFile(session);
                        if (MODE.insert == session.getDestination()) {
                            String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + "test.sql";
                            valueFromTPDB.executeScript(filePath);
                        }
                        sessionRepository.updateStatusById(session.getId(), Status.COMPLETED);
                    } catch (IOException | ScriptException e) {
                        sessionRepository.updateStatusById(session.getId(), Status.ERROR);
                    }
                }
            }
        }

        return savedTask;
    }


    private void generateTestFile(Session session) throws IOException {
        List<Task> tasks = session.getTasks();
        List<List<String>> values = new ArrayList<>();
        for (Task task : tasks) {
            String filePath = ROOT_DIR + File.separator + session.getDirectory() + File.separator + task.getFileName();
            File file = new File(filePath);
            if (!file.exists()) throw new IOException("file not exist.");
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
            fileName = ROOT_DIR + File.separator + session.getDirectory() + File.separator + "test.sql";

            for (List<String> value : values) {
                columns.append(value.get(0)).append(",");
            }
            columns.delete(columns.length() - 1, columns.length());
            for (int row = 1; row < minRow; row++) {
                sb = new StringBuilder();

                sb.append("insert " + session.getTheSchema() + "." + session.getTableName() + " (" + columns + ") values (");
                for (int col = 0; col < values.size(); col++) {
                    sb.append(values.get(col).get(row)).append(",");
                }
                sb.delete(sb.length() - 1, sb.length());
                sb.append(");");

                insertValues.add(sb.toString());
            }
        } else {
            fileName = ROOT_DIR + File.separator + session.getDirectory() + File.separator + "test.csv";

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
        if (StringUtils.isNotBlank(dto.getFilePath())) task.setFileName(dto.getFilePath());
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

}
