package com.example.controller;

import com.example.entity.Score;
import com.example.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/score")
@CrossOrigin
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @GetMapping("/list")
    public List<Score> findAll() { return scoreService.findAll(); }

    @GetMapping("/{id}")
    public Score findById(@PathVariable Integer id) { return scoreService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Score entity) {
        Map<String, Object> result = new HashMap<>();
        int r = scoreService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Score entity) {
        Map<String, Object> result = new HashMap<>();
        int r = scoreService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = scoreService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
