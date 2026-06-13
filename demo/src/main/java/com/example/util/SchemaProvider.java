package com.example.util;

public final class SchemaProvider {

    private SchemaProvider() {
    }

    public static String schemaDescription() {
        return """
                数据库 training_platform，可用表结构如下（仅可使用 SELECT 查询这些表）：

                1. student（学生）
                   - id, name, gender(1男2女), birthday, phone, class_id, parent_id, enroll_date, status, create_time

                2. clazz（班级）
                   - id, name, grade, head_teacher_id, room, capacity, description, status, create_time

                3. attendance（考勤）
                   - id, student_id, class_id, course_id, attend_date, status(1正常2迟到3早退4缺勤5请假), remark, recorder_id, create_time

                4. exam（考试）
                   - id, name, course_id, class_id, exam_date, start_time, end_time, location, total_score, status, remark, create_time

                5. score（成绩）
                   - id, exam_id, student_id, score, rank_num, remark, create_time

                6. course（课程）
                   - id, name, description, teacher_id, hours, fee, target_grade, subject, teach_mode, location, status, create_time

                7. teacher（教师）
                   - id, user_id, name, gender, phone, teacher_level(1任课2班主任), subject, title, hire_date, status, create_time

                8. parent（家长）
                   - id, user_id, name, phone, email, address, create_time

                常用 JOIN：
                - student.class_id = clazz.id
                - attendance.student_id = student.id
                - score.student_id = student.id AND score.exam_id = exam.id
                - exam.class_id = clazz.id AND exam.course_id = course.id
                """;
    }
}
