package com.example.mapper;

import com.example.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExamMapper {
    List<Exam> findAll();
    Exam findById(Integer id);
    int insert(Exam entity);
    int update(Exam entity);
    int deleteById(Integer id);
}
