package com.example.service;

import com.example.entity.Exam;
import java.util.List;

public interface ExamService {
    List<Exam> findAll();
    List<Exam> findAllForTeacher(Integer userId, Integer teacherLevel);
    Exam findById(Integer id);
    int insert(Exam entity);
    int update(Exam entity);
    int deleteById(Integer id);
}
