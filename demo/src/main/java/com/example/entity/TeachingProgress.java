package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingProgress {
    private Integer id;
    private Integer classId;
    private Integer courseId;
    private Integer teacherId;
    private String chapter;
    private String content;
    private String plannedDate;
    private String actualDate;
    private Integer status;
    private String remark;
    private String createTime;
    private String className;
    private String courseName;
    private String teacherName;
}
