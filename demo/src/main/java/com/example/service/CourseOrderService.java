package com.example.service;

import com.example.entity.CourseOrder;
import java.util.List;

public interface CourseOrderService {
    List<CourseOrder> findAll();
    CourseOrder findById(Integer id);
    int insert(CourseOrder entity);
    int update(CourseOrder entity);
    int deleteById(Integer id);
}
