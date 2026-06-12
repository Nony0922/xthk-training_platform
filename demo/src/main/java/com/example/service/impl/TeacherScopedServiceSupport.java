package com.example.service.impl;

import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.TeacherScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

abstract class TeacherScopedServiceSupport {

    @Autowired
    protected TeacherScopeService teacherScopeService;

    protected List<Integer> classIds(Integer userId, Integer teacherLevel) {
        return teacherScopeService.resolveClassIds(userId, teacherLevel);
    }

    protected List<Student> scopedStudents(List<Student> all, Integer userId, Integer teacherLevel) {
        List<Integer> classIds = classIds(userId, teacherLevel);
        if (classIds.isEmpty()) {
            return List.of();
        }
        return all.stream()
                .filter(s -> s.getClassId() != null && classIds.contains(s.getClassId()))
                .collect(Collectors.toList());
    }

    protected Set<Integer> scopedStudentIds(List<Student> all, Integer userId, Integer teacherLevel) {
        return scopedStudents(all, userId, teacherLevel).stream()
                .map(Student::getId)
                .collect(Collectors.toSet());
    }

    protected Set<Integer> scopedParentIds(List<Student> all, Integer userId, Integer teacherLevel) {
        return scopedStudents(all, userId, teacherLevel).stream()
                .map(Student::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
