package com.example.service;

import com.example.entity.TeachingProgress;
import java.util.List;

public interface TeachingProgressService {
    List<TeachingProgress> findAll();
    TeachingProgress findById(Integer id);
    int insert(TeachingProgress entity);
    int update(TeachingProgress entity);
    int deleteById(Integer id);
}
