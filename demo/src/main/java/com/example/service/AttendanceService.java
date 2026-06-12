package com.example.service;

import com.example.entity.Attendance;
import java.util.List;

public interface AttendanceService {
    List<Attendance> findAll();
    List<Attendance> findAllForTeacher(Integer userId, Integer teacherLevel);
    Attendance findById(Integer id);
    int insert(Attendance entity);
    int update(Attendance entity);
    int deleteById(Integer id);
}
