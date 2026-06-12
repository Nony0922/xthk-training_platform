package com.example.mapper;

import com.example.entity.CourseOrder;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseOrderMapper {
    List<CourseOrder> findAll();
    CourseOrder findById(Integer id);
    int insert(CourseOrder entity);
    int update(CourseOrder entity);
    int deleteById(Integer id);
}
