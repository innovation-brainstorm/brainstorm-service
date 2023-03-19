package org.brainstorm.controller;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.service.SessionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionStatusController {
    @Autowired
    private SessionStatusService sessionStatusService;

    @GetMapping("/session/getStatus/{id}")
    public Status getSessionStatus(@PathVariable Long id) {
        return sessionStatusService.getSessionById(id).getStatus();
    }

//    @GetMapping("/session/get/{id}")
//    public Session getSession(@PathVariable Long id) {
//        return sessionStatusService.getSessionById(id);
//    }

    @PostMapping("/task/updateStatus/{id}")
    public boolean updateTaskStatus(@PathVariable Long id, @RequestParam Status status) {
        return sessionStatusService.updateTask(id, status);
    }
}
