package org.brainstorm.interfaces.rest;

import io.swagger.v3.oas.annotations.Hidden;
import org.apache.commons.lang3.RandomStringUtils;
import org.brainstorm.interfaces.facade.dto.StudentDto;
import org.brainstorm.model.Student;
import org.brainstorm.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Hidden
@RestController
@RequestMapping("/student")
public class StudentRestController {
    @Autowired
    StudentService studentService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping(value = "{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable("id") Long id) {
        Student std = studentService.getStudentById(id);
        StudentDto studentDto = modelMapper.map(std, StudentDto.class);
        return ResponseEntity.ok().body(studentDto);
    }

    @PostMapping("/generateRandom")
    public boolean generateRandomStudent() {
        ArrayList<Student> students = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10001; i++) {
            Student student = new Student();
            student.setName(RandomStringUtils.randomAlphabetic(5) + " " + RandomStringUtils.randomAlphabetic(5));
            student.setAge(random.nextInt(100));
            students.add(student);
            if (i % 1000 == 0) {
                studentService.createStudent(students);
                students.clear();
            }
        }
        studentService.createStudent(students);
        return true;
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto) {

        // convert DTO to entity
        Student studentRequest = modelMapper.map(studentDto, Student.class);

        Student student = studentService.createStudent(studentRequest);

        // convert entity to DTO
        StudentDto studentResponse = modelMapper.map(student, StudentDto.class);

        return new ResponseEntity<StudentDto>(studentResponse, HttpStatus.CREATED);
    }

    // change the request for DTO
    // change the response for DTO
    @PostMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable long id, @RequestBody StudentDto studentDto) {

        // convert DTO to Entity
        Student studentRequest = modelMapper.map(studentDto, Student.class);

        Student student = studentService.updateStudent(id, studentRequest);

        // entity to DTO
        StudentDto studentResponse = modelMapper.map(student, StudentDto.class);

        return ResponseEntity.ok().body(studentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<Student>> deleteStudent(@PathVariable(name = "id") Long id) {
        try {
            studentService.deleteStudent(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("error when delete id" + id);
        }
    }
}
