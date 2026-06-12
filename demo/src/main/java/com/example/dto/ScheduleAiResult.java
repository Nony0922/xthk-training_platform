package com.example.dto;

import com.example.entity.ClassSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAiResult {
    private String semester;
    private List<ClassSchedule> schedules;
    private List<ScheduleConflict> conflicts;
    private List<ScheduleSuggestion> suggestions;
    private String aiSummary;
    private boolean aiEnabled;
    private Map<Integer, Integer> classStudentCounts;
    private Map<Integer, Integer> classCapacities;
    private Map<String, Integer> roomCapacities;
}
