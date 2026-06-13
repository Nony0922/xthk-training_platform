package com.example.util;

import com.example.entity.Exam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ExamStatusUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ExamStatusUtil() {}

    /**
     * 根据考试日期与时间自动判定状态：0未开始 1进行中 2已结束
     */
    public static int resolveStatus(Exam exam) {
        if (exam == null || exam.getExamDate() == null || exam.getExamDate().isBlank()) {
            return exam != null && exam.getStatus() != null ? exam.getStatus() : 0;
        }
        LocalDate date = LocalDate.parse(exam.getExamDate().substring(0, 10), DATE_FMT);
        LocalTime start = parseTime(exam.getStartTime(), LocalTime.MIN);
        LocalTime end = parseTime(exam.getEndTime(), LocalTime.of(23, 59, 59));
        LocalDateTime startAt = LocalDateTime.of(date, start);
        LocalDateTime endAt = LocalDateTime.of(date, end);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startAt)) {
            return 0;
        }
        if (now.isAfter(endAt)) {
            return 2;
        }
        return 1;
    }

    public static void applyResolvedStatus(Exam exam) {
        if (exam != null) {
            exam.setStatus(resolveStatus(exam));
        }
    }

    private static LocalTime parseTime(String value, LocalTime fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String time = value.trim();
        if (time.length() == 5) {
            time = time + ":00";
        } else if (time.length() > 8) {
            time = time.substring(0, 8);
        }
        return LocalTime.parse(time, TIME_FMT);
    }
}
