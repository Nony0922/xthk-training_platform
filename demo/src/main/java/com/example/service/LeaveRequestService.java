package com.example.service;

import com.example.entity.LeaveRequest;
import java.util.List;

public interface LeaveRequestService {
    List<LeaveRequest> findAll();
    LeaveRequest findById(Integer id);
    int insert(LeaveRequest entity);
    int update(LeaveRequest entity);
    int deleteById(Integer id);
}
