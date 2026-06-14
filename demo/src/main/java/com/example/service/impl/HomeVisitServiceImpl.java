package com.example.service.impl;

import com.example.entity.HomeVisit;
import com.example.mapper.HomeVisitMapper;
import com.example.mapper.StudentMapper;
import com.example.service.HomeVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeVisitServiceImpl extends TeacherScopedServiceSupport implements HomeVisitService {
    @Autowired
    private HomeVisitMapper homeVisitMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override public List<HomeVisit> findAll() { return homeVisitMapper.findAll(); }

    @Override
    public List<HomeVisit> findAllForTeacher(Integer userId, Integer teacherLevel) {
        Set<Integer> studentIds = scopedStudentIds(studentMapper.findAll(), userId, teacherLevel);
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return homeVisitMapper.findAll().stream()
                .filter(item -> studentIds.contains(item.getStudentId()))
                .collect(Collectors.toList());
    }

    @Override public HomeVisit findById(Integer id) { return homeVisitMapper.findById(id); }

    @Override
    public String validateForTeacher(HomeVisit entity, Integer userId, Integer teacherLevel, boolean isUpdate) {
        Integer teacherId = teacherScopeService.resolveTeacherId(userId);
        if (teacherId == null) {
            return "教师信息不存在";
        }
        Set<Integer> studentIds = scopedStudentIds(studentMapper.findAll(), userId, teacherLevel);
        if (entity.getStudentId() == null || !studentIds.contains(entity.getStudentId())) {
            return "只能为本班学生登记家访";
        }
        entity.setTeacherId(teacherId);
        if (isUpdate && entity.getId() != null) {
            HomeVisit existing = homeVisitMapper.findById(entity.getId());
            if (existing == null || !studentIds.contains(existing.getStudentId())) {
                return "无权修改该家访记录";
            }
        }
        return null;
    }

    @Override
    public boolean canDeleteForTeacher(Integer id, Integer userId, Integer teacherLevel) {
        HomeVisit existing = homeVisitMapper.findById(id);
        if (existing == null) {
            return false;
        }
        Set<Integer> studentIds = scopedStudentIds(studentMapper.findAll(), userId, teacherLevel);
        return studentIds.contains(existing.getStudentId());
    }

    @Override public int insert(HomeVisit entity) { return homeVisitMapper.insert(entity); }
    @Override public int update(HomeVisit entity) { return homeVisitMapper.update(entity); }
    @Override public int deleteById(Integer id) { return homeVisitMapper.deleteById(id); }
}
