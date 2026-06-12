package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id;
    private String name;
    private Integer gender;
    private String birthday;
    private String phone;
    private Integer classId;
    private Integer parentId;
    private String enrollDate;
    private Integer status;
    private String createTime;
    private String className;
    private String parentName;
}
