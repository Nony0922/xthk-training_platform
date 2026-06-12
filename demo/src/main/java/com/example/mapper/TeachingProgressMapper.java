package com.example.mapper;

import com.example.entity.TeachingProgress;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TeachingProgressMapper {
    List<TeachingProgress> findAll();
    TeachingProgress findById(Integer id);
    int insert(TeachingProgress entity);
    int update(TeachingProgress entity);
    int deleteById(Integer id);
}
