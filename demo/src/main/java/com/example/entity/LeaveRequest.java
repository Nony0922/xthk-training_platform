package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    private Integer id;
    private Integer studentId;
    private Integer applicantId;
    private String applicantName;
    private Integer leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private Integer status;
    private Integer approverId;
    private String approveTime;
    private String remark;
    private String createTime;
    private String studentName;
}
