package com.example.service.impl;

import com.example.entity.ClassSchedule;
import com.example.mapper.ClassScheduleMapper;
import com.example.service.ClassScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassScheduleServiceImpl extends TeacherScopedServiceSupport implements ClassScheduleService {
    @Autowired
    private ClassScheduleMapper classScheduleMapper;

    @Override public List<ClassSchedule> findAll() { return classScheduleMapper.findAll(); }

    @Override
    public List<ClassSchedule> findAllForTeacher(Integer userId, Integer teacherLevel, String semester) {
        Integer teacherId = teacherScopeService.resolveTeacherId(userId);
        if (teacherId == null) {
            return List.of();
        }
        Map<Integer, ClassSchedule> merged = new LinkedHashMap<>();
        if (teacherLevel != null && teacherLevel == 2) {
            List<Integer> classIds = classIds(userId, teacherLevel);
            if (!classIds.isEmpty()) {
                classScheduleMapper.findByClassIds(classIds, semester).forEach(s -> merged.put(s.getId(), s));
            }
        }
        classScheduleMapper.findByTeacherId(teacherId, semester).forEach(s -> merged.put(s.getId(), s));
        return new ArrayList<>(merged.values());
    }

    @Override public List<String> findSemesters() { return classScheduleMapper.findSemesters(); }
    @Override public ClassSchedule findById(Integer id) { return classScheduleMapper.findById(id); }
    @Override public int insert(ClassSchedule entity) { return classScheduleMapper.insert(entity); }
    @Override public int update(ClassSchedule entity) { return classScheduleMapper.update(entity); }
    @Override public int deleteById(Integer id) { return classScheduleMapper.deleteById(id); }
}
