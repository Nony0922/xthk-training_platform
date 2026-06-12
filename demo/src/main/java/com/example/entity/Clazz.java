package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clazz {
    private Integer id;
    private String name;
    private String grade;
    private Integer headTeacherId;
    private String room;
    private Integer capacity;
    private String description;
    private Integer status;
    private String createTime;
    private String headTeacherName;
}
