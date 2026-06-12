package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    List<Student> findAll();
    Student findById(Integer id);
    List<Map<String, Object>> countByClassId();
    int insert(Student entity);
    int update(Student entity);
    int deleteById(Integer id);
}
