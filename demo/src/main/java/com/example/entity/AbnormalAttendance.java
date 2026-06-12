package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbnormalAttendance {
    private Integer id;
    private Integer attendanceId;
    private Integer studentId;
    private Integer abnormalType;
    private String description;
    private Integer handleStatus;
    private String handleResult;
    private Integer handlerId;
    private String handleTime;
    private String createTime;
    private String studentName;
}
