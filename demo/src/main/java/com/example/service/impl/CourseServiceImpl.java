package com.example.service.impl;

import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends TeacherScopedServiceSupport implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override public List<Course> findAll() { return courseMapper.findAll(); }
    @Override
    public List<Course> findAllForTeacher(Integer userId, Integer teacherLevel) {
        Integer teacherId = teacherScopeService.resolveTeacherId(userId);
        if (teacherId == null) {
            return List.of();
        }
        return courseMapper.findAll().stream()
                .filter(course -> Objects.equals(course.getTeacherId(), teacherId))
                .collect(Collectors.toList());
    }
    @Override public Course findById(Integer id) { return courseMapper.findById(id); }
    @Override public int insert(Course entity) { return courseMapper.insert(entity); }
    @Override public int update(Course entity) { return courseMapper.update(entity); }
    @Override public int deleteById(Integer id) { return courseMapper.deleteById(id); }
    @Override public int incrementEnrolledCount(Integer id) { return courseMapper.incrementEnrolledCount(id); }

    @Override
    public String validatePurchasable(Course course) {
        if (course == null) {
            return "课程不存在";
        }
        if (course.getStatus() == null || course.getStatus() != 1) {
            return "课程已下架，无法购买";
        }
        LocalDate today = LocalDate.now();
        if (course.getValidEnd() != null && !course.getValidEnd().isEmpty()) {
            if (LocalDate.parse(course.getValidEnd()).isBefore(today)) {
                return "课程已结束报名";
            }
        }
        if (course.getValidStart() != null && !course.getValidStart().isEmpty()) {
            if (LocalDate.parse(course.getValidStart()).isAfter(today)) {
                return "课程尚未开放报名";
            }
        }
        if (course.getMaxStudents() != null && course.getMaxStudents() > 0
                && course.getEnrolledCount() != null
                && course.getEnrolledCount() >= course.getMaxStudents()) {
            return "课程名额已满";
        }
        return null;
    }
}
