package com.example.service.impl;

import com.example.entity.Parent;
import com.example.mapper.ParentMapper;
import com.example.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParentServiceImpl implements ParentService {
    @Autowired
    private ParentMapper parentMapper;

    @Override public List<Parent> findAll() { return parentMapper.findAll(); }
    @Override public Parent findById(Integer id) { return parentMapper.findById(id); }
    @Override public Parent findByUserId(Integer userId) { return parentMapper.findByUserId(userId); }
    @Override public int insert(Parent entity) { return parentMapper.insert(entity); }
    @Override public int update(Parent entity) { return parentMapper.update(entity); }
    @Override public int deleteById(Integer id) { return parentMapper.deleteById(id); }
}
