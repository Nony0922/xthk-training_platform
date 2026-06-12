package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSuggestion {
    private Integer scheduleId;
    private String action;
    private Integer newWeekday;
    private String newStartTime;
    private String newEndTime;
    private String newRoom;
    private Integer newTeacherId;
    private String reason;
    private String source;
}
