package com.example.service.impl;

import com.example.entity.LeaveRequest;
import com.example.mapper.LeaveRequestMapper;
import com.example.mapper.StudentMapper;
import com.example.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl extends TeacherScopedServiceSupport implements LeaveRequestService {
    @Autowired
    private LeaveRequestMapper leaveRequestMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override public List<LeaveRequest> findAll() { return leaveRequestMapper.findAll(); }
    @Override
    public List<LeaveRequest> findAllForTeacher(Integer userId, Integer teacherLevel) {
        Set<Integer> studentIds = scopedStudentIds(studentMapper.findAll(), userId, teacherLevel);
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return leaveRequestMapper.findAll().stream()
                .filter(item -> studentIds.contains(item.getStudentId()))
                .collect(Collectors.toList());
    }
    @Override public LeaveRequest findById(Integer id) { return leaveRequestMapper.findById(id); }
    @Override public int insert(LeaveRequest entity) { return leaveRequestMapper.insert(entity); }
    @Override public int update(LeaveRequest entity) { return leaveRequestMapper.update(entity); }
    @Override public int deleteById(Integer id) { return leaveRequestMapper.deleteById(id); }
}
