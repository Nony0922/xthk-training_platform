package com.example.service.impl;

import com.example.entity.ClassSchedule;
import com.example.mapper.ClassScheduleMapper;
import com.example.service.ClassScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {
    @Autowired
    private ClassScheduleMapper classScheduleMapper;

    @Override public List<ClassSchedule> findAll() { return classScheduleMapper.findAll(); }
    @Override public ClassSchedule findById(Integer id) { return classScheduleMapper.findById(id); }
    @Override public int insert(ClassSchedule entity) { return classScheduleMapper.insert(entity); }
    @Override public int update(ClassSchedule entity) { return classScheduleMapper.update(entity); }
    @Override public int deleteById(Integer id) { return classScheduleMapper.deleteById(id); }
}
