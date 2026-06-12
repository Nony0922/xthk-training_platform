package com.example.service;

import java.util.List;

public interface TeacherScopeService {
    List<Integer> resolveClassIds(Integer userId, Integer teacherLevel);
    Integer resolveTeacherId(Integer userId);
}
