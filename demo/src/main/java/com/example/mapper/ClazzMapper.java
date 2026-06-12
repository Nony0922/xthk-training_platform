package com.example.mapper;

import com.example.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ClazzMapper {
    List<Clazz> findAll();
    Clazz findById(Integer id);
    int insert(Clazz entity);
    int update(Clazz entity);
    int deleteById(Integer id);
}
