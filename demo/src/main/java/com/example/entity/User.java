package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String role;
    private Integer teacherLevel;
    private String phone;
    private String email;
    private Integer status;
    private String createTime;
    /** 家长登录时关联的家长表ID（非数据库字段） */
    private Integer parentId;
}
