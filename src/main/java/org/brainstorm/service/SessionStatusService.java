package org.brainstorm.service;

import org.brainstorm.model.Session;
import org.brainstorm.model.Task;

public interface SessionStatusService {
    Session getSessionById(Long id);

    Session createSessionWithTasks(Session session);

    Task updateTask(Task task);

    Session updateSession(Session session);

}
