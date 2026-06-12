package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private Integer id;
    private Integer studentId;
    private Integer classId;
    private Integer courseId;
    private String attendDate;
    private Integer status;
    private String remark;
    private Integer recorderId;
    private String createTime;
    private String studentName;
    private String className;
    private String courseName;
}
