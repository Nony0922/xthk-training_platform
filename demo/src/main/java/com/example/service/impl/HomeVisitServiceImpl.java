package com.example.service.impl;

import com.example.entity.HomeVisit;
import com.example.mapper.HomeVisitMapper;
import com.example.service.HomeVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HomeVisitServiceImpl implements HomeVisitService {
    @Autowired
    private HomeVisitMapper homeVisitMapper;

    @Override public List<HomeVisit> findAll() { return homeVisitMapper.findAll(); }
    @Override public HomeVisit findById(Integer id) { return homeVisitMapper.findById(id); }
    @Override public int insert(HomeVisit entity) { return homeVisitMapper.insert(entity); }
    @Override public int update(HomeVisit entity) { return homeVisitMapper.update(entity); }
    @Override public int deleteById(Integer id) { return homeVisitMapper.deleteById(id); }
}
