package com.example.service.impl;

import com.example.entity.Score;
import com.example.mapper.ScoreMapper;
import com.example.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    private ScoreMapper scoreMapper;

    @Override public List<Score> findAll() { return scoreMapper.findAll(); }
    @Override public Score findById(Integer id) { return scoreMapper.findById(id); }
    @Override public int insert(Score entity) { return scoreMapper.insert(entity); }
    @Override public int update(Score entity) { return scoreMapper.update(entity); }
    @Override public int deleteById(Integer id) { return scoreMapper.deleteById(id); }
}
