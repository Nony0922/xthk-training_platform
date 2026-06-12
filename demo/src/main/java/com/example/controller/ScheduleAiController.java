package com.example.controller;

import com.example.dto.ScheduleAiResult;
import com.example.service.ScheduleAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule/ai")
@CrossOrigin
public class ScheduleAiController {

    @Autowired
    private ScheduleAiService scheduleAiService;

    @GetMapping("/semesters")
    public Map<String, Object> semesters() {
        Map<String, Object> result = new HashMap<>();
        List<String> list = scheduleAiService.listSemesters();
        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", list);
        return result;
    }

    @GetMapping("/analyze")
    public Map<String, Object> analyze(@RequestParam(required = false) String semester,
                                       @RequestParam(defaultValue = "false") boolean includeAi) {
        Map<String, Object> result = new HashMap<>();
        try {
            ScheduleAiResult data = scheduleAiService.analyze(semester, includeAi);
            result.put("code", 200);
            result.put("msg", "分析完成");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
