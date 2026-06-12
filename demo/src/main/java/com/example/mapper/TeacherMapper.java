package com.example.mapper;

import com.example.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeacherMapper {
    List<Teacher> findAll();
    Teacher findById(Integer id);
    Teacher findByUserId(@Param("userId") Integer userId);
    int insert(Teacher entity);
    int update(Teacher entity);
    int deleteById(Integer id);
}
