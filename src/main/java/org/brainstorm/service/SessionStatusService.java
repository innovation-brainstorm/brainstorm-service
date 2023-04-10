package org.brainstorm.service;

import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.AIUpdateDTO;

public interface SessionStatusService {
    Session getSessionById(Long id);

    void triggerTasks(Session session);

    Session createSessionWithTasks(Session session);

    Task updateTask(Task task);

    Task updateTask(AIUpdateDTO dto) ;

    Session updateSession(Session session);

}
