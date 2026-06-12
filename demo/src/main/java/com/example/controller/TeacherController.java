package com.example.controller;

import com.example.entity.Teacher;
import com.example.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher")
@CrossOrigin
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/list")
    public List<Teacher> findAll() { return teacherService.findAll(); }

    @GetMapping("/{id}")
    public Teacher findById(@PathVariable Integer id) { return teacherService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Teacher entity) {
        Map<String, Object> result = new HashMap<>();
        int r = teacherService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Teacher entity) {
        Map<String, Object> result = new HashMap<>();
        int r = teacherService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = teacherService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
