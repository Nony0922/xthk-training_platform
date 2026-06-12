package com.example.controller;

import com.example.entity.Student;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@CrossOrigin
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public List<Student> findAll(@RequestParam(required = false) Integer scopeUserId,
                                 @RequestParam(required = false) Integer teacherLevel) {
        if (scopeUserId != null && teacherLevel != null) {
            return studentService.findAllForTeacher(scopeUserId, teacherLevel);
        }
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Integer id) { return studentService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Student entity) {
        Map<String, Object> result = new HashMap<>();
        int r = studentService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Student entity) {
        Map<String, Object> result = new HashMap<>();
        int r = studentService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = studentService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
