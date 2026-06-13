package com.example.service.impl;

import com.example.config.SparkProperties;
import com.example.dto.*;
import com.example.entity.LearningReport;
import com.example.mapper.LearningReportMapper;
import com.example.service.LearningReportService;
import com.example.service.SparkAiService;
import com.example.service.TeacherScopeService;
import com.example.util.SchemaProvider;
import com.example.util.SqlSafetyValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class LearningReportServiceImpl implements LearningReportService {

    private static final List<String> PRESET_QUESTIONS = List.of(
            "统计各班级学生人数",
            "统计一年级1班各考勤状态的人数分布",
            "查询各学生最近考试成绩及排名",
            "统计各班级平均考试成绩",
            "查询缺勤次数最多的学生",
            "统计各课程关联的考勤正常率"
    );

    @Autowired
    private SparkAiService sparkAiService;
    @Autowired
    private SparkProperties sparkProperties;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LearningReportMapper learningReportMapper;
    @Autowired
    private TeacherScopeService teacherScopeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LearningReportResult analyze(LearningReportQueryRequest request) {
        if (!StringUtils.hasText(request.getQuestion())) {
            throw new IllegalArgumentException("请输入分析问题");
        }

        ScopeContext scope = buildScopeContext(request);
        LearningReportResult result = new LearningReportResult();
        result.setQuestion(request.getQuestion().trim());
        result.setAiEnabled(sparkAiService.isAvailable());

        String sql;
        String sqlExplanation;
        if (sparkAiService.isAvailable()) {
            try {
                SqlGeneration sqlGen = generateSqlWithAi(request.getQuestion(), scope);
                sql = sqlGen.sql;
                sqlExplanation = sqlGen.explanation;
            } catch (Exception e) {
                sql = buildFallbackSql(request.getQuestion(), scope);
                sqlExplanation = "星火 SQL 生成失败，已使用预置模板：" + e.getMessage();
            }
        } else {
            sql = buildFallbackSql(request.getQuestion(), scope);
            sqlExplanation = "未配置星火 API，使用预置查询模板";
        }

        sql = SqlSafetyValidator.validateAndNormalize(sql);
        result.setSql(sql);
        result.setSqlExplanation(sqlExplanation);

        QueryData queryData = executeQuery(sql);
        result.setColumns(queryData.columns);
        result.setRows(queryData.rows);

        if (sparkAiService.isAvailable()) {
            try {
                applyInsightsWithAi(result, queryData);
            } catch (Exception e) {
                applyFallbackVisualization(result, queryData);
                applyFallbackReport(result, queryData, e.getMessage());
            }
        } else {
            applyFallbackVisualization(result, queryData);
            applyFallbackReport(result, queryData, null);
        }
        ensureCharts(result, queryData);

        if (Boolean.TRUE.equals(request.getPublishToParent())) {
            saveReport(request, result, 1);
            result.setSaved(true);
            result.setPublishedToParent(true);
        } else {
            saveReport(request, result, 0);
            result.setSaved(true);
            result.setPublishedToParent(false);
        }

        return result;
    }

    @Override
    public List<LearningReport> listForUser(String creatorRole, Integer creatorId) {
        if ("admin".equals(creatorRole)) {
            return learningReportMapper.findAll();
        }
        if (creatorId == null) {
            return List.of();
        }
        return learningReportMapper.findByCreator(creatorRole, creatorId);
    }

    @Override
    public List<LearningReport> listForParent(Integer parentId) {
        return learningReportMapper.findVisibleForParent(parentId);
    }

    @Override
    public LearningReportResult getDetail(Integer id, Integer parentId) {
        LearningReport report = learningReportMapper.findById(id);
        if (report == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        if (parentId != null) {
            if (report.getParentVisible() == null || report.getParentVisible() != 1) {
                throw new IllegalArgumentException("无权查看该报告");
            }
            List<LearningReport> allowed = learningReportMapper.findVisibleForParent(parentId);
            boolean ok = allowed.stream().anyMatch(r -> Objects.equals(r.getId(), id));
            if (!ok) {
                throw new IllegalArgumentException("无权查看该报告");
            }
        }
        return toResult(report);
    }

    @Override
    public int deleteById(Integer id) {
        return learningReportMapper.deleteById(id);
    }

    @Override
    public List<String> presetQuestions() {
        return PRESET_QUESTIONS;
    }

    private void saveReport(LearningReportQueryRequest request, LearningReportResult result, int parentVisible) {
        try {
            LearningReport entity = new LearningReport();
            entity.setTitle(StringUtils.hasText(result.getReportTitle()) ? result.getReportTitle() : "学情分析报告");
            entity.setQuestion(result.getQuestion());
            entity.setSqlText(result.getSql());
            entity.setQueryResultJson(objectMapper.writeValueAsString(buildQueryJson(result)));
            entity.setChartConfigJson(objectMapper.writeValueAsString(
                    result.getCharts() != null ? result.getCharts() : List.of()));
            entity.setReportContentJson(objectMapper.writeValueAsString(buildReportJson(result)));
            entity.setClassId(request.getClassId());
            entity.setStudentId(request.getStudentId());
            entity.setCreatorRole(request.getCreatorRole());
            entity.setCreatorId(request.getCreatorId());
            entity.setCreatorName(request.getCreatorName());
            entity.setParentVisible(parentVisible);
            learningReportMapper.insert(entity);
            result.setId(entity.getId());
        } catch (Exception e) {
            throw new RuntimeException("保存报告失败: " + e.getMessage(), e);
        }
    }

    private LearningReportResult toResult(LearningReport report) {
        LearningReportResult result = new LearningReportResult();
        result.setId(report.getId());
        result.setQuestion(report.getQuestion());
        result.setSql(report.getSqlText());
        result.setSaved(true);
        result.setPublishedToParent(report.getParentVisible() != null && report.getParentVisible() == 1);
        try {
            if (StringUtils.hasText(report.getQueryResultJson())) {
                JsonNode node = objectMapper.readTree(report.getQueryResultJson());
                result.setColumns(objectMapper.convertValue(node.path("columns"), new TypeReference<List<String>>() {}));
                result.setRows(objectMapper.convertValue(node.path("rows"), new TypeReference<List<List<Object>>>() {}));
            }
            if (StringUtils.hasText(report.getChartConfigJson())) {
                result.setCharts(objectMapper.readValue(report.getChartConfigJson(), new TypeReference<List<ChartConfig>>() {}));
            }
            if (StringUtils.hasText(report.getReportContentJson())) {
                JsonNode node = objectMapper.readTree(report.getReportContentJson());
                result.setReportTitle(node.path("title").asText(""));
                result.setSummary(node.path("summary").asText(""));
                result.setSections(objectMapper.convertValue(node.path("sections"), new TypeReference<List<ReportSection>>() {}));
            }
        } catch (Exception e) {
            throw new RuntimeException("解析报告数据失败: " + e.getMessage(), e);
        }
        return result;
    }

    private ScopeContext buildScopeContext(LearningReportQueryRequest request) {
        ScopeContext ctx = new ScopeContext();
        ctx.classId = request.getClassId();
        ctx.studentId = request.getStudentId();

        if ("teacher".equals(request.getCreatorRole()) && request.getScopeUserId() != null) {
            ctx.allowedClassIds = teacherScopeService.resolveClassIds(
                    request.getScopeUserId(), request.getTeacherLevel());
            ctx.teacherId = teacherScopeService.resolveTeacherId(request.getScopeUserId());
        }
        return ctx;
    }

    private SqlGeneration generateSqlWithAi(String question, ScopeContext scope) throws Exception {
        String systemPrompt = """
                你是培训机构学情分析 SQL 专家。根据用户自然语言问题和数据库 Schema 生成 MySQL 查询语句。
                规则：
                1. 只能生成单条 SELECT 语句，不要 markdown 代码块
                2. 必须使用真实表名和字段名
                3. 考勤 status：1正常 2迟到 3早退 4缺勤 5请假
                4. 尽量使用 JOIN 获取学生名、班级名等可读字段
                5. 结果集建议不超过 500 行，需要时使用 LIMIT
                6. 必须严格返回 JSON：{"sql":"SELECT ...","explanation":"简要说明"}
                """ + "\n\n" + SchemaProvider.schemaDescription();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("question", question);
        payload.put("scopeHint", scope.toHint());

        String userPrompt = "请生成 SQL：\n" + toJson(payload);
        String aiText = callSparkWithTimeout(systemPrompt, userPrompt);
        return parseSqlGeneration(aiText);
    }

    private void applyInsightsWithAi(LearningReportResult result, QueryData queryData) throws Exception {
        String systemPrompt = """
                你是学情分析与数据可视化专家。根据 SQL 查询结果，同时生成图表配置和学情分析报告。
                不要 markdown 代码块，严格返回 JSON：
                {
                  "title": "报告标题",
                  "summary": "200字以内总体概述",
                  "sections": [
                    {"heading":"小节标题","content":"分析内容，含具体数据"}
                  ],
                  "charts": [
                    {
                      "type": "bar|pie|line",
                      "title": "图表标题",
                      "xAxis": ["标签1","标签2"],
                      "series": [{"name":"系列名","data":[1,2]}]
                    }
                  ]
                }
                规则：分类对比用 bar，占比用 pie，时间趋势用 line；sections 2-4 个小节；数据不适合图表时 charts 可为空数组。
                报告 summary 和 sections 的 content 必须使用自然语言，面向教师和家长，不要出现 SQL 语句、表名、字段名或 SELECT 等数据库术语。
                """;
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("question", result.getQuestion());
        payload.put("columns", queryData.columns);
        payload.put("rows", queryData.previewRows(30));
        payload.put("rowCount", queryData.rows.size());
        String aiText = callSparkWithTimeout(systemPrompt, "请分析以下学情数据：\n" + toJson(payload));
        JsonNode root = objectMapper.readTree(extractJson(aiText));
        result.setReportTitle(root.path("title").asText("学情分析报告"));
        result.setSummary(root.path("summary").asText(""));
        JsonNode sectionsNode = root.path("sections");
        if (sectionsNode.isArray()) {
            result.setSections(objectMapper.convertValue(sectionsNode, new TypeReference<List<ReportSection>>() {}));
        }
        JsonNode chartsNode = root.path("charts");
        if (chartsNode.isArray()) {
            try {
                result.setCharts(objectMapper.convertValue(chartsNode, new TypeReference<List<ChartConfig>>() {}));
            } catch (Exception ignored) {
                result.setCharts(new ArrayList<>());
            }
        }
    }

    private void ensureCharts(LearningReportResult result, QueryData queryData) {
        if ((result.getCharts() == null || result.getCharts().isEmpty()) && !queryData.rows.isEmpty()) {
            applyFallbackVisualization(result, queryData);
        }
    }

    private Map<String, Object> buildQueryJson(LearningReportResult result) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("columns", result.getColumns() != null ? result.getColumns() : List.of());
        map.put("rows", result.getRows() != null ? result.getRows() : List.of());
        return map;
    }

    private Map<String, Object> buildReportJson(LearningReportResult result) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("title", StringUtils.hasText(result.getReportTitle()) ? result.getReportTitle() : "学情分析报告");
        map.put("summary", result.getSummary() != null ? result.getSummary() : "");
        map.put("sections", result.getSections() != null ? result.getSections() : List.of());
        return map;
    }

    private void applyFallbackVisualization(LearningReportResult result, QueryData queryData) {
        if (queryData.columns.isEmpty() || queryData.rows.isEmpty()) {
            result.setCharts(List.of());
            return;
        }
        int labelIdx = 0;
        int valueIdx = findNumericColumnIndex(queryData);
        ChartConfig chart = new ChartConfig();
        chart.setType(queryData.rows.size() <= 6 ? "pie" : "bar");
        chart.setTitle(StringUtils.hasText(result.getReportTitle()) ? result.getReportTitle() : "查询结果可视化");
        List<String> labels = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (List<Object> row : queryData.rows) {
            if (labels.size() >= 20) break;
            labels.add(String.valueOf(row.get(labelIdx)));
            values.add(toNumber(row.get(valueIdx)));
        }
        chart.setXAxis(labels);
        ChartSeries series = new ChartSeries();
        series.setName(queryData.columns.get(valueIdx));
        series.setData(values);
        chart.setSeries(List.of(series));
        result.setCharts(List.of(chart));
    }

    private int findNumericColumnIndex(QueryData queryData) {
        if (queryData.rows.isEmpty()) {
            return queryData.columns.size() > 1 ? 1 : 0;
        }
        for (int col = queryData.columns.size() - 1; col >= 0; col--) {
            final int idx = col;
            boolean numeric = queryData.rows.stream()
                    .limit(Math.min(5, queryData.rows.size()))
                    .allMatch(row -> row.size() > idx && row.get(idx) instanceof Number);
            if (numeric) {
                return col;
            }
        }
        return queryData.columns.size() > 1 ? 1 : 0;
    }

    private void applyFallbackReport(LearningReportResult result, QueryData queryData, String aiError) {
        result.setReportTitle("学情分析报告");
        String summary = "共查询到 " + queryData.rows.size() + " 条记录。";
        if (StringUtils.hasText(aiError)) {
            summary += "（AI 报告生成失败：" + aiError + "，以下为系统自动摘要）";
        }
        result.setSummary(summary);

        ReportSection overview = new ReportSection();
        overview.setHeading("数据概况");
        overview.setContent("问题：" + result.getQuestion() + "；返回字段："
                + String.join("、", queryData.columns) + "；记录数：" + queryData.rows.size());

        ReportSection finding = new ReportSection();
        finding.setHeading("主要发现");
        finding.setContent(buildSimpleFinding(queryData));

        result.setSections(List.of(overview, finding));
    }

    private String buildSimpleFinding(QueryData queryData) {
        if (queryData.rows.isEmpty()) {
            return "当前条件下暂无数据，建议调整查询范围或确认数据是否已录入。";
        }
        StringBuilder sb = new StringBuilder("样本数据：");
        int limit = Math.min(5, queryData.rows.size());
        for (int i = 0; i < limit; i++) {
            sb.append("\n- ").append(queryData.rows.get(i).stream()
                    .map(String::valueOf).collect(Collectors.joining(" | ")));
        }
        return sb.toString();
    }

    private String buildFallbackSql(String question, ScopeContext scope) {
        String q = question.toLowerCase();
        if (q.contains("考勤") && (q.contains("状态") || q.contains("分布"))) {
            return applyScope("""
                    SELECT CASE a.status WHEN 1 THEN '正常' WHEN 2 THEN '迟到' WHEN 3 THEN '早退'
                           WHEN 4 THEN '缺勤' WHEN 5 THEN '请假' ELSE '未知' END AS status_name,
                           COUNT(*) AS count
                    FROM attendance a
                    JOIN student s ON a.student_id = s.id
                    JOIN clazz c ON s.class_id = c.id
                    WHERE 1=1
                    """, scope, "s.class_id", "s.id");
        }
        if (q.contains("平均") && q.contains("成绩")) {
            return applyScope("""
                    SELECT c.name AS class_name, ROUND(AVG(sc.score), 1) AS avg_score, COUNT(sc.id) AS exam_count
                    FROM score sc
                    JOIN student s ON sc.student_id = s.id
                    JOIN clazz c ON s.class_id = c.id
                    GROUP BY c.id, c.name
                    ORDER BY avg_score DESC
                    """, scope, "s.class_id", "s.id");
        }
        if (q.contains("成绩") || q.contains("考试")) {
            return applyScope("""
                    SELECT s.name AS student_name, c.name AS class_name, e.name AS exam_name,
                           sc.score, sc.rank_num
                    FROM score sc
                    JOIN student s ON sc.student_id = s.id
                    JOIN clazz c ON s.class_id = c.id
                    JOIN exam e ON sc.exam_id = e.id
                    ORDER BY sc.score DESC
                    """, scope, "s.class_id", "s.id");
        }
        if (q.contains("缺勤")) {
            return applyScope("""
                    SELECT s.name AS student_name, c.name AS class_name, COUNT(*) AS absent_count
                    FROM attendance a
                    JOIN student s ON a.student_id = s.id
                    JOIN clazz c ON s.class_id = c.id
                    WHERE a.status = 4
                    GROUP BY s.id, s.name, c.name
                    ORDER BY absent_count DESC
                    """, scope, "s.class_id", "s.id");
        }
        return applyScope("""
                SELECT c.name AS class_name, COUNT(s.id) AS student_count
                FROM clazz c
                LEFT JOIN student s ON s.class_id = c.id
                GROUP BY c.id, c.name
                ORDER BY student_count DESC
                """, scope, "c.id", "s.id");
    }

    private String applyScope(String baseSql, ScopeContext scope, String classColumn, String studentColumn) {
        StringBuilder sql = new StringBuilder(baseSql.trim());
        List<String> conditions = new ArrayList<>();
        if (scope.classId != null) {
            conditions.add(classColumn + " = " + scope.classId);
        } else if (scope.studentId != null) {
            conditions.add(studentColumn + " = " + scope.studentId);
        } else if (scope.allowedClassIds != null && !scope.allowedClassIds.isEmpty()) {
            String ids = scope.allowedClassIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            conditions.add(classColumn + " IN (" + ids + ")");
        }
        if (!conditions.isEmpty()) {
            if (sql.toString().toLowerCase().contains(" where ")) {
                sql.append(" AND ").append(String.join(" AND ", conditions));
            } else {
                sql.append(" WHERE ").append(String.join(" AND ", conditions));
            }
        }
        if (!sql.toString().toLowerCase().contains(" limit ")) {
            sql.append(" LIMIT 500");
        }
        return sql.toString();
    }

    private QueryData executeQuery(String sql) {
        List<Map<String, Object>> rawRows = jdbcTemplate.queryForList(sql);
        QueryData data = new QueryData();
        if (rawRows.isEmpty()) {
            return data;
        }
        data.columns = new ArrayList<>(rawRows.get(0).keySet());
        for (Map<String, Object> row : rawRows) {
            List<Object> values = new ArrayList<>();
            for (String col : data.columns) {
                values.add(row.get(col));
            }
            data.rows.add(values);
        }
        return data;
    }

    private SqlGeneration parseSqlGeneration(String aiText) {
        try {
            JsonNode root = objectMapper.readTree(extractJson(aiText));
            SqlGeneration gen = new SqlGeneration();
            gen.sql = root.path("sql").asText("").trim();
            gen.explanation = root.path("explanation").asText("");
            if (!StringUtils.hasText(gen.sql)) {
                throw new IllegalArgumentException("模型未返回有效 SQL");
            }
            return gen;
        } catch (Exception e) {
            throw new RuntimeException("解析 SQL 失败: " + e.getMessage(), e);
        }
    }

    private String callSparkWithTimeout(String systemPrompt, String userPrompt) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executor.submit(() -> sparkAiService.chat(systemPrompt, userPrompt));
            return future.get(sparkProperties.getTimeoutSeconds(), TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }

    private Number toNumber(Object value) {
        if (value instanceof Number n) {
            return n;
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private static class ScopeContext {
        Integer classId;
        Integer studentId;
        List<Integer> allowedClassIds;
        Integer teacherId;

        String toHint() {
            Map<String, Object> hint = new LinkedHashMap<>();
            if (classId != null) hint.put("classId", classId);
            if (studentId != null) hint.put("studentId", studentId);
            if (allowedClassIds != null && !allowedClassIds.isEmpty()) {
                hint.put("allowedClassIds", allowedClassIds);
                hint.put("note", "SQL 必须限制在这些班级范围内");
            }
            if (teacherId != null) hint.put("teacherId", teacherId);
            return hint.toString();
        }
    }

    private static class SqlGeneration {
        String sql;
        String explanation;
    }

    private static class QueryData {
        List<String> columns = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        List<List<Object>> previewRows(int max) {
            return rows.size() <= max ? rows : rows.subList(0, max);
        }
    }
}
