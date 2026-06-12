package com.example.service.impl;

import com.example.entity.TeachingProgress;
import com.example.mapper.TeachingProgressMapper;
import com.example.service.TeachingProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeachingProgressServiceImpl implements TeachingProgressService {
    @Autowired
    private TeachingProgressMapper teachingProgressMapper;

    @Override public List<TeachingProgress> findAll() { return teachingProgressMapper.findAll(); }
    @Override public TeachingProgress findById(Integer id) { return teachingProgressMapper.findById(id); }
    @Override public int insert(TeachingProgress entity) { return teachingProgressMapper.insert(entity); }
    @Override public int update(TeachingProgress entity) { return teachingProgressMapper.update(entity); }
    @Override public int deleteById(Integer id) { return teachingProgressMapper.deleteById(id); }
}
