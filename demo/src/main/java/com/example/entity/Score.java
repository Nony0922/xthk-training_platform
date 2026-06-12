package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    private Integer id;
    private Integer examId;
    private Integer studentId;
    private BigDecimal score;
    private Integer rankNum;
    private String remark;
    private String createTime;
    private String examName;
    private String studentName;
}
