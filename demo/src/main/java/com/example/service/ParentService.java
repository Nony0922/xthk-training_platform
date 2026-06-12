package com.example.service;

import com.example.entity.Parent;
import java.util.List;

public interface ParentService {
    List<Parent> findAll();
    Parent findById(Integer id);
    Parent findByUserId(Integer userId);
    int insert(Parent entity);
    int update(Parent entity);
    int deleteById(Integer id);
}
