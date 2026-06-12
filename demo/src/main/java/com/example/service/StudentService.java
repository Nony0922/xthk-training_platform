package com.example.service;

import com.example.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    Student findById(Integer id);
    int insert(Student entity);
    int update(Student entity);
    int deleteById(Integer id);
}
