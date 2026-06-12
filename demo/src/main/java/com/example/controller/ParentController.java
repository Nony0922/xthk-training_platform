package com.example.controller;

import com.example.entity.Parent;
import com.example.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parent")
@CrossOrigin
public class ParentController {
    @Autowired
    private ParentService parentService;

    @GetMapping("/list")
    public List<Parent> findAll() { return parentService.findAll(); }

    @GetMapping("/by-user/{userId}")
    public Parent findByUserId(@PathVariable Integer userId) {
        return parentService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public Parent findById(@PathVariable Integer id) { return parentService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Parent entity) {
        Map<String, Object> result = new HashMap<>();
        int r = parentService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Parent entity) {
        Map<String, Object> result = new HashMap<>();
        int r = parentService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = parentService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
