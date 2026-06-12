package com.example.service;

import com.example.entity.Clazz;
import java.util.List;

public interface ClazzService {
    List<Clazz> findAll();
    Clazz findById(Integer id);
    int insert(Clazz entity);
    int update(Clazz entity);
    int deleteById(Integer id);
}
