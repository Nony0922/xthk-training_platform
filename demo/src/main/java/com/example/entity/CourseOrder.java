package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOrder {
    private Integer id;
    private String orderNo;
    private Integer parentId;
    private Integer courseId;
    private String courseName;
    private String teacherName;
    private Integer hours;
    private BigDecimal fee;
    private Integer status;
    private String payTime;
    private String createTime;
    private String parentName;
}
