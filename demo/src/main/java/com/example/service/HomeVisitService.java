package com.example.service;

import com.example.entity.HomeVisit;
import java.util.List;

public interface HomeVisitService {
    List<HomeVisit> findAll();
    HomeVisit findById(Integer id);
    int insert(HomeVisit entity);
    int update(HomeVisit entity);
    int deleteById(Integer id);
}
