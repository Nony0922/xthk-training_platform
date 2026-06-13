package com.example.service;

import com.example.dto.LearningReportQueryRequest;
import com.example.dto.LearningReportResult;
import com.example.entity.LearningReport;

import java.util.List;

public interface LearningReportService {
    LearningReportResult analyze(LearningReportQueryRequest request);

    List<LearningReport> listForUser(String creatorRole, Integer creatorId);

    List<LearningReport> listForParent(Integer parentId);

    LearningReportResult getDetail(Integer id, Integer parentId);

    int deleteById(Integer id);

    List<String> presetQuestions();
}
