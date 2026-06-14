package com.example.service;

import com.example.entity.HomeVisit;
import java.util.List;

public interface HomeVisitService {
    List<HomeVisit> findAll();
    List<HomeVisit> findAllForTeacher(Integer userId, Integer teacherLevel);
    HomeVisit findById(Integer id);
    String validateForTeacher(HomeVisit entity, Integer userId, Integer teacherLevel, boolean isUpdate);
    boolean canDeleteForTeacher(Integer id, Integer userId, Integer teacherLevel);
    int insert(HomeVisit entity);
    int update(HomeVisit entity);
    int deleteById(Integer id);
}
