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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

@RestController
@Slf4j
public class SessionStatusController {
    @Value("${root.directory}")
    private String ROOT_DIR;

    @Autowired
    private SessionStatusService sessionStatusService;
    //just a testdemo,无法多线程。后面需要的改动：前端需要将shellfile和sessionid传过来，后端分开两部分把shellfile和json分别写进同一个session entity里
    private  List<byte[]> shellFiles=new ArrayList<>();
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
    public ResponseDto<Session> generatedData( @RequestBody NewSessionDto newSessionDto)  {
            this.shellFiles.remove(0);
        //HttpServletRequest request, @ModelAttribute NewSessionDto newSessionDto
            Session session=sessionStatusService.createSessionWithTasks(DtoToEntity.convertToSession(newSessionDto,this.shellFiles));
            log.info("session and tasks created.");

            sessionStatusService.triggerTasks(session);
            log.info("start generating data....");

            return new ResponseDto<>(true, session);
    }
    @PostMapping("/session/receiveShellFiles")
    public synchronized ResponseDto receiveShellFiles(MultipartHttpServletRequest request){
        try {
            this.shellFiles.clear();
            // Get the files and form fields from the multipart request
            Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String fileName = fileNames.next();
                List<MultipartFile> file = request.getFiles(fileName);
                file.forEach(f-> {
                    try {
                        this.shellFiles.add(f.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return new ResponseDto<>(true, null);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/session/insert/{id}")
    public ResponseDto<Boolean> generatedData(@PathVariable Long id) {
        Session session = sessionStatusService.getSessionById(id);
        Status status = session.getStatus();
        if (Status.COMPLETED != status)
            return new ResponseDto<>(false, "This session " + id + " has not completed or failed.");

        log.info("start inserting data into and tasks created.");
        boolean succeed = sessionStatusService.insertIntoDatabase(session);

        return new ResponseDto<>(succeed, null);
    }

    @PostMapping("/session/clearAll")
    public ResponseDto<Boolean> generatedData() {
        try {
            File file = new File(ROOT_DIR);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (Exception e) {
            log.error("clear failed", e);
            return new ResponseDto<>(false, null);
        }
        return new ResponseDto<>(true, null);
    }


    @GetMapping("/session/download/{type}/{id}")
    public ResponseEntity<Resource> download(@PathVariable String type, @PathVariable Long id) {
        Session session = sessionStatusService.getSessionById(id);
        String filename = id + "." + type;
        String path = ROOT_DIR + File.separator + session.getDirectory() + File.separator + filename;
        if (!Status.COMPLETED.equals(session.getStatus()) || !Arrays.asList("csv", "sql").contains(type) || !new File(path).exists())
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=error.csv").
                    contentType(MediaType.valueOf("application/x-msdownload; chatset=utf-8")).
                    body(new ClassPathResource("error.csv"));
        else {
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename).
                    contentType(MediaType.valueOf("application/x-msdownload; chatset=utf-8")).
                    body(new FileSystemResource(path));
        }
    }

    //for test
    @PostMapping("/task/createTask")
    public TaskResponseDto createTask(@RequestBody TaskInfoDTO taskInfoDTO) throws IOException {
        System.out.println(taskInfoDTO.getModelId());
        System.out.println(taskInfoDTO.isUsingExistModel());

        ArrayList<String> values = new ArrayList<>();
        values.add(taskInfoDTO.getColumnName());
        for (int i = 0; i < taskInfoDTO.getExpectedCount(); i++) {
            values.add(i + "");
        }
        String filePath = taskInfoDTO.getFilePath();
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        String dir = filePath.substring(0, filePath.length() - fileName.length() - 1);
        dir = dir.substring(0, dir.lastIndexOf(File.separator));
        FileUtils.writeLines(new File(dir + File.separator + taskInfoDTO.getColumnName() + ".csv"), values);
        return new TaskResponseDto(taskInfoDTO.getSessionId(), taskInfoDTO.getTaskId(), Status.RUNNING);
    }

    //for test
    @GetMapping("/task/model/{modelId}")
    public Boolean createTask(@PathVariable String modelId) {
        System.out.println(modelId);
        return true;
    }
}
