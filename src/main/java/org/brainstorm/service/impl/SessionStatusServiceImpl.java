package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.brainstorm.instant.Status;
import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.DefaultDataType;
import org.brainstorm.interfaces.strategy.StrategyEnums;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.AIUpdateDTO;
import org.brainstorm.model.dto.TaskResponseDto;
import org.brainstorm.repository.SessionRepository;
import org.brainstorm.repository.TaskRepository;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.SessionStatusService;
import org.brainstorm.service.StrategyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class SessionStatusServiceImpl implements SessionStatusService {
    @Value("${URL.AI}")
    private String URL_AI;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataGenerateStrategyService strategyService;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 1L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

    private ConcurrentHashMap<Long, Integer> sessionId2unfinishedTasks = new ConcurrentHashMap<>();

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new RuntimeException(id + "not found"));
    }

    @Override
    public void triggerTasks(Session session) {
        sessionId2unfinishedTasks.put(session.getId(), session.getTasks().size());
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
        DataType defaultDataType = DefaultDataType.getDefaultDataType(StrategyEnums.values()[task.getStrategy()]);
        DefaultDataType.setLength((int) session.getExpectedCount());

        task.setStatus(Status.RUNNING);

        executor.execute(() -> {
            try {
                StrategyData strategyData = strategyService.generateData(defaultDataType, task.getStrategy());
                populateValueInFile(File.separator + session.getDirectory() + File.separator + task.getFileName(), strategyData.getData());
                TimeUnit.SECONDS.sleep(2);

                task.setStatus(Status.COMPLETED);
                updateTask(task);
            } catch (Exception e) {
                log.error("taskId: {} generate data failed, failed reason: {}", task.getId(), e.getMessage());
                task.setStatus(Status.ERROR);
                taskRepository.save(task);
                sessionRepository.updateStatusById(session.getId(),Status.ERROR); // seems in another thread entity status  will be detached
            }
        });
    }

    private void startAITask(Session session, Task task) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject taskInfo = new JSONObject();
        taskInfo.put("sessionId", session.getId());
        taskInfo.put("taskId", task.getId());
        taskInfo.put("columnName", task.getColumnName());
        taskInfo.put("expectedCount", session.getExpectedCount());
        taskInfo.put("status", task.getStatus());
        taskInfo.put("filePath", session.getDirectory());
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


    @Override
    public Session createSessionWithTasks(Session session) {
        if (session.getTasks().isEmpty()) throw new RuntimeException("tasks can't be empty.");
        return sessionRepository.save(session);
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getId() == null) throw new RuntimeException("taskId can't be null.");
        Task savedTask = taskRepository.save(task);

        //set session completed when all tasks are completed.
        if (Status.COMPLETED.equals(savedTask.getStatus())) {
            Session session = savedTask.getSession();
            sessionId2unfinishedTasks.computeIfPresent(session.getId(), (k, v) -> --v);
            System.out.println(sessionId2unfinishedTasks.get(session.getId()));
            if (sessionId2unfinishedTasks.getOrDefault(session.getId(), -1) == 0) {
                sessionRepository.updateStatusById(session.getId(), Status.COMPLETED);//it's fine we set multiple times & don't use save, it will change related tasks' status
            }
        }

        return savedTask;
    }

    @Override
    public Task updateTask(AIUpdateDTO dto) {
        if (dto.getTaskId() == null) throw new RuntimeException("taskId can't be null.");
        Task task = taskRepository.findById(dto.getTaskId()).orElseThrow(() -> new RuntimeException(dto.getTaskId() + " not found"));
        task.setStatus(dto.getStatus());
        task.setFileName(dto.getFilePath());
        return updateTask(task);
    }

    @Override
    public Session updateSession(Session session) {
        return sessionRepository.save(session);
    }

    private void populateValueInFile(String filePath, List<Integer> values) throws IOException {
        File file = new File(filePath);
        FileUtils.touch(new File(filePath));
        FileUtils.writeLines(file, values);
    }

}
