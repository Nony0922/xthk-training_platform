package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeVisit {
    private Integer id;
    private Integer studentId;
    private Integer teacherId;
    private String visitDate;
    private Integer visitType;
    private String content;
    private String feedback;
    private String nextPlan;
    private String createTime;
    private String studentName;
    private String teacherName;
}
