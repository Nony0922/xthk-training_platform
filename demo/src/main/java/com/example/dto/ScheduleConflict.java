package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflict {
    private String type;
    private String severity;
    private String message;
    private List<Integer> scheduleIds;
    private String detail;
}
