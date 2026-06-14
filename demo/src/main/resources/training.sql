-- 心田花开培训机构综合管理平台 数据库初始化脚本
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS training_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE training_platform;

DROP TABLE IF EXISTS learning_report;
DROP TABLE IF EXISTS course_order;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS home_visit;
DROP TABLE IF EXISTS leave_request;
DROP TABLE IF EXISTS abnormal_attendance;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS score;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS teaching_progress;
DROP TABLE IF EXISTS class_schedule;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS clazz;
DROP TABLE IF EXISTS parent;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS sys_user;

-- 系统用户（管理员/教师/家长登录账号）
CREATE TABLE sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    role VARCHAR(20) NOT NULL COMMENT 'admin管理员 teacher教师 parent家长',
    teacher_level TINYINT DEFAULT NULL COMMENT '1任课教师 2班主任',
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1 COMMENT '1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统用户表';

-- 教师信息
CREATE TABLE teacher (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(50) NOT NULL,
    gender TINYINT COMMENT '1男 2女',
    phone VARCHAR(20),
    email VARCHAR(100),
    teacher_level TINYINT DEFAULT 1 COMMENT '1任课教师 2班主任',
    subject VARCHAR(50) COMMENT '任教科目',
    title VARCHAR(50) COMMENT '职称',
    hire_date DATE,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '教师表';

-- 家长信息
CREATE TABLE parent (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '家长表';

-- 班级
CREATE TABLE clazz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    grade VARCHAR(20),
    head_teacher_id INT,
    room VARCHAR(50),
    capacity INT DEFAULT 30,
    description VARCHAR(500),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '班级表';

-- 学生
CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender TINYINT COMMENT '1男 2女',
    birthday DATE,
    phone VARCHAR(20),
    class_id INT,
    parent_id INT,
    enroll_date DATE,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '学生表';

-- 课程
CREATE TABLE course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    teacher_id INT,
    hours INT DEFAULT 0 COMMENT '学时',
    fee DECIMAL(10,2) DEFAULT 0 COMMENT '费用',
    target_grade VARCHAR(50) COMMENT '适用年级',
    subject VARCHAR(50) COMMENT '学科',
    teach_mode TINYINT DEFAULT 1 COMMENT '1线下 2线上 3混合',
    location VARCHAR(200) COMMENT '上课地点',
    valid_start DATE COMMENT '课程开始日期',
    valid_end DATE COMMENT '课程结束日期',
    class_time_desc VARCHAR(200) COMMENT '上课时间说明',
    max_students INT DEFAULT 0 COMMENT '招生名额，0表示不限',
    enrolled_count INT DEFAULT 0 COMMENT '已报名人数',
    suitable_age VARCHAR(50) COMMENT '适合年龄',
    highlights VARCHAR(500) COMMENT '课程亮点，|分隔',
    status TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '课程表';

-- 课程表安排
CREATE TABLE class_schedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    class_id INT NOT NULL,
    course_id INT NOT NULL,
    teacher_id INT,
    weekday TINYINT COMMENT '1-7周一到周日',
    start_time TIME,
    end_time TIME,
    room VARCHAR(50),
    semester VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '课程表';

-- 教学进度
CREATE TABLE teaching_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    class_id INT NOT NULL,
    course_id INT NOT NULL,
    teacher_id INT,
    chapter VARCHAR(100),
    content TEXT,
    planned_date DATE,
    actual_date DATE,
    status TINYINT DEFAULT 0 COMMENT '0未开始 1进行中 2已完成',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '教学进度表';

-- 通知公告
CREATE TABLE announcement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    publisher_id INT,
    publisher_name VARCHAR(50),
    target_role VARCHAR(20) DEFAULT 'all' COMMENT 'all/admin/teacher/parent',
    status TINYINT DEFAULT 1 COMMENT '1发布 0草稿',
    publish_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '通知公告表';

-- 考试
CREATE TABLE exam (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    course_id INT,
    class_id INT,
    exam_date DATE,
    start_time TIME,
    end_time TIME,
    location VARCHAR(100),
    total_score DECIMAL(5,1) DEFAULT 100,
    status TINYINT DEFAULT 0 COMMENT '0未开始 1进行中 2已结束',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '考试表';

-- 成绩
CREATE TABLE score (
    id INT PRIMARY KEY AUTO_INCREMENT,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    score DECIMAL(5,1),
    rank_num INT,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '成绩表';

-- 考勤
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    class_id INT,
    course_id INT,
    attend_date DATE NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '1正常 2迟到 3早退 4缺勤 5请假',
    remark VARCHAR(200),
    recorder_id INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '考勤表';

-- 异常考勤
CREATE TABLE abnormal_attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attendance_id INT,
    student_id INT NOT NULL,
    abnormal_type TINYINT COMMENT '2迟到 3早退 4缺勤',
    description VARCHAR(500),
    handle_status TINYINT DEFAULT 0 COMMENT '0待处理 1已处理',
    handle_result VARCHAR(500),
    handler_id INT,
    handle_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '异常考勤表';

-- 请假
CREATE TABLE leave_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    applicant_id INT COMMENT '申请人user_id',
    applicant_name VARCHAR(50),
    leave_type TINYINT DEFAULT 1 COMMENT '1事假 2病假 3其他',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status TINYINT DEFAULT 0 COMMENT '0待审批 1已通过 2已驳回',
    approver_id INT,
    approve_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '请假表';

-- 家访记录
CREATE TABLE home_visit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    teacher_id INT NOT NULL,
    visit_date DATE NOT NULL,
    visit_type TINYINT DEFAULT 1 COMMENT '1上门 2电话 3线上',
    content TEXT,
    feedback TEXT,
    next_plan VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '家访记录表';

-- 留言（小程序端）
CREATE TABLE message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    parent_id INT NOT NULL,
    content TEXT NOT NULL,
    reply TEXT,
    replier_id INT,
    status TINYINT DEFAULT 0 COMMENT '0待回复 1已回复',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    reply_time DATETIME
) COMMENT '留言表';

-- 课程订单（小程序端）
CREATE TABLE course_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    parent_id INT NOT NULL,
    course_id INT NOT NULL,
    course_name VARCHAR(100),
    teacher_name VARCHAR(50),
    hours INT,
    fee DECIMAL(10,2),
    status TINYINT DEFAULT 0 COMMENT '0待支付 1已支付 2已取消',
    pay_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '课程订单表';

-- AI 学情分析报告
CREATE TABLE learning_report (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '报告标题',
    question TEXT NOT NULL COMMENT '自然语言问题',
    sql_text TEXT COMMENT '执行的 SQL',
    query_result_json MEDIUMTEXT COMMENT '查询结果 JSON',
    chart_config_json MEDIUMTEXT COMMENT '图表配置 JSON',
    report_content_json MEDIUMTEXT COMMENT '报告正文 JSON',
    class_id INT COMMENT '关联班级',
    student_id INT COMMENT '关联学生',
    creator_role VARCHAR(20) COMMENT '创建者角色 admin/teacher',
    creator_id INT COMMENT '创建者 ID',
    creator_name VARCHAR(50) COMMENT '创建者姓名',
    parent_visible TINYINT DEFAULT 0 COMMENT '1家长可见 0仅教师',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'AI学情分析报告';

-- ========== 初始数据 ==========

-- 系统用户：admin / teacher1(班主任兼课) / teacher2(任课) / teacher3(语文任课) / parent1~parent4
INSERT INTO sys_user (username, password, name, role, teacher_level, phone) VALUES
('admin', '123456', '教务处管理员', 'admin', NULL, '13800000001'),
('teacher1', '123456', '张老师', 'teacher', 2, '13800000002'),
('teacher2', '123456', '李老师', 'teacher', 1, '13800000003'),
('teacher3', '123456', '王老师', 'teacher', 1, '13800000008'),
('parent1', '123456', '王家长', 'parent', NULL, '13800000004'),
('parent2', '123456', '李家长', 'parent', NULL, '13800000005'),
('parent3', '123456', '陈家长', 'parent', NULL, '13800000006'),
('parent4', '123456', '张家长', 'parent', NULL, '13800000007');

INSERT INTO teacher (user_id, name, gender, phone, teacher_level, subject, title, hire_date) VALUES
(2, '张老师', 1, '13800000002', 2, '班级管理/英语', '高级教师', '2020-09-01'),
(3, '李老师', 2, '13800000003', 1, '数学', '中级教师', '2021-03-01'),
(4, '王老师', 1, '13800000008', 1, '语文', '中级教师', '2021-09-01');

INSERT INTO parent (user_id, name, phone, address)
SELECT id, '王家长', '13800000004', '成都市武侯区天府大道100号' FROM sys_user WHERE username = 'parent1'
UNION ALL
SELECT id, '李家长', '13800000005', '成都市锦江区春熙路88号' FROM sys_user WHERE username = 'parent2'
UNION ALL
SELECT id, '陈家长', '13800000006', '成都市青羊区宽窄巷子66号' FROM sys_user WHERE username = 'parent3'
UNION ALL
SELECT id, '张家长', '13800000007', '成都市高新区天府三街200号' FROM sys_user WHERE username = 'parent4';
-- 家长账号已绑定档案；每位家长最多关联 2 名学生（多孩家庭演示见 parent1）

INSERT INTO clazz (name, grade, head_teacher_id, room, capacity, description) VALUES
('一年级1班', '一年级', 1, 'A101', 30, '语文基础班'),
('二年级数学提高班', '二年级', NULL, 'B203', 25, '数学提高培训');

INSERT INTO student (name, gender, birthday, class_id, parent_id, enroll_date)
SELECT '王小明', 1, '2018-05-12', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT '李小红', 2, '2017-08-20', 2, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT '陈小华', 2, '2018-03-08', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000005'
UNION ALL
SELECT '赵小强', 1, '2018-07-15', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000005'
UNION ALL
SELECT '刘小丽', 2, '2018-01-22', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000006'
UNION ALL
SELECT '周小杰', 1, '2018-11-05', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000006'
UNION ALL
SELECT '张小洋', 1, '2017-04-18', 2, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000007';

INSERT INTO course (name, description, teacher_id, hours, fee, status, target_grade, subject, teach_mode, location, valid_start, valid_end, class_time_desc, max_students, enrolled_count, suitable_age, highlights) VALUES
('小学语文基础', '面向一年级学生的语文基础课程，系统培养拼音、识字、阅读与写作能力，小班制互动教学。', 3, 48, 3600.00, 1, '一年级', '语文', 1, 'A101 教室（武侯校区）', '2025-03-01', '2025-06-30', '每周一、三 09:00-10:30', 30, 12, '6-7岁', '拼音启蒙|课外阅读|写作训练|小班互动'),
('小学数学提高', '面向二年级学生的数学提高课程，强化逻辑思维与综合应用能力，含阶段测评与错题讲解。', 2, 36, 2800.00, 1, '二年级', '数学', 1, 'B203 教室（武侯校区）', '2025-03-01', '2025-06-30', '每周二、四 14:00-15:30', 25, 8, '7-8岁', '思维训练|应用题突破|阶段测评|错题讲解'),
('英语口语启蒙', '面向初学者的英语口语启蒙课程，线上直播互动，外教式情景对话练习，支持回放复习。', 1, 24, 1800.00, 1, '全年级', '英语', 2, '线上直播（腾讯会议）', '2025-03-15', '2025-05-31', '每周六 09:30-11:00', 50, 20, '6-10岁', '情景对话|发音纠正|直播互动|课程回放');

INSERT INTO class_schedule (class_id, course_id, teacher_id, weekday, start_time, end_time, room, semester) VALUES
(1, 1, 3, 1, '09:00:00', '10:30:00', 'A101', '2025春季'),
(1, 1, 3, 3, '09:00:00', '10:30:00', 'A101', '2025春季'),
(1, 3, 1, 6, '09:30:00', '11:00:00', 'C305', '2025春季'),
(2, 2, 2, 2, '14:00:00', '15:30:00', 'B203', '2025春季'),
(2, 2, 2, 4, '14:00:00', '15:30:00', 'B203', '2025春季'),
(2, 3, 1, 1, '09:30:00', '11:00:00', 'C305', '2025春季');

INSERT INTO teaching_progress (class_id, course_id, teacher_id, chapter, content, planned_date, actual_date, status) VALUES
(1, 1, 3, '第一单元', '拼音入门与基础识字', '2025-03-01', '2025-03-01', 2),
(1, 1, 3, '第二单元', '短文阅读训练', '2025-03-15', NULL, 1),
(2, 2, 2, '第一讲', '加减法综合应用', '2025-03-05', '2025-03-05', 2),
(1, 3, 1, '第一讲', '日常问候与自我介绍', '2025-03-16', '2025-03-16', 2),
(2, 3, 1, '第一讲', '情景对话：购物用语', '2025-03-03', '2025-03-03', 2);

INSERT INTO announcement (title, content, publisher_id, publisher_name, target_role, status, publish_time) VALUES
('2025春季开学通知', '各位家长和同学，2025春季班将于3月1日正式开课，请按时到校。', 1, '教务处管理员', 'all', 1, NOW()),
('期中考试安排预告', '期中考试将于4月中旬举行，请各班级做好复习准备。', 1, '教务处管理员', 'parent', 1, NOW());

INSERT INTO exam (name, course_id, class_id, exam_date, start_time, end_time, location, total_score, status) VALUES
('语文期中考试', 1, 1, '2025-04-15', '09:00:00', '10:30:00', 'A101', 100, 0),
('语文单元测验', 1, 1, '2025-03-20', '09:00:00', '10:00:00', 'A101', 100, 2),
('语文期末模拟', 1, 1, '2025-05-10', '09:00:00', '10:30:00', 'A101', 100, 0),
('数学单元测试', 2, 2, '2025-04-10', '14:00:00', '15:00:00', 'B203', 100, 0),
('数学阶段测评', 2, 2, '2025-04-20', '14:00:00', '15:00:00', 'B203', 100, 0),
('英语口语阶段测评', 3, 1, '2025-04-08', '09:30:00', '11:00:00', 'C305', 100, 0),
('英语口语单元测试', 3, 2, '2025-04-12', '09:30:00', '11:00:00', 'C305', 100, 0);
-- 注：status 入库值仅供参考，接口返回时由 ExamStatusUtil 按日期时间自动计算

INSERT INTO score (exam_id, student_id, score, rank_num)
SELECT e.id, s.id, v.score, v.rank_num
FROM exam e
JOIN student s ON s.class_id = e.class_id
JOIN (
  SELECT '语文期中考试' AS en, '王小明' AS n, 92.5 AS score, 1 AS rank_num UNION ALL
  SELECT '语文期中考试', '陈小华', 88.0, 2 UNION ALL
  SELECT '语文期中考试', '赵小强', 85.5, 3 UNION ALL
  SELECT '语文期中考试', '刘小丽', 79.0, 4 UNION ALL
  SELECT '语文期中考试', '周小杰', 76.5, 5 UNION ALL
  SELECT '语文单元测验', '王小明', 95.0, 1 UNION ALL
  SELECT '语文单元测验', '陈小华', 90.0, 2 UNION ALL
  SELECT '语文单元测验', '赵小强', 87.0, 3 UNION ALL
  SELECT '语文单元测验', '刘小丽', 82.0, 4 UNION ALL
  SELECT '语文单元测验', '周小杰', 80.0, 5 UNION ALL
  SELECT '数学单元测试', '李小红', 88.0, 1 UNION ALL
  SELECT '数学单元测试', '张小洋', 72.0, 2 UNION ALL
  SELECT '数学阶段测评', '李小红', 91.0, 1 UNION ALL
  SELECT '数学阶段测评', '张小洋', 78.5, 2 UNION ALL
  SELECT '英语口语阶段测评', '王小明', 93.0, 1 UNION ALL
  SELECT '英语口语阶段测评', '陈小华', 89.5, 2 UNION ALL
  SELECT '英语口语阶段测评', '赵小强', 86.0, 3 UNION ALL
  SELECT '英语口语阶段测评', '刘小丽', 81.0, 4 UNION ALL
  SELECT '英语口语阶段测评', '周小杰', 78.0, 5 UNION ALL
  SELECT '英语口语单元测试', '李小红', 90.0, 1 UNION ALL
  SELECT '英语口语单元测试', '张小洋', 75.5, 2
) v ON e.name = v.en AND s.name = v.n;

INSERT INTO attendance (student_id, class_id, course_id, attend_date, status, recorder_id)
SELECT s.id, s.class_id, c.course_id, c.attend_date, c.status, c.recorder_id
FROM student s
JOIN (
  SELECT '王小明' AS n, 1 AS class_id, 1 AS course_id, '2025-03-01' AS attend_date, 1 AS status, 2 AS recorder_id UNION ALL
  SELECT '王小明', 1, 1, '2025-03-03', 2, 2 UNION ALL
  SELECT '王小明', 1, 1, '2025-03-05', 1, 2 UNION ALL
  SELECT '王小明', 1, 1, '2025-03-08', 1, 2 UNION ALL
  SELECT '王小明', 1, 1, '2025-03-10', 5, 2 UNION ALL
  SELECT '陈小华', 1, 1, '2025-03-01', 1, 2 UNION ALL
  SELECT '陈小华', 1, 1, '2025-03-03', 1, 2 UNION ALL
  SELECT '陈小华', 1, 1, '2025-03-05', 1, 2 UNION ALL
  SELECT '陈小华', 1, 1, '2025-03-08', 2, 2 UNION ALL
  SELECT '陈小华', 1, 1, '2025-03-10', 1, 2 UNION ALL
  SELECT '赵小强', 1, 1, '2025-03-01', 1, 2 UNION ALL
  SELECT '赵小强', 1, 1, '2025-03-03', 4, 2 UNION ALL
  SELECT '赵小强', 1, 1, '2025-03-05', 1, 2 UNION ALL
  SELECT '赵小强', 1, 1, '2025-03-08', 4, 2 UNION ALL
  SELECT '赵小强', 1, 1, '2025-03-10', 1, 2 UNION ALL
  SELECT '刘小丽', 1, 1, '2025-03-01', 1, 2 UNION ALL
  SELECT '刘小丽', 1, 1, '2025-03-03', 1, 2 UNION ALL
  SELECT '刘小丽', 1, 1, '2025-03-05', 2, 2 UNION ALL
  SELECT '刘小丽', 1, 1, '2025-03-08', 1, 2 UNION ALL
  SELECT '刘小丽', 1, 1, '2025-03-10', 1, 2 UNION ALL
  SELECT '周小杰', 1, 1, '2025-03-01', 2, 2 UNION ALL
  SELECT '周小杰', 1, 1, '2025-03-03', 1, 2 UNION ALL
  SELECT '周小杰', 1, 1, '2025-03-05', 1, 2 UNION ALL
  SELECT '周小杰', 1, 1, '2025-03-08', 3, 2 UNION ALL
  SELECT '周小杰', 1, 1, '2025-03-10', 4, 2 UNION ALL
  SELECT '李小红', 2, 2, '2025-03-02', 1, 3 UNION ALL
  SELECT '李小红', 2, 2, '2025-03-04', 4, 3 UNION ALL
  SELECT '李小红', 2, 2, '2025-03-06', 1, 3 UNION ALL
  SELECT '李小红', 2, 2, '2025-03-09', 2, 3 UNION ALL
  SELECT '张小洋', 2, 2, '2025-03-02', 1, 3 UNION ALL
  SELECT '张小洋', 2, 2, '2025-03-04', 1, 3 UNION ALL
  SELECT '张小洋', 2, 2, '2025-03-06', 4, 3 UNION ALL
  SELECT '张小洋', 2, 2, '2025-03-09', 4, 3 UNION ALL
  SELECT '王小明', 1, 3, '2025-03-16', 1, 1 UNION ALL
  SELECT '王小明', 1, 3, '2025-03-23', 1, 1 UNION ALL
  SELECT '陈小华', 1, 3, '2025-03-16', 2, 1 UNION ALL
  SELECT '赵小强', 1, 3, '2025-03-16', 1, 1 UNION ALL
  SELECT '刘小丽', 1, 3, '2025-03-23', 1, 1 UNION ALL
  SELECT '周小杰', 1, 3, '2025-03-16', 4, 1 UNION ALL
  SELECT '李小红', 2, 3, '2025-03-03', 1, 1 UNION ALL
  SELECT '李小红', 2, 3, '2025-03-10', 1, 1 UNION ALL
  SELECT '张小洋', 2, 3, '2025-03-03', 2, 1 UNION ALL
  SELECT '张小洋', 2, 3, '2025-03-10', 1, 1
) c ON s.name = c.n AND s.class_id = c.class_id;

INSERT INTO abnormal_attendance (attendance_id, student_id, abnormal_type, description, handle_status)
SELECT a.id, s.id, 2, '迟到15分钟', 0
FROM attendance a
JOIN student s ON a.student_id = s.id
WHERE s.name = '王小明' AND a.attend_date = '2025-03-03' AND a.status = 2
UNION ALL
SELECT a.id, s.id, 4, '未到课，未请假', 0
FROM attendance a
JOIN student s ON a.student_id = s.id
WHERE s.name = '赵小强' AND a.attend_date = '2025-03-08' AND a.status = 4;

INSERT INTO leave_request (student_id, applicant_id, applicant_name, leave_type, start_date, end_date, reason, status)
SELECT s.id, u.id, '王家长', 2, '2025-03-10', '2025-03-11', '感冒发烧，需要休息', 0
FROM student s
JOIN sys_user u ON u.username = 'parent1'
WHERE s.name = '王小明';

INSERT INTO home_visit (student_id, teacher_id, visit_date, visit_type, content, feedback, next_plan) VALUES
(1, 1, '2025-02-20', 2, '电话了解学生在家学习情况', '家长反馈孩子阅读兴趣较高', '建议增加课外阅读');

INSERT INTO message (parent_id, content, status)
SELECT p.id, '请问下周课程是否有调整？', 0 FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT p.id, '赵小强最近数学作业完成情况如何？', 0 FROM parent p WHERE p.phone = '13800000005'
UNION ALL
SELECT p.id, '周小杰能否申请调课？', 1 FROM parent p WHERE p.phone = '13800000006';

INSERT INTO course_order (order_no, parent_id, course_id, course_name, teacher_name, hours, fee, status)
SELECT 'ORD20250301001', p.id, 1, '小学语文基础', '王老师', 48, 3600.00, 1 FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT 'ORD20250315002', p.id, 3, '英语口语启蒙', '张老师', 24, 1800.00, 0 FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT 'ORD20250320003', p.id, 1, '小学语文基础', '王老师', 48, 3600.00, 1 FROM parent p WHERE p.phone = '13800000005'
UNION ALL
SELECT 'ORD20250322004', p.id, 2, '小学数学提高', '李老师', 36, 2800.00, 0 FROM parent p WHERE p.phone = '13800000007';

-- 说明：本文件为唯一数据库脚本，包含建库、建表及全部初始/演示数据（含 AI 学情分析、家长绑定、多家长多孩家庭等）。
--
-- 教师角色模型（与 PC 教师端菜单一致）：
--   • teacher_level=1：任课教师，仅使用「教师功能」菜单（授课相关）。
--   • teacher_level=2：班主任，使用全部「教师功能」+ 额外「班主任专有」菜单（本班学生/家长、请假、家访、本班考勤/成绩）。
--   • 班主任可同时任课：张老师(teacher1) 任一年级1班班主任，并兼任「英语口语启蒙」授课；语文由王老师(teacher3) 任课。
--
-- 数据范围（接口 scopeUserId + teacherLevel，前端 scopeMode）：
--   • teaching（teacherLevel=1）：本人授课课程、课表中 teacher_id 匹配的安排、授课班级考试/考勤/成绩。
--   • homeroom（teacherLevel=2）：head_teacher_id 关联班级的学生、家长、请假、家访、本班考勤/成绩。
--   • 我的课表：班主任合并展示「本班完整课表 + 本人授课安排」；任课教师仅展示本人授课安排。
--
-- 家长与学生：parent1 王小明+李小红；parent2 陈小华+赵小强；parent3 刘小丽+周小杰；parent4 张小洋（每位家长最多 2 孩）。
-- 使用方式：在 MySQL 中完整执行本文件即可，无需再执行其他 SQL 脚本。
