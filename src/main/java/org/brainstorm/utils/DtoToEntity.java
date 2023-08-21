package org.brainstorm.utils;

import com.mysql.cj.jdbc.Blob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.AIUpdateDTO;
import org.brainstorm.model.dto.Column;
import org.brainstorm.model.dto.NewSessionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
@Slf4j
public class DtoToEntity {
    //在这里新增一个实体类shellfile
    public static Session convertToSession(NewSessionDto sessionDto,List<byte[]> shellFiles )  {
        Session session = new Session();
        session.setTheSchema(sessionDto.getSchema());
        session.setTableName(sessionDto.getTable());
        session.setExpectedCount(sessionDto.getQuantity());
        session.setDestination(sessionDto.getDestination());

        ArrayList<Task> tasks = new ArrayList<>();
        for (int i=0;i<sessionDto.getColumns().size();i++) {
            Column column=sessionDto.getColumns().get(i);
            Task task = new Task();
            task.setSession(session);
            task.setColumnName(column.getName());
            task.setGeneratedByAI(column.getStrategy() == 0);
            task.setStrategy(column.getStrategy());
            //set task shell file

            task.setShellfile(shellFiles.get(i));

            task.setModelId(column.getModelId());
            task.setPretrained(column.getIsPretrained() == null ? false : column.getIsPretrained());
            tasks.add(task);
        }
        session.setTasks(tasks);
        session.setColumnCount(tasks.size());
        return session;
    }

    public static Task convertToTask(AIUpdateDTO aiUpdateDTO) {
        Task task = new Task();
        task.setId(aiUpdateDTO.getTaskId());
        task.setStatus(aiUpdateDTO.getStatus());
        task.setFileName(aiUpdateDTO.getFilePath());
        return task;
    }
    public static byte[] convertBlobToByteArray(Blob blob) {
        try (InputStream inputStream = blob.getBinaryStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
