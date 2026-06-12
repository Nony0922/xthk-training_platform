package com.example.service.impl;

import com.example.entity.Teacher;
import com.example.mapper.ClassScheduleMapper;
import com.example.mapper.ClazzMapper;
import com.example.mapper.TeacherMapper;
import com.example.service.TeacherScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeacherScopeServiceImpl implements TeacherScopeService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private ClassScheduleMapper classScheduleMapper;

    @Override
    public List<Integer> resolveClassIds(Integer userId, Integer teacherLevel) {
        Teacher teacher = teacherMapper.findByUserId(userId);
        if (teacher == null) {
            return List.of();
        }
        if (teacherLevel != null && teacherLevel == 2) {
            return clazzMapper.findIdsByHeadTeacherId(teacher.getId());
        }
        return classScheduleMapper.findClassIdsByTeacherId(teacher.getId());
    }

    @Override
    public Integer resolveTeacherId(Integer userId) {
        Teacher teacher = teacherMapper.findByUserId(userId);
        return teacher != null ? teacher.getId() : null;
    }
}
