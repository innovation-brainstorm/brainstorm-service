package org.brainstorm.utils;

import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.Column;
import org.brainstorm.model.dto.NewSessionDto;

import java.util.ArrayList;

public class DtoToEntity {
    public static Session convertToSession(NewSessionDto sessionDto) {
        Session session = new Session();
        session.setTheSchema(sessionDto.getSchema());
        session.setTableName(sessionDto.getTable());
        session.setExpectedCount(sessionDto.getQuantity());
        session.setDestination(sessionDto.getDestination());

        ArrayList<Task> tasks = new ArrayList<>();
        for (Column column : sessionDto.getColumns()) {
            Task task = new Task();
            task.setSession(session);
            task.setColumnName(column.getName());
            //todo setGenerateByAI
            task.setStrategy(column.getStrategy());
            tasks.add(task);
        }
        session.setTasks(tasks);
        session.setColumnCount(tasks.size());
        return session;
    }
}
