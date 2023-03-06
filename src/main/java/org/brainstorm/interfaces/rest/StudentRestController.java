package org.brainstorm.interfaces.rest;

import org.brainstorm.interfaces.facade.dto.StudentDto;
import org.brainstorm.model.Student;
import org.brainstorm.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentRestController {
    @Autowired
    StudentService studentService;

    @Autowired
    ModelMapper modelMapper;

    @RequestMapping(value = "{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable("id") Long id){
        Student std = studentService.getStudentById(id);
        StudentDto studentDto = modelMapper.map(std, StudentDto.class);
        return ResponseEntity.ok().body(studentDto);
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
