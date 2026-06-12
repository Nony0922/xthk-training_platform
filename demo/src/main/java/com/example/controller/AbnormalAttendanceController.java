package com.example.controller;

import com.example.entity.AbnormalAttendance;
import com.example.service.AbnormalAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/abnormal-attendance")
@CrossOrigin
public class AbnormalAttendanceController {
    @Autowired
    private AbnormalAttendanceService abnormalAttendanceService;

    @GetMapping("/list")
    public List<AbnormalAttendance> findAll() { return abnormalAttendanceService.findAll(); }

    @GetMapping("/{id}")
    public AbnormalAttendance findById(@PathVariable Integer id) { return abnormalAttendanceService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody AbnormalAttendance entity) {
        Map<String, Object> result = new HashMap<>();
        int r = abnormalAttendanceService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody AbnormalAttendance entity) {
        Map<String, Object> result = new HashMap<>();
        int r = abnormalAttendanceService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = abnormalAttendanceService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
