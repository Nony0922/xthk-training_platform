package com.example.mapper;

import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StudentMapper {
    List<Student> findAll();
    Student findById(Integer id);
    int insert(Student entity);
    int update(Student entity);
    int deleteById(Integer id);
}
