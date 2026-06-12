package com.example.mapper;

import com.example.entity.ClassSchedule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ClassScheduleMapper {
    List<ClassSchedule> findAll();
    ClassSchedule findById(Integer id);
    int insert(ClassSchedule entity);
    int update(ClassSchedule entity);
    int deleteById(Integer id);
}
