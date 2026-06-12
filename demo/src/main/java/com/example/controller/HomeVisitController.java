package com.example.controller;

import com.example.entity.HomeVisit;
import com.example.service.HomeVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home-visit")
@CrossOrigin
public class HomeVisitController {
    @Autowired
    private HomeVisitService homeVisitService;

    @GetMapping("/list")
    public List<HomeVisit> findAll() { return homeVisitService.findAll(); }

    @GetMapping("/{id}")
    public HomeVisit findById(@PathVariable Integer id) { return homeVisitService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody HomeVisit entity) {
        Map<String, Object> result = new HashMap<>();
        int r = homeVisitService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody HomeVisit entity) {
        Map<String, Object> result = new HashMap<>();
        int r = homeVisitService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = homeVisitService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
