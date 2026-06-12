package com.example.service.impl;

import com.example.entity.AbnormalAttendance;
import com.example.mapper.AbnormalAttendanceMapper;
import com.example.service.AbnormalAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AbnormalAttendanceServiceImpl implements AbnormalAttendanceService {
    @Autowired
    private AbnormalAttendanceMapper abnormalAttendanceMapper;

    @Override public List<AbnormalAttendance> findAll() { return abnormalAttendanceMapper.findAll(); }
    @Override public AbnormalAttendance findById(Integer id) { return abnormalAttendanceMapper.findById(id); }
    @Override public int insert(AbnormalAttendance entity) { return abnormalAttendanceMapper.insert(entity); }
    @Override public int update(AbnormalAttendance entity) { return abnormalAttendanceMapper.update(entity); }
    @Override public int deleteById(Integer id) { return abnormalAttendanceMapper.deleteById(id); }
}
