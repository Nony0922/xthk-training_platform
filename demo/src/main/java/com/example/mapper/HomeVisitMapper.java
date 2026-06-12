package com.example.mapper;

import com.example.entity.HomeVisit;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HomeVisitMapper {
    List<HomeVisit> findAll();
    HomeVisit findById(Integer id);
    int insert(HomeVisit entity);
    int update(HomeVisit entity);
    int deleteById(Integer id);
}
