package com.example.service.impl;

import com.example.entity.Clazz;
import com.example.mapper.ClazzMapper;
import com.example.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzMapper clazzMapper;

    @Override public List<Clazz> findAll() { return clazzMapper.findAll(); }
    @Override public Clazz findById(Integer id) { return clazzMapper.findById(id); }
    @Override public int insert(Clazz entity) { return clazzMapper.insert(entity); }
    @Override public int update(Clazz entity) { return clazzMapper.update(entity); }
    @Override public int deleteById(Integer id) { return clazzMapper.deleteById(id); }
}
