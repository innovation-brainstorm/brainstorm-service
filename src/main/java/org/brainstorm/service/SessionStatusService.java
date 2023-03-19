package org.brainstorm.service;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;

public interface SessionStatusService {
    Session getSessionById(Long id);

    Session createSessionWithTasks(Session session);

    Task updateTask(Task task);

    boolean updateTask(Long id, Status status);

    Session updateSession(Session session);

}
