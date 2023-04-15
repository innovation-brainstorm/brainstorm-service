package org.brainstorm.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.*;
import org.brainstorm.service.SessionStatusService;
import org.brainstorm.utils.DtoToEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@Slf4j
public class SessionStatusController {
    @Value("${root.directory}")
    private String ROOT_DIR;

    @Autowired
    private SessionStatusService sessionStatusService;

    @GetMapping("/session/getStatus/{id}")
    public ResponseDto<Status> getSessionStatus(@PathVariable Long id) {
        Status status = sessionStatusService.getSessionById(id).getStatus();
        return new ResponseDto(true, status);
    }

    @GetMapping("/session/get/{id}")
    public ResponseDto<Session> getSession(@PathVariable Long id) {
        return new ResponseDto<>(true, sessionStatusService.getSessionById(id));
    }

    @PostMapping("/task/updateStatus")
    public ResponseDto<Task> updateTaskStatus(@RequestBody AIUpdateDTO dto) {
        return new ResponseDto<>(true, sessionStatusService.updateTask(dto));
    }

    @PostMapping("/session/generatedData")
    public ResponseDto<Session> generatedData(@RequestBody NewSessionDto newSessionDto) {
        Session session = sessionStatusService.createSessionWithTasks(DtoToEntity.convertToSession(newSessionDto));
        log.info("session and tasks created.");

        sessionStatusService.triggerTasks(session);
        log.info("start generating data....");

        return new ResponseDto<>(true, session);
    }

    @GetMapping("/session/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Session session = sessionStatusService.getSessionById(id);
        if (!Status.COMPLETED.equals(session.getStatus()))
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=error.csv").
                    contentType(MediaType.valueOf("application/x-msdownload; chatset=utf-8")).
                    body(new ClassPathResource("error.csv"));
        else
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.csv").
                    contentType(MediaType.valueOf("application/x-msdownload; chatset=utf-8")).
                    body(new FileSystemResource(ROOT_DIR + File.separator + session.getDirectory() + File.separator + "test.csv"));
    }

    //for test
    @PostMapping("/task/createTask")
    public TaskResponseDto createTask(@RequestBody TaskInfoDTO taskInfoDTO) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        values.add(taskInfoDTO.getColumnName());
        for (int i = 0; i <taskInfoDTO.getExpectedCount(); i++) {
            values.add(i + "");
        }
        String filePath = taskInfoDTO.getFilePath();
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        String dir = filePath.substring(0, filePath.length() - fileName.length()-1);
        dir = dir.substring(0, dir.lastIndexOf(File.separator));
        FileUtils.writeLines(new File(dir + File.separator + taskInfoDTO.getColumnName() + ".csv"), values);
        return new TaskResponseDto(taskInfoDTO.getSessionId(), taskInfoDTO.getTaskId(), Status.RUNNING);
    }
}
