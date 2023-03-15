package org.brainstorm.service.impl;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.service.SessionStatusService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Arrays;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Transactional
class SessionStatusServiceImplTest {

    @Autowired
    SessionStatusService sessionService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createSessionWithTasksTest() {
        Session session = new Session("tableA");
        Task task1 = new Task(session, "appid");
        Task task2 = new Task(session, "app_name");
        session.setTasks(Arrays.asList(task1, task2));

        Session savedSession = sessionService.createSessionWithTasks(session);
        Assert.assertNotNull(savedSession.getId());
        Assert.assertFalse(savedSession.getTasks().isEmpty());
        Assert.assertNotNull(savedSession.getTasks().get(0).getId());
    }

    @Test
    void updateAndFindSessionTest() {
        Session session = new Session("tableA");
        Task task1 = new Task(session, "appid");
        Task task2 = new Task(session, "app_name");
        session.setTasks(Arrays.asList(task1, task2));

        Session savedSession = sessionService.createSessionWithTasks(session);
        Assert.assertEquals(Status.NEW, savedSession.getStatus());

        task1.setStatus(Status.COMPLETED);
        sessionService.updateTask(task1);
        Session completedSession = sessionService.getSessionById(session.getId());
        Assert.assertEquals(Status.NEW, completedSession.getStatus());

        task2.setStatus(Status.COMPLETED);
        sessionService.updateTask(task2);
        completedSession = sessionService.getSessionById(session.getId());
        Assert.assertEquals(Status.COMPLETED, completedSession.getStatus());
    }
}