package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer id;
    private String name;
    private String description;
    private Integer teacherId;
    private Integer hours;
    private BigDecimal fee;
    private String targetGrade;
    private String subject;
    private Integer teachMode;
    private String location;
    private String validStart;
    private String validEnd;
    private String classTimeDesc;
    private Integer maxStudents;
    private Integer enrolledCount;
    private String suitableAge;
    private String highlights;
    private Integer status;
    private String createTime;
    private String teacherName;
}
