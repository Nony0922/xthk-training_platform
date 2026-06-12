package com.example.service.impl;

import com.example.entity.Teacher;
import com.example.mapper.TeacherMapper;
import com.example.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override public List<Teacher> findAll() { return teacherMapper.findAll(); }
    @Override public Teacher findById(Integer id) { return teacherMapper.findById(id); }
    @Override public int insert(Teacher entity) { return teacherMapper.insert(entity); }
    @Override public int update(Teacher entity) { return teacherMapper.update(entity); }
    @Override public int deleteById(Integer id) { return teacherMapper.deleteById(id); }
}
