package com.example.service.impl;

import com.example.entity.Exam;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExamMapper;
import com.example.service.ExamService;
import com.example.util.ExamStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends TeacherScopedServiceSupport implements ExamService {
    @Autowired
    private ExamMapper examMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Exam> findAll() {
        return examMapper.findAll().stream().peek(ExamStatusUtil::applyResolvedStatus).collect(Collectors.toList());
    }

    @Override
    public List<Exam> findAllForTeacher(Integer userId, Integer teacherLevel) {
        List<Integer> classIds = classIds(userId, teacherLevel);
        Set<Integer> courseIds = courseIdsOf(userId, teacherLevel);
        if (classIds.isEmpty() && courseIds.isEmpty()) {
            return List.of();
        }
        return examMapper.findAll().stream()
                .filter(item -> matchesTeacherScope(item, classIds, courseIds, teacherLevel))
                .peek(ExamStatusUtil::applyResolvedStatus)
                .collect(Collectors.toList());
    }

    private Set<Integer> courseIdsOf(Integer userId, Integer teacherLevel) {
        if (teacherLevel == null || teacherLevel != 1) {
            return Set.of();
        }
        Integer teacherId = teacherScopeService.resolveTeacherId(userId);
        if (teacherId == null) {
            return Set.of();
        }
        return courseMapper.findAll().stream()
                .filter(course -> Objects.equals(course.getTeacherId(), teacherId))
                .map(com.example.entity.Course::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private boolean matchesTeacherScope(Exam exam, List<Integer> classIds, Set<Integer> courseIds, Integer teacherLevel) {
        if (exam.getClassId() != null && classIds.contains(exam.getClassId())) {
            return true;
        }
        return teacherLevel != null && teacherLevel == 1
                && exam.getCourseId() != null
                && courseIds.contains(exam.getCourseId());
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
