package org.brainstorm.service;

import org.brainstorm.model.Student;

import java.util.List;

public interface StudentService {
    Student getStudentById(Long StudentId);

    List<Student> getAllStudents();

    Student createStudent(Student Student);

    void createStudent(List<Student> students);

    Student updateStudent(long id, Student Student);

    void deleteStudent(long id);
}
