package com.example.service;

import com.example.dto.ScheduleAiResult;
import java.util.List;

public interface ScheduleAiService {
    List<String> listSemesters();
    ScheduleAiResult analyze(String semester, boolean includeAi);
}
