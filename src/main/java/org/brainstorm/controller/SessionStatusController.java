package org.brainstorm.controller;

import lombok.extern.slf4j.Slf4j;
import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.model.dto.*;
import org.brainstorm.service.SessionStatusService;
import org.brainstorm.utils.DtoToEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SessionStatusController {
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

    //for test
    @PostMapping("/task/createTask")
    public @ResponseBody TaskResponseDto createTask(@RequestBody TaskInfoDTO taskInfoDTO) {
        return new TaskResponseDto(taskInfoDTO.getSessionId(), taskInfoDTO.getTaskId(), Status.RUNNING);
    }
}
