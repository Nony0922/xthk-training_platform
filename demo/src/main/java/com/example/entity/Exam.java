package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    private Integer id;
    private String name;
    private Integer courseId;
    private Integer classId;
    private String examDate;
    private String startTime;
    private String endTime;
    private String location;
    private BigDecimal totalScore;
    private Integer status;
    private String remark;
    private String createTime;
    private String courseName;
    private String className;
}
