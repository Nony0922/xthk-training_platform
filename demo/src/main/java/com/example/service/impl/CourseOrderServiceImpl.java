package com.example.service.impl;

import com.example.entity.CourseOrder;
import com.example.mapper.CourseOrderMapper;
import com.example.service.CourseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseOrderServiceImpl implements CourseOrderService {
    @Autowired
    private CourseOrderMapper courseOrderMapper;

    @Override public List<CourseOrder> findAll() { return courseOrderMapper.findAll(); }
    @Override public CourseOrder findById(Integer id) { return courseOrderMapper.findById(id); }
    @Override public int insert(CourseOrder entity) { return courseOrderMapper.insert(entity); }
    @Override public int update(CourseOrder entity) { return courseOrderMapper.update(entity); }
    @Override public int deleteById(Integer id) { return courseOrderMapper.deleteById(id); }
}
