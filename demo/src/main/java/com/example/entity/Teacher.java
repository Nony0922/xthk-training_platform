package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private Integer id;
    private Integer userId;
    private String name;
    private Integer gender;
    private String phone;
    private String email;
    private Integer teacherLevel;
    private String subject;
    private String title;
    private String hireDate;
    private Integer status;
    private String createTime;
}
