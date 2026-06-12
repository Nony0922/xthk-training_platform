package com.example.controller;

import com.example.entity.LeaveRequest;
import com.example.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
@CrossOrigin
public class LeaveRequestController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping("/list")
    public List<LeaveRequest> findAll(@RequestParam(required = false) Integer scopeUserId,
                                      @RequestParam(required = false) Integer teacherLevel) {
        if (scopeUserId != null && teacherLevel != null) {
            return leaveRequestService.findAllForTeacher(scopeUserId, teacherLevel);
        }
        return leaveRequestService.findAll();
    }

    @GetMapping("/{id}")
    public LeaveRequest findById(@PathVariable Integer id) { return leaveRequestService.findById(id); }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody LeaveRequest entity) {
        Map<String, Object> result = new HashMap<>();
        int r = leaveRequestService.insert(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "添加成功" : "添加失败");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody LeaveRequest entity) {
        Map<String, Object> result = new HashMap<>();
        int r = leaveRequestService.update(entity);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = leaveRequestService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
