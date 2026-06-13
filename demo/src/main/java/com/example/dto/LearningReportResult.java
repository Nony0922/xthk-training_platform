package com.example.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class LearningReportResult {
    private Integer id;
    private String question;
    private String sql;
    private String sqlExplanation;
    private List<String> columns = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();
    private List<ChartConfig> charts = new ArrayList<>();
    private String reportTitle;
    private List<ReportSection> sections = new ArrayList<>();
    private String summary;
    private boolean aiEnabled;
    private boolean saved;
    private boolean publishedToParent;
}
