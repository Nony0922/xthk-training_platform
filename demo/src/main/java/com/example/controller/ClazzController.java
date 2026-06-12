package com.example.controller;

import com.example.entity.Clazz;
import com.example.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clazz")
@CrossOrigin
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    @GetMapping("/list")
    public List<Clazz> findAll() { return clazzService.findAll(); }

    @GetMapping("/{id}")
    public Clazz findById(@PathVariable Integer id) { return clazzService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Clazz entity) {
        Map<String, Object> result = new HashMap<>();
        int r = clazzService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Clazz entity) {
        Map<String, Object> result = new HashMap<>();
        int r = clazzService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = clazzService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
