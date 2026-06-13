package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningReport {
    private Integer id;
    private String title;
    private String question;
    private String sqlText;
    private String queryResultJson;
    private String chartConfigJson;
    private String reportContentJson;
    private Integer classId;
    private Integer studentId;
    private String creatorRole;
    private Integer creatorId;
    private String creatorName;
    private Integer parentVisible;
    private String createTime;
    private String className;
    private String studentName;
}
