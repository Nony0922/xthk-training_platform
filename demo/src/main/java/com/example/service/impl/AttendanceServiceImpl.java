package com.example.service.impl;

import com.example.entity.Attendance;
import com.example.mapper.AttendanceMapper;
import com.example.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override public List<Attendance> findAll() { return attendanceMapper.findAll(); }
    @Override public Attendance findById(Integer id) { return attendanceMapper.findById(id); }
    @Override public int insert(Attendance entity) { return attendanceMapper.insert(entity); }
    @Override public int update(Attendance entity) { return attendanceMapper.update(entity); }
    @Override public int deleteById(Integer id) { return attendanceMapper.deleteById(id); }
}
