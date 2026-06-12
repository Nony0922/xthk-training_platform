package com.example.service.impl;

import com.example.entity.Announcement;
import com.example.mapper.AnnouncementMapper;
import com.example.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override public List<Announcement> findAll() { return announcementMapper.findAll(); }
    @Override public Announcement findById(Integer id) { return announcementMapper.findById(id); }
    @Override public int insert(Announcement entity) { return announcementMapper.insert(entity); }
    @Override public int update(Announcement entity) { return announcementMapper.update(entity); }
    @Override public int deleteById(Integer id) { return announcementMapper.deleteById(id); }
}
