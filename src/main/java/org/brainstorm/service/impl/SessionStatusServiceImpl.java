package org.brainstorm.service.impl;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.repository.SessionRepository;
import org.brainstorm.repository.TaskRepository;
import org.brainstorm.service.SessionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@Transactional
public class SessionStatusServiceImpl implements SessionStatusService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow(() -> new RuntimeException(id + "not found"));
    }

    @Override
    public Session createSessionWithTasks(Session session) {
        if (session.getTasks().isEmpty()) throw new RuntimeException("tasks can't be empty.");

        session.setColumnCount(session.getTasks().size());
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
