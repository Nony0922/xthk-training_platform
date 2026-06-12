package com.example.service.impl;

import com.example.entity.LeaveRequest;
import com.example.mapper.LeaveRequestMapper;
import com.example.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Override public List<LeaveRequest> findAll() { return leaveRequestMapper.findAll(); }
    @Override public LeaveRequest findById(Integer id) { return leaveRequestMapper.findById(id); }
    @Override public int insert(LeaveRequest entity) { return leaveRequestMapper.insert(entity); }
    @Override public int update(LeaveRequest entity) { return leaveRequestMapper.update(entity); }
    @Override public int deleteById(Integer id) { return leaveRequestMapper.deleteById(id); }
}
