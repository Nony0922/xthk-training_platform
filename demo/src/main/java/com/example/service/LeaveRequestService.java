package com.example.service;

import com.example.entity.LeaveRequest;
import java.util.List;

public interface LeaveRequestService {
    List<LeaveRequest> findAll();
    List<LeaveRequest> findAllForTeacher(Integer userId, Integer teacherLevel);
    LeaveRequest findById(Integer id);
    int insert(LeaveRequest entity);
    int update(LeaveRequest entity);
    int deleteById(Integer id);
}
