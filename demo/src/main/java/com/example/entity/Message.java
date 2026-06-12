package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer id;
    private Integer parentId;
    private String content;
    private String reply;
    private Integer replierId;
    private Integer status;
    private String createTime;
    private String replyTime;
    private String parentName;
}
