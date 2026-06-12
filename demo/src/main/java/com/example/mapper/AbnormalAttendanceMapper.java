package com.example.mapper;

import com.example.entity.AbnormalAttendance;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AbnormalAttendanceMapper {
    List<AbnormalAttendance> findAll();
    AbnormalAttendance findById(Integer id);
    int insert(AbnormalAttendance entity);
    int update(AbnormalAttendance entity);
    int deleteById(Integer id);
}
