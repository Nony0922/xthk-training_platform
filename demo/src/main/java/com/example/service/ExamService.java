package com.example.service;

import com.example.entity.Exam;
import java.util.List;

public interface ExamService {
    List<Exam> findAll();
    Exam findById(Integer id);
    int insert(Exam entity);
    int update(Exam entity);
    int deleteById(Integer id);
}
