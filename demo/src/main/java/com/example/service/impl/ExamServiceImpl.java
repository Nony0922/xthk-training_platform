package com.example.service.impl;

import com.example.entity.Exam;
import com.example.mapper.ExamMapper;
import com.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamMapper examMapper;

    @Override public List<Exam> findAll() { return examMapper.findAll(); }
    @Override public Exam findById(Integer id) { return examMapper.findById(id); }
    @Override public int insert(Exam entity) { return examMapper.insert(entity); }
    @Override public int update(Exam entity) { return examMapper.update(entity); }
    @Override public int deleteById(Integer id) { return examMapper.deleteById(id); }
}
