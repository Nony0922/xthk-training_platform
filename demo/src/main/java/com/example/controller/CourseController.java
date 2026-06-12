package com.example.controller;

import com.example.entity.Course;
import com.example.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
@CrossOrigin
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public List<Course> findAll(@RequestParam(required = false) Integer scopeUserId,
                                @RequestParam(required = false) Integer teacherLevel) {
        if (scopeUserId != null && teacherLevel != null) {
            return courseService.findAllForTeacher(scopeUserId, teacherLevel);
        }
        return courseService.findAll();
    }

    @GetMapping("/{id}")
    public Course findById(@PathVariable Integer id) { return courseService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Course entity) {
        Map<String, Object> result = new HashMap<>();
        int r = courseService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Course entity) {
        Map<String, Object> result = new HashMap<>();
        int r = courseService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = courseService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
