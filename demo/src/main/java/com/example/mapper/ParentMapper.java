package com.example.mapper;

import com.example.entity.Parent;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ParentMapper {
    List<Parent> findAll();
    Parent findById(Integer id);
    Parent findByUserId(Integer userId);
    int insert(Parent entity);
    int update(Parent entity);
    int deleteById(Integer id);
}
