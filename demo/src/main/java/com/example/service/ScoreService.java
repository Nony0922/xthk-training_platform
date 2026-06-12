package com.example.service;

import com.example.entity.Score;
import java.util.List;

public interface ScoreService {
    List<Score> findAll();
    Score findById(Integer id);
    int insert(Score entity);
    int update(Score entity);
    int deleteById(Integer id);
}
