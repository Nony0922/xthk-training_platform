package com.example.service.impl;

import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override public List<Course> findAll() { return courseMapper.findAll(); }
    @Override public Course findById(Integer id) { return courseMapper.findById(id); }
    @Override public int insert(Course entity) { return courseMapper.insert(entity); }
    @Override public int update(Course entity) { return courseMapper.update(entity); }
    @Override public int deleteById(Integer id) { return courseMapper.deleteById(id); }
}
