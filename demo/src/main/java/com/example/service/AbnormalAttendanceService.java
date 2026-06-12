package com.example.service;

import com.example.entity.AbnormalAttendance;
import java.util.List;

public interface AbnormalAttendanceService {
    List<AbnormalAttendance> findAll();
    AbnormalAttendance findById(Integer id);
    int insert(AbnormalAttendance entity);
    int update(AbnormalAttendance entity);
    int deleteById(Integer id);
}
