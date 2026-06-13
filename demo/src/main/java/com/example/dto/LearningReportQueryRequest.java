package com.example.dto;

import lombok.Data;

@Data
public class LearningReportQueryRequest {
    private String question;
    private Integer classId;
    private Integer studentId;
    private Integer scopeUserId;
    private Integer teacherLevel;
    private String creatorRole;
    private Integer creatorId;
    private String creatorName;
    /** 是否保存报告并推送给家长 */
    private Boolean publishToParent;
}
