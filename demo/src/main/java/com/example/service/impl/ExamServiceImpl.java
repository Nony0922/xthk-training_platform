package com.example.service.impl;

import com.example.entity.Exam;
import com.example.mapper.ExamMapper;
import com.example.service.ExamService;
import com.example.util.ExamStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends TeacherScopedServiceSupport implements ExamService {
    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<Exam> findAll() {
        return examMapper.findAll().stream().peek(ExamStatusUtil::applyResolvedStatus).collect(Collectors.toList());
    }

    @Override
    public List<Exam> findAllForTeacher(Integer userId, Integer teacherLevel) {
        if (isTeachingScope(teacherLevel)) {
            Set<Integer> courseIds = teachingCourseIds(userId);
            if (courseIds.isEmpty()) {
                return List.of();
            }
            return examMapper.findAll().stream()
                    .filter(exam -> exam.getCourseId() != null && courseIds.contains(exam.getCourseId()))
                    .peek(ExamStatusUtil::applyResolvedStatus)
                    .collect(Collectors.toList());
        }
        List<Integer> classIds = classIds(userId, teacherLevel);
        if (classIds.isEmpty()) {
            return List.of();
        }
        return examMapper.findAll().stream()
                .filter(exam -> exam.getClassId() != null && classIds.contains(exam.getClassId()))
                .peek(ExamStatusUtil::applyResolvedStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Exam findById(Integer id) {
        Exam exam = examMapper.findById(id);
        ExamStatusUtil.applyResolvedStatus(exam);
        return exam;
    }

    @Override
    public int insert(Exam entity) {
        ExamStatusUtil.applyResolvedStatus(entity);
        return examMapper.insert(entity);
    }

    @Override
    public int update(Exam entity) {
        ExamStatusUtil.applyResolvedStatus(entity);
        return examMapper.update(entity);
    }

    @Override public int deleteById(Integer id) { return examMapper.deleteById(id); }
}
