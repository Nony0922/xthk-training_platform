package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parent {
    private Integer id;
    private Integer userId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String createTime;
}
