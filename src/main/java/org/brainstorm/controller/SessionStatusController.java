package org.brainstorm.controller;

import lombok.extern.slf4j.Slf4j;
import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.dto.NewSessionDto;
import org.brainstorm.service.SessionStatusService;
import org.brainstorm.utils.DtoToEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SessionStatusController {
    @Autowired
    private SessionStatusService sessionStatusService;

    @GetMapping("/session/getStatus/{id}")
    public ResponseEntity<Status> getSessionStatus(@PathVariable Long id) {
        return new ResponseEntity(sessionStatusService.getSessionById(id).getStatus(), HttpStatus.OK);
    }

//    @GetMapping("/session/get/{id}")
//    public Session getSession(@PathVariable Long id) {
//        return sessionStatusService.getSessionById(id);
//    }

    @PostMapping("/task/updateStatus/{id}")
    public boolean updateTaskStatus(@PathVariable Long id, @RequestParam Status status) {
        return sessionStatusService.updateTask(id, status);
    }


    @PostMapping("/session/generatedData")
    public ResponseEntity<Session> generatedData(@RequestBody NewSessionDto newSessionDto) {
        Session session = sessionStatusService.createSessionWithTasks(DtoToEntity.convertToSession(newSessionDto));
        log.info("session and tasks created.");

        sessionStatusService.triggerTasks(session);
        log.info("start generating data....");

        return new ResponseEntity(session, HttpStatus.OK);
    }
}
