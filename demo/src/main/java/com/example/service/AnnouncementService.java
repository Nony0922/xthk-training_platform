package com.example.service;

import com.example.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> findAll();
    Announcement findById(Integer id);
    int insert(Announcement entity);
    int update(Announcement entity);
    int deleteById(Integer id);
}
