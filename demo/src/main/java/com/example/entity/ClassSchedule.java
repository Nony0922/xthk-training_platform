package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSchedule {
    private Integer id;
    private Integer classId;
    private Integer courseId;
    private Integer teacherId;
    private Integer weekday;
    private String startTime;
    private String endTime;
    private String room;
    private String semester;
    private String createTime;
    private String className;
    private String courseName;
    private String teacherName;
}
