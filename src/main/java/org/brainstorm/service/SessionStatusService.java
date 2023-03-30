package org.brainstorm.service;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;

import java.util.List;

public interface SessionStatusService {
    Session getSessionById(Long id);

    void triggerTasks(Session session);

    Session createSessionWithTasks(Session session);

    Task updateTask(Task task);

    boolean updateTask(Long id, Status status);

    Session updateSession(Session session);

}
