package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.TaskResponseDto;
import org.brainstorm.repository.SessionRepository;
import org.brainstorm.repository.TaskRepository;
import org.brainstorm.service.SessionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SessionStatusServiceImpl implements SessionStatusService {
    //todo add host name
    String URL_AI = "task/createTask";

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TaskRepository taskRepository;


    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new RuntimeException(id + "not found"));
    }

    @Override
    public void triggerTasks(Session session) {
        List<Task> tasks = session.getTasks();
        for (Task task : tasks) {
            if (task.isGeneratedByAI()) {
                //start self-service tasks.
                //todo
            } else {
                startAITask(session, task);
            }
        }

    }

    private void startAITask(Session session, Task task) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject taskInfo = new JSONObject();
        taskInfo.put("sessionId", session.getId());
        taskInfo.put("taskId", task.getId());
        taskInfo.put("columnName", task.getColumnName());
        taskInfo.put("expectedCount", session.getExpectedCount());
        taskInfo.put("filePath", session.getDirectory());//todo
        HttpEntity<String> request = new HttpEntity<>(taskInfo.toString(), headers);

        ResponseEntity<TaskResponseDto> responseEntity = restTemplate.postForEntity(URL_AI, request, TaskResponseDto.class);
        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            TaskResponseDto body = responseEntity.getBody();
            Task updateTask = new Task();
            updateTask.setId(body.getTaskId());
            updateTask.setStatus(body.getStatus());
            updateTask(task);
        } else {
            //todo
            log.error("response error.");
        }
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

        if (Status.COMPLETED.equals(savedTask.getStatus())) {
            Session session = savedTask.getSession();
            if (isReady(session)) {
                session.setStatus(Status.COMPLETED);
            }
        }

        return savedTask;
    }

    @Override
    public boolean updateTask(Long id, Status status) {
        if (id == null) throw new RuntimeException("taskId can't be null.");
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException(id + "not found"));
        task.setStatus(status);
        taskRepository.save(task);
        return true;
    }

    @Override
    public Session updateSession(Session session) {
        return sessionRepository.save(session);
    }

    private boolean isReady(Session session) {
        int columnCount = session.getColumnCount();
        for (Task task : session.getTasks()) {
            if (!Status.COMPLETED.equals(task.getStatus())) return false;
            columnCount--;
        }
        //todo: check the file exists in the directory.
        return columnCount == 0;
    }
}
