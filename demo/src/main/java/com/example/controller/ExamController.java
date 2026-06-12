package com.example.controller;

import com.example.entity.Exam;
import com.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
@CrossOrigin
public class ExamController {
    @Autowired
    private ExamService examService;

    @GetMapping("/list")
    public List<Exam> findAll() { return examService.findAll(); }

    @GetMapping("/{id}")
    public Exam findById(@PathVariable Integer id) { return examService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Exam entity) {
        Map<String, Object> result = new HashMap<>();
        int r = examService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Exam entity) {
        Map<String, Object> result = new HashMap<>();
        int r = examService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = examService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
