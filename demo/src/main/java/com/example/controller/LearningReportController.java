package com.example.controller;

import com.example.dto.LearningReportQueryRequest;
import com.example.dto.LearningReportResult;
import com.example.entity.LearningReport;
import com.example.service.LearningReportService;
import com.example.service.SparkAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report/ai")
@CrossOrigin
public class LearningReportController {

    @Autowired
    private LearningReportService learningReportService;

    @Autowired
    private SparkAiService sparkAiService;

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", Map.of("aiEnabled", sparkAiService.isAvailable()));
        return result;
    }

    @GetMapping("/presets")
    public Map<String, Object> presets() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", learningReportService.presetQuestions());
        return result;
    }

    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestBody LearningReportQueryRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            LearningReportResult data = learningReportService.analyze(request);
            result.put("code", 200);
            result.put("msg", "分析完成");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String creatorRole,
                                    @RequestParam(required = false) Integer creatorId) {
        Map<String, Object> result = new HashMap<>();
        List<LearningReport> data = learningReportService.listForUser(creatorRole, creatorId);
        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", data);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            LearningReportResult data = learningReportService.getDetail(id, null);
            result.put("code", 200);
            result.put("msg", "success");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = learningReportService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }
}
