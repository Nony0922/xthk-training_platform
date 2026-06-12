package com.example.mapper;

import com.example.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AttendanceMapper {
    List<Attendance> findAll();
    Attendance findById(Integer id);
    int insert(Attendance entity);
    int update(Attendance entity);
    int deleteById(Integer id);
}
