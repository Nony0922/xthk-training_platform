package com.example.controller;

import com.example.entity.Announcement;
import com.example.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcement")
@CrossOrigin
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/list")
    public List<Announcement> findAll() { return announcementService.findAll(); }

    @GetMapping("/{id}")
    public Announcement findById(@PathVariable Integer id) { return announcementService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Announcement entity) {
        Map<String, Object> result = new HashMap<>();
        int r = announcementService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Announcement entity) {
        Map<String, Object> result = new HashMap<>();
        int r = announcementService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = announcementService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
