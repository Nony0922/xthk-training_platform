package com.example.service.impl;

import com.example.config.SparkProperties;
import com.example.dto.ScheduleAiResult;
import com.example.dto.ScheduleConflict;
import com.example.dto.ScheduleSuggestion;
import com.example.entity.ClassSchedule;
import com.example.entity.Clazz;
import com.example.mapper.ClassScheduleMapper;
import com.example.mapper.ClazzMapper;
import com.example.mapper.StudentMapper;
import com.example.service.ScheduleAiService;
import com.example.service.SparkAiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ScheduleAiServiceImpl implements ScheduleAiService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String[] WEEKDAYS = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Autowired
    private ClassScheduleMapper classScheduleMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private SparkAiService sparkAiService;
    @Autowired
    private SparkProperties sparkProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> listSemesters() {
        List<String> semesters = classScheduleMapper.findSemesters();
        return semesters == null ? List.of() : semesters;
    }

    @Override
    public ScheduleAiResult analyze(String semester, boolean includeAi) {
        String targetSemester = StringUtils.hasText(semester) ? semester : null;
        List<ClassSchedule> schedules = targetSemester == null
                ? classScheduleMapper.findAll()
                : classScheduleMapper.findBySemester(targetSemester);

        if (schedules == null) {
            schedules = List.of();
        }
        if (targetSemester == null && !schedules.isEmpty()) {
            targetSemester = schedules.get(0).getSemester();
        }

        Map<Integer, Integer> classStudentCounts = buildClassStudentCounts();
        Map<Integer, Integer> classCapacities = buildClassCapacities();
        Map<String, Integer> roomCapacities = buildRoomCapacities();

        List<ScheduleConflict> conflicts = detectConflicts(schedules, classStudentCounts, classCapacities, roomCapacities);
        List<ScheduleSuggestion> suggestions = buildRuleSuggestions(conflicts, schedules);

        String aiSummary = buildDefaultSummary(conflicts, includeAi);

        if (includeAi && sparkAiService.isAvailable()) {
            try {
                AiParsedResult aiParsed = requestAiSuggestionsWithTimeout(
                        targetSemester, schedules, conflicts, classStudentCounts, classCapacities, roomCapacities);
                if (StringUtils.hasText(aiParsed.summary)) {
                    aiSummary = aiParsed.summary;
                }
                if (!aiParsed.suggestions.isEmpty()) {
                    suggestions = mergeSuggestions(suggestions, aiParsed.suggestions);
                }
            } catch (TimeoutException e) {
                aiSummary = "规则引擎已完成冲突检测。星火大模型响应超时（" + sparkProperties.getTimeoutSeconds()
                        + "秒），请稍后重试或检查网络。";
            } catch (Exception e) {
                aiSummary = "规则引擎已完成冲突检测。大模型建议获取失败：" + e.getMessage();
            }
        }

        ScheduleAiResult result = new ScheduleAiResult();
        result.setSemester(targetSemester);
        result.setSchedules(schedules);
        result.setConflicts(conflicts);
        result.setSuggestions(suggestions);
        result.setAiSummary(aiSummary);
        result.setAiEnabled(sparkAiService.isAvailable());
        result.setClassStudentCounts(classStudentCounts);
        result.setClassCapacities(classCapacities);
        result.setRoomCapacities(roomCapacities);
        return result;
    }

    private String buildDefaultSummary(List<ScheduleConflict> conflicts, boolean includeAi) {
        if (!conflicts.isEmpty()) {
            if (includeAi && !sparkAiService.isAvailable()) {
                return "已检测到 " + conflicts.size() + " 项冲突。请在 application-local.yaml 配置 spark.api-password 后点击「AI 深度分析」。";
            }
            return "已检测到 " + conflicts.size() + " 项冲突，请查看右侧详情与优化建议。";
        }
        if (includeAi && !sparkAiService.isAvailable()) {
            return "当前课表未发现冲突。（未配置星火 API，仅使用规则检测）";
        }
        return "当前课表未发现冲突。" + (includeAi ? " 星火大模型未检测到额外风险。" : " 可点击「AI 深度分析」获取大模型建议。");
    }

    private AiParsedResult requestAiSuggestionsWithTimeout(String semester,
                                                             List<ClassSchedule> schedules,
                                                             List<ScheduleConflict> conflicts,
                                                             Map<Integer, Integer> studentCounts,
                                                             Map<Integer, Integer> classCapacities,
                                                             Map<String, Integer> roomCapacities) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<AiParsedResult> future = executor.submit(() -> requestAiSuggestions(
                    semester, schedules, conflicts, studentCounts, classCapacities, roomCapacities));
            return future.get(sparkProperties.getTimeoutSeconds(), TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    private Map<Integer, Integer> buildClassStudentCounts() {
        List<Map<String, Object>> rows = studentMapper.countByClassId();
        Map<Integer, Integer> map = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Integer classId = toInt(row.get("classId"));
                Integer count = toInt(row.get("count"));
                if (classId != null && count != null) {
                    map.put(classId, count);
                }
            }
        }
        return map;
    }

    private Map<Integer, Integer> buildClassCapacities() {
        Map<Integer, Integer> map = new HashMap<>();
        List<Clazz> classes = clazzMapper.findAll();
        if (classes != null) {
            for (Clazz clazz : classes) {
                map.put(clazz.getId(), clazz.getCapacity() != null ? clazz.getCapacity() : 30);
            }
        }
        return map;
    }

    private Map<String, Integer> buildRoomCapacities() {
        Map<String, Integer> map = new HashMap<>();
        List<Clazz> classes = clazzMapper.findAll();
        if (classes != null) {
            for (Clazz clazz : classes) {
                if (StringUtils.hasText(clazz.getRoom())) {
                    int cap = clazz.getCapacity() != null ? clazz.getCapacity() : 30;
                    map.merge(clazz.getRoom(), cap, Math::max);
                }
            }
        }
        map.putIfAbsent("A101", 30);
        map.putIfAbsent("B203", 25);
        map.putIfAbsent("C305", 20);
        return map;
    }

    private List<ScheduleConflict> detectConflicts(List<ClassSchedule> schedules,
                                                     Map<Integer, Integer> studentCounts,
                                                     Map<Integer, Integer> classCapacities,
                                                     Map<String, Integer> roomCapacities) {
        List<ScheduleConflict> conflicts = new ArrayList<>();

        for (int i = 0; i < schedules.size(); i++) {
            ClassSchedule a = schedules.get(i);
            for (int j = i + 1; j < schedules.size(); j++) {
                ClassSchedule b = schedules.get(j);
                if (!Objects.equals(a.getWeekday(), b.getWeekday())) continue;
                if (!isTimeOverlap(a.getStartTime(), a.getEndTime(), b.getStartTime(), b.getEndTime())) continue;

                if (Objects.equals(a.getTeacherId(), b.getTeacherId())) {
                    conflicts.add(new ScheduleConflict(
                            "teacher", "high",
                            "教师时间冲突：" + safe(a.getTeacherName()) + " 在 " + weekdayText(a.getWeekday())
                                    + " " + formatTimeRange(a) + " 与 " + formatTimeRange(b) + " 课程重叠",
                            List.of(a.getId(), b.getId()),
                            safe(a.getCourseName()) + " vs " + safe(b.getCourseName())
                    ));
                }

                if (StringUtils.hasText(a.getRoom()) && a.getRoom().equals(b.getRoom())) {
                    conflicts.add(new ScheduleConflict(
                            "room", "high",
                            "教室占用冲突：" + a.getRoom() + " 在 " + weekdayText(a.getWeekday())
                                    + " 同时安排了 " + safe(a.getClassName()) + " 与 " + safe(b.getClassName()),
                            List.of(a.getId(), b.getId()),
                            formatTimeRange(a) + " / " + formatTimeRange(b)
                    ));
                }
            }
        }

        for (ClassSchedule schedule : schedules) {
            Integer classId = schedule.getClassId();
            if (classId == null) continue;

            int students = studentCounts.getOrDefault(classId, 0);
            int classCap = classCapacities.getOrDefault(classId, 30);
            if (students > classCap) {
                conflicts.add(new ScheduleConflict(
                        "capacity", "high",
                        "班级超员：" + safe(schedule.getClassName()) + " 现有 " + students
                                + " 名学生，超过班级容量 " + classCap,
                        List.of(schedule.getId()),
                        safe(schedule.getCourseName()) + " · " + weekdayText(schedule.getWeekday())
                ));
            }

            if (StringUtils.hasText(schedule.getRoom())) {
                int roomCap = roomCapacities.getOrDefault(schedule.getRoom(), classCap);
                if (students > roomCap) {
                    conflicts.add(new ScheduleConflict(
                            "capacity", "medium",
                            "教室容量不足：" + schedule.getRoom() + " 容量 " + roomCap
                                    + "，" + safe(schedule.getClassName()) + " 实际 " + students + " 人",
                            List.of(schedule.getId()),
                            formatTimeRange(schedule)
                    ));
                }
            }
        }

        return dedupeConflicts(conflicts);
    }

    private List<ScheduleConflict> dedupeConflicts(List<ScheduleConflict> conflicts) {
        LinkedHashMap<String, ScheduleConflict> map = new LinkedHashMap<>();
        for (ScheduleConflict c : conflicts) {
            String key = c.getType() + "|" + c.getScheduleIds().stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
            map.putIfAbsent(key, c);
        }
        return new ArrayList<>(map.values());
    }

    private List<ScheduleSuggestion> buildRuleSuggestions(List<ScheduleConflict> conflicts, List<ClassSchedule> schedules) {
        List<ScheduleSuggestion> suggestions = new ArrayList<>();
        Map<Integer, ClassSchedule> scheduleMap = schedules.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(ClassSchedule::getId, s -> s, (a, b) -> a));

        for (ScheduleConflict conflict : conflicts) {
            if (conflict.getScheduleIds() == null || conflict.getScheduleIds().isEmpty()) continue;
            Integer scheduleId = conflict.getScheduleIds().get(0);
            ClassSchedule schedule = scheduleMap.get(scheduleId);
            if (schedule == null) continue;

            if ("teacher".equals(conflict.getType()) || "room".equals(conflict.getType())) {
                int altWeekday = schedule.getWeekday() == 5 ? 2 : schedule.getWeekday() + 1;
                suggestions.add(new ScheduleSuggestion(
                        scheduleId, "move", altWeekday,
                        schedule.getStartTime(), schedule.getEndTime(),
                        schedule.getRoom(), null,
                        "规则建议：将课程调整至" + weekdayText(altWeekday) + "同一时段，避开当前冲突",
                        "rule"
                ));
            } else if ("capacity".equals(conflict.getType())) {
                suggestions.add(new ScheduleSuggestion(
                        scheduleId, "change_room", schedule.getWeekday(),
                        schedule.getStartTime(), schedule.getEndTime(),
                        "D401", null,
                        "规则建议：更换至更大容量教室，或拆班上课",
                        "rule"
                ));
            }
        }
        return suggestions;
    }

    private AiParsedResult requestAiSuggestions(String semester,
                                                List<ClassSchedule> schedules,
                                                List<ScheduleConflict> conflicts,
                                                Map<Integer, Integer> studentCounts,
                                                Map<Integer, Integer> classCapacities,
                                                Map<String, Integer> roomCapacities) throws Exception {
        String systemPrompt = """
                你是培训机构教务排课专家。请根据课表数据、教师占用、教室容量、班级人数和已检测冲突，给出排课优化建议。
                必须严格返回 JSON，不要输出 markdown 代码块，格式如下：
                {
                  "summary": "100字以内的综合分析",
                  "suggestions": [
                    {
                      "scheduleId": 1,
                      "action": "move",
                      "newWeekday": 3,
                      "newStartTime": "09:00:00",
                      "newEndTime": "10:30:00",
                      "newRoom": "B203",
                      "newTeacherId": null,
                      "reason": "调整原因"
                    }
                  ]
                }
                action 取值：move / change_room / change_teacher。
                newWeekday 为 1-7（周一到周日）。
                """;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("semester", semester);
        payload.put("schedules", schedules.stream().map(s -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", s.getId());
            item.put("className", s.getClassName());
            item.put("courseName", s.getCourseName());
            item.put("teacherName", s.getTeacherName());
            item.put("weekday", s.getWeekday());
            item.put("time", shortTime(s.getStartTime()) + "-" + shortTime(s.getEndTime()));
            item.put("room", s.getRoom());
            return item;
        }).toList());
        payload.put("conflicts", conflicts);
        payload.put("classStudentCounts", studentCounts);
        payload.put("classCapacities", classCapacities);
        payload.put("roomCapacities", roomCapacities);

        String userPrompt = "请分析以下排课数据并返回 JSON：\n" + objectMapper.writeValueAsString(payload);
        String aiText = sparkAiService.chat(systemPrompt, userPrompt);
        return parseAiResponse(aiText);
    }

    private AiParsedResult parseAiResponse(String aiText) throws Exception {
        AiParsedResult result = new AiParsedResult();
        String json = extractJson(aiText);
        JsonNode root = objectMapper.readTree(json);
        result.summary = root.path("summary").asText("");
        JsonNode suggestionsNode = root.path("suggestions");
        if (suggestionsNode.isArray()) {
            List<ScheduleSuggestion> list = objectMapper.convertValue(
                    suggestionsNode, new TypeReference<List<ScheduleSuggestion>>() {});
            for (ScheduleSuggestion s : list) {
                s.setSource("ai");
            }
            result.suggestions = list;
        }
        return result;
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private List<ScheduleSuggestion> mergeSuggestions(List<ScheduleSuggestion> ruleSuggestions,
                                                        List<ScheduleSuggestion> aiSuggestions) {
        LinkedHashMap<Integer, ScheduleSuggestion> map = new LinkedHashMap<>();
        for (ScheduleSuggestion s : ruleSuggestions) {
            if (s.getScheduleId() != null) {
                map.put(s.getScheduleId(), s);
            }
        }
        for (ScheduleSuggestion s : aiSuggestions) {
            if (s.getScheduleId() != null) {
                map.put(s.getScheduleId(), s);
            }
        }
        return new ArrayList<>(map.values());
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        try {
            LocalTime s1 = LocalTime.parse(normalizeTime(start1), TIME_FMT);
            LocalTime e1 = LocalTime.parse(normalizeTime(end1), TIME_FMT);
            LocalTime s2 = LocalTime.parse(normalizeTime(start2), TIME_FMT);
            LocalTime e2 = LocalTime.parse(normalizeTime(end2), TIME_FMT);
            return s1.isBefore(e2) && s2.isBefore(e1);
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeTime(String time) {
        if (!StringUtils.hasText(time)) return "00:00:00";
        return time.length() == 5 ? time + ":00" : time;
    }

    private String weekdayText(Integer weekday) {
        if (weekday == null || weekday < 1 || weekday > 7) return "未知";
        return WEEKDAYS[weekday];
    }

    private String formatTimeRange(ClassSchedule schedule) {
        return weekdayText(schedule.getWeekday()) + " "
                + shortTime(schedule.getStartTime()) + "-" + shortTime(schedule.getEndTime());
    }

    private String shortTime(String time) {
        if (!StringUtils.hasText(time)) return "--:--";
        return time.length() >= 5 ? time.substring(0, 5) : time;
    }

    private String safe(String value) {
        return StringUtils.hasText(value) ? value : "-";
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static class AiParsedResult {
        private String summary = "";
        private List<ScheduleSuggestion> suggestions = new ArrayList<>();
    }
}
