package com.example.controller;

import com.example.entity.ClassSchedule;
import com.example.service.ClassScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@CrossOrigin
public class ClassScheduleController {
    @Autowired
    private ClassScheduleService classScheduleService;

    @GetMapping("/list")
    public List<ClassSchedule> findAll() { return classScheduleService.findAll(); }

    @GetMapping("/{id:\\d+}")
    public ClassSchedule findById(@PathVariable Integer id) { return classScheduleService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody ClassSchedule entity) {
        Map<String, Object> result = new HashMap<>();
        int r = classScheduleService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody ClassSchedule entity) {
        Map<String, Object> result = new HashMap<>();
        int r = classScheduleService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id:\\d+}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = classScheduleService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
