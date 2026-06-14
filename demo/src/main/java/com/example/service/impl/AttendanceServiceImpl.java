package com.example.service.impl;

import com.example.entity.Attendance;
import com.example.mapper.AttendanceMapper;
import com.example.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl extends TeacherScopedServiceSupport implements AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override public List<Attendance> findAll() { return attendanceMapper.findAll(); }

    @Override
    public List<Attendance> findAllForTeacher(Integer userId, Integer teacherLevel) {
        if (isTeachingScope(teacherLevel)) {
            Set<Integer> courseIds = teachingCourseIds(userId);
            if (courseIds.isEmpty()) {
                return List.of();
            }
            return attendanceMapper.findAll().stream()
                    .filter(item -> item.getCourseId() != null && courseIds.contains(item.getCourseId()))
                    .collect(Collectors.toList());
        }
        List<Integer> classIds = classIds(userId, teacherLevel);
        if (classIds.isEmpty()) {
            return List.of();
        }
        return attendanceMapper.findAll().stream()
                .filter(item -> item.getClassId() != null && classIds.contains(item.getClassId()))
                .collect(Collectors.toList());
    }

    @Override public Attendance findById(Integer id) { return attendanceMapper.findById(id); }
    @Override public int insert(Attendance entity) { return attendanceMapper.insert(entity); }
    @Override public int update(Attendance entity) { return attendanceMapper.update(entity); }
    @Override public int deleteById(Integer id) { return attendanceMapper.deleteById(id); }
}
