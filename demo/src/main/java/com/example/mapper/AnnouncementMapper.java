package com.example.mapper;

import com.example.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AnnouncementMapper {
    List<Announcement> findAll();
    Announcement findById(Integer id);
    int insert(Announcement entity);
    int update(Announcement entity);
    int deleteById(Integer id);
}
