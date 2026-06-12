package com.example.mapper;

import com.example.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ScoreMapper {
    List<Score> findAll();
    Score findById(Integer id);
    int insert(Score entity);
    int update(Score entity);
    int deleteById(Integer id);
}
