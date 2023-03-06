package org.brainstorm.service.impl;

import org.brainstorm.service.StudentService;
import org.brainstorm.model.Student;
import org.brainstorm.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "not found"));
    }

    public StudentServiceImpl(StudentRepository studentRepository) {
        super();
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(long id, Student StudentRequest) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "not found"));

        student.setStudentId(StudentRequest.getStudentId());
        student.setName(StudentRequest.getName());
        student.setAge(StudentRequest.getAge());
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id + "not found"));

        studentRepository.delete(student);
    }
}
