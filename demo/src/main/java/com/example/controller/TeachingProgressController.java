package com.example.controller;

import com.example.entity.TeachingProgress;
import com.example.service.TeachingProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/progress")
@CrossOrigin
public class TeachingProgressController {
    @Autowired
    private TeachingProgressService teachingProgressService;

    @GetMapping("/list")
    public List<TeachingProgress> findAll() { return teachingProgressService.findAll(); }

    @GetMapping("/{id}")
    public TeachingProgress findById(@PathVariable Integer id) { return teachingProgressService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody TeachingProgress entity) {
        Map<String, Object> result = new HashMap<>();
        int r = teachingProgressService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TeachingProgress entity) {
        Map<String, Object> result = new HashMap<>();
        int r = teachingProgressService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = teachingProgressService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
