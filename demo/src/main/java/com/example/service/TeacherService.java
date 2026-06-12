package com.example.service;

import com.example.entity.Teacher;
import java.util.List;

public interface TeacherService {
    List<Teacher> findAll();
    Teacher findById(Integer id);
    int insert(Teacher entity);
    int update(Teacher entity);
    int deleteById(Integer id);
}
