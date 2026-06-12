package com.example.service.impl;

import com.example.entity.Student;
import com.example.mapper.StudentMapper;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override public List<Student> findAll() { return studentMapper.findAll(); }
    @Override public Student findById(Integer id) { return studentMapper.findById(id); }
    @Override public int insert(Student entity) { return studentMapper.insert(entity); }
    @Override public int update(Student entity) { return studentMapper.update(entity); }
    @Override public int deleteById(Integer id) { return studentMapper.deleteById(id); }
}
