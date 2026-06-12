-- 心田花开培训机构综合管理平台 数据库初始化脚本
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS training_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE training_platform;

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

-- ========== 初始数据 ==========

-- 系统用户：admin/teacher1(班主任)/teacher2(任课)/parent1
INSERT INTO sys_user (username, password, name, role, teacher_level, phone) VALUES
('admin', '123456', '教务处管理员', 'admin', NULL, '13800000001'),
('teacher1', '123456', '张老师', 'teacher', 2, '13800000002'),
('teacher2', '123456', '李老师', 'teacher', 1, '13800000003'),
('parent1', '123456', '王家长', 'parent', NULL, '13800000004');

INSERT INTO teacher (user_id, name, gender, phone, teacher_level, subject, title, hire_date) VALUES
(2, '张老师', 1, '13800000002', 2, '语文', '高级教师', '2020-09-01'),
(3, '李老师', 2, '13800000003', 1, '数学', '中级教师', '2021-03-01');

INSERT INTO parent (user_id, name, phone, address)
SELECT id, '王家长', '13800000004', '成都市武侯区' FROM sys_user WHERE username = 'parent1';

INSERT INTO clazz (name, grade, head_teacher_id, room, capacity, description) VALUES
('一年级1班', '一年级', 1, 'A101', 30, '语文基础班'),
('二年级数学提高班', '二年级', NULL, 'B203', 25, '数学提高培训');

INSERT INTO student (name, gender, birthday, class_id, parent_id, enroll_date)
SELECT '王小明', 1, '2018-05-12', 1, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000004'
UNION ALL
SELECT '李小红', 2, '2017-08-20', 2, p.id, '2024-09-01' FROM parent p WHERE p.phone = '13800000004';

INSERT INTO course (name, description, teacher_id, hours, fee, status) VALUES
('小学语文基础', '面向一年级学生的语文基础课程，培养阅读与写作能力', 1, 48, 3600.00, 1),
('小学数学提高', '面向二年级学生的数学提高课程，强化逻辑思维', 2, 36, 2800.00, 1),
('英语口语启蒙', '面向初学者的英语口语启蒙课程', 2, 24, 1800.00, 1);

INSERT INTO class_schedule (class_id, course_id, teacher_id, weekday, start_time, end_time, room, semester) VALUES
(1, 1, 1, 1, '09:00:00', '10:30:00', 'A101', '2025春季'),
(1, 1, 1, 3, '09:00:00', '10:30:00', 'A101', '2025春季'),
(2, 2, 2, 2, '14:00:00', '15:30:00', 'B203', '2025春季'),
(2, 2, 2, 4, '14:00:00', '15:30:00', 'B203', '2025春季'),
(2, 3, 1, 1, '09:30:00', '11:00:00', 'C305', '2025春季');

INSERT INTO teaching_progress (class_id, course_id, teacher_id, chapter, content, planned_date, actual_date, status) VALUES
(1, 1, 1, '第一单元', '拼音入门与基础识字', '2025-03-01', '2025-03-01', 2),
(1, 1, 1, '第二单元', '短文阅读训练', '2025-03-15', NULL, 1),
(2, 2, 2, '第一讲', '加减法综合应用', '2025-03-05', '2025-03-05', 2);

INSERT INTO announcement (title, content, publisher_id, publisher_name, target_role, status, publish_time) VALUES
('2025春季开学通知', '各位家长和同学，2025春季班将于3月1日正式开课，请按时到校。', 1, '教务处管理员', 'all', 1, NOW()),
('期中考试安排预告', '期中考试将于4月中旬举行，请各班级做好复习准备。', 1, '教务处管理员', 'parent', 1, NOW());

INSERT INTO exam (name, course_id, class_id, exam_date, start_time, end_time, location, total_score, status) VALUES
('语文期中考试', 1, 1, '2025-04-15', '09:00:00', '10:30:00', 'A101', 100, 0),
('数学单元测试', 2, 2, '2025-04-10', '14:00:00', '15:00:00', 'B203', 100, 0);

INSERT INTO score (exam_id, student_id, score, rank_num) VALUES
(1, 1, 92.5, 1),
(2, 2, 88.0, 1);

INSERT INTO attendance (student_id, class_id, course_id, attend_date, status, recorder_id) VALUES
(1, 1, 1, '2025-03-01', 1, 2),
(1, 1, 1, '2025-03-03', 2, 2),
(2, 2, 2, '2025-03-02', 1, 3),
(2, 2, 2, '2025-03-04', 4, 3);

INSERT INTO abnormal_attendance (attendance_id, student_id, abnormal_type, description, handle_status) VALUES
(2, 1, 2, '迟到15分钟', 0),
(4, 2, 4, '未到课，未请假', 0);

INSERT INTO leave_request (student_id, applicant_id, applicant_name, leave_type, start_date, end_date, reason, status) VALUES
(1, 4, '王家长', 2, '2025-03-10', '2025-03-11', '感冒发烧，需要休息', 0);

INSERT INTO home_visit (student_id, teacher_id, visit_date, visit_type, content, feedback, next_plan) VALUES
(1, 1, '2025-02-20', 2, '电话了解学生在家学习情况', '家长反馈孩子阅读兴趣较高', '建议增加课外阅读');

INSERT INTO message (parent_id, content, status) VALUES
(1, '请问下周课程是否有调整？', 0);

INSERT INTO course_order (order_no, parent_id, course_id, course_name, teacher_name, hours, fee, status) VALUES
('ORD20250301001', 1, 1, '小学语文基础', '张老师', 48, 3600.00, 1),
('ORD20250315002', 1, 3, '英语口语启蒙', '李老师', 24, 1800.00, 0);
