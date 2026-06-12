package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private Integer publisherId;
    private String publisherName;
    private String targetRole;
    private Integer status;
    private String publishTime;
    private String createTime;
}
