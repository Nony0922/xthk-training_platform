package com.example.service;

import com.example.entity.ClassSchedule;
import java.util.List;

public interface ClassScheduleService {
    List<ClassSchedule> findAll();
    ClassSchedule findById(Integer id);
    int insert(ClassSchedule entity);
    int update(ClassSchedule entity);
    int deleteById(Integer id);
}
