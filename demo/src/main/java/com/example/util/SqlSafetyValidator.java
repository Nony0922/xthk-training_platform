package com.example.util;

import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.regex.Pattern;

public final class SqlSafetyValidator {

    private static final Set<String> ALLOWED_TABLES = Set.of(
            "student", "attendance", "score", "exam", "clazz", "course", "teacher", "parent"
    );

    private static final Pattern FORBIDDEN = Pattern.compile(
            "\\b(INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|TRUNCATE|REPLACE|GRANT|REVOKE|EXEC|EXECUTE|"
                    + "CALL|LOAD|OUTFILE|INFILE|INTO\\s+DUMPFILE|UNION\\s+ALL|UNION\\s+SELECT|"
                    + "INFORMATION_SCHEMA|MYSQL\\.|PERFORMANCE_SCHEMA|SYS\\.)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern MULTI_STATEMENT = Pattern.compile(";\\s*\\S");

    private SqlSafetyValidator() {
    }

    public static String validateAndNormalize(String sql) {
        if (!StringUtils.hasText(sql)) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        String normalized = sql.trim();
        while (normalized.endsWith(";")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }
        if (!normalized.regionMatches(true, 0, "SELECT", 0, 6)) {
            throw new IllegalArgumentException("仅允许执行 SELECT 查询");
        }
        if (FORBIDDEN.matcher(normalized).find()) {
            throw new IllegalArgumentException("SQL 包含不允许的操作");
        }
        if (MULTI_STATEMENT.matcher(normalized).find()) {
            throw new IllegalArgumentException("不允许执行多条 SQL 语句");
        }
        String lower = normalized.toLowerCase();
        boolean hasAllowedTable = ALLOWED_TABLES.stream().anyMatch(lower::contains);
        if (!hasAllowedTable) {
            throw new IllegalArgumentException("SQL 未使用允许的学情数据表");
        }
        if (!lower.contains(" limit ")) {
            normalized = normalized + " LIMIT 500";
        }
        return normalized;
    }
}
