package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseMapper {
    List<Course> findAll();
    Course findById(Integer id);
    int insert(Course entity);
    int update(Course entity);
    int deleteById(Integer id);
    int incrementEnrolledCount(Integer id);
}
