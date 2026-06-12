package com.example.controller;

import com.example.entity.Attendance;
import com.example.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@CrossOrigin
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list")
    public List<Attendance> findAll(@RequestParam(required = false) Integer scopeUserId,
                                    @RequestParam(required = false) Integer teacherLevel) {
        if (scopeUserId != null && teacherLevel != null) {
            return attendanceService.findAllForTeacher(scopeUserId, teacherLevel);
        }
        return attendanceService.findAll();
    }

    @GetMapping("/{id}")
    public Attendance findById(@PathVariable Integer id) { return attendanceService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody Attendance entity) {
        Map<String, Object> result = new HashMap<>();
        int r = attendanceService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Attendance entity) {
        Map<String, Object> result = new HashMap<>();
        int r = attendanceService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = attendanceService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
