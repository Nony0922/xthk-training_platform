# 心田花开培训机构综合管理平台

本仓库为**心田花开培训机构综合管理平台**的完整源代码，面向培训机构日常运营场景，支持 **PC 端（管理员 / 教师）** 与 **微信小程序端（家长）** 三类角色协同使用。

---

## 一、项目简介

| 子项目 | 技术栈 | 说明 |
|--------|--------|------|
| `demo/` | Spring Boot 3 + MyBatis + MySQL | 后端 REST API，端口 8080 |
| `vue-demo/` | Vue 3 + Vite + Vue Router | PC 管理端（管理员、教师） |
| `miniprograme/` | 微信小程序 | 家长端服务 |

数据库名：`training_platform`（脚本见 `demo/src/main/resources/training.sql`）

---

## 二、系统完整功能说明

系统共包含 **34 个功能模块**（含 AI 智能排课、AI 学情分析、教师课表、家访管理等），按使用端与角色划分如下。

### 2.1 小程序端（家长）— 共 13 项

| 功能 | 说明 |
|------|------|
| 用户登录 | 家长账号登录，自动绑定 `parentId` 与家长档案 |
| 通知公告浏览 | 查看面向家长的公告列表 |
| 课程浏览 | 浏览已上架课程；支持**全部 / 线下 / 线上 / 混合**筛选；卡片展示适用年级、学科、有效日期、地点、剩余名额等 |
| 课程购买与订单 | 详情页完整信息购课；创建订单、支付、**取消待支付订单**；订单列表/详情展示课程关键信息 |
| 课程表浏览 | 按关联**学生分块**、**表格**展示各孩子所在班级的上课安排（星期、时间、课程、教师、教室） |
| 考勤浏览 | 按**学生分块**、**表格**展示个人考勤（日期、课程、班级、状态标签） |
| 考试安排浏览 | 按**学生分块**、**表格**展示所在班级的考试计划（日期、名称、时间、地点、状态） |
| 成绩浏览 | 按**学生分块**、**表格**展示个人成绩（考试、分数、排名） |
| **学情报告浏览** | 查看教师推送的 AI 学情分析报告（概述、图表、表格、分析正文） |
| **请假申请** | 为关联学生提交请假；查看审批状态与备注；**待审批**时可撤回 |
| **家访记录浏览** | 按关联**学生分块**展示班主任登记的家访记录（日期、方式、内容、家长反馈、后续计划） |
| 留言 | 向机构提交留言、查看回复 |
| 个人信息维护 | 查看/修改家长资料、关联学生信息 |

小程序 TabBar：**首页 | 课程 | 订单 | 我的**

其他入口：请假、**家访记录**在首页「功能服务」与「我的」页；课表 / 考勤 / 考试 / 成绩 / **学情报告**在首页「功能服务」。

#### 家长端核心流程

**请假：** 家长（`parent1` / 王家长）小程序提交 → 班主任 PC「本班请假」审批 → 家长查看结果；待审批可撤回。

**家访：** 班主任（`teacher1` / 张老师）PC「家访管理」登记本班学生家访 → 家长（`parent1`）小程序「家访记录」按孩子查看；初始数据含王小明一条电话家访记录。

**购课：** 课程列表筛选 → 详情确认信息 → 创建订单 → 支付（支付成功更新课程 `enrolled_count`）或取消待支付订单。

**多孩家庭：** 王家长关联王小明（一年级1班）、李小红（二年级数学提高班）；课表 / 考勤 / 考试 / 成绩 / **家访记录**页面均按孩子独立分块，避免数据混淆。

---

### 2.2 PC 端 — 管理员 — 共 12 项

#### 权限管理（1 项）

| 功能 | 说明 |
|------|------|
| 权限管理 | 系统用户增删改查，分配角色（管理员 / 教师 / 家长）及教师级别 |

#### 学校管理（11 项）

| 功能 | 说明 |
|------|------|
| 留言管理 | 查看、回复家长留言 |
| 公告管理 | 发布、编辑、删除通知公告 |
| 学生管理 | 学生档案增删改查；**按班级分组卡片**展示，班级内按姓名排序 |
| 教师管理 | 教师档案增删改查（任课教师 / 班主任） |
| 家长管理 | 家长档案增删改查 |
| 班级管理 | 班级信息及班主任配置 |
| 课程管理 | 课程信息维护、上下架；含适用年级、学科、授课方式、有效日期、地点、名额、亮点等字段 |
| 考试管理 | 考试安排维护；**按课程 → 班级分组**展示 |
| 考勤管理 | 学生考勤记录维护；**按课程 → 班级分组**展示 |
| **AI 智能排课** | 管理员 PC 可视化课表与 AI 深度分析；规则冲突检测与数据统计（A、B 各主责一个子模块，见第四节） |
| **AI 学情分析** | 教师 PC 自然语言报告生成与 PDF 导出；家长小程序学情报告浏览（A、B 各主责一个子模块，见第四节） |

> 仓库中还包含课表 CRUD、教学进度、课程订单、异常考勤等管理页面源码；PC 端学生/考试/考勤/成绩等列表已改为**分组卡片**展示（见 2.4）；A、B 按**功能模块**分工，每模块含 PC、小程序、后端、数据库四层（见第四节）。

---

### 2.3 PC 端 — 教师 — 统一菜单 + 班主任专有分支

教师端与管理端共用页面组件，通过路由 `readOnly`、`teacherLevels` 与 `scopeMode` 控制权限。**所有教师共用同一套「任课老师功能」菜单**；若账号为班主任（`teacherLevel=2`），额外显示 **「班主任专有」** 分支，任课教师（`teacherLevel=1`）看不到该分支。

**班主任可以同时任课**（如 `teacher1` 张老师：既任一年级1班班主任，又兼任英语口语授课）。考试/考勤/成绩等列表与管理员端一致，采用**分组卡片**展示，避免课程、班级、考试名称逐条重复。

#### 任课老师功能（所有教师可见）

| 菜单 | 说明 | 数据范围（`scopeMode=teaching`） |
|------|------|----------------------------------|
| 公告浏览 | 只读查看公告 | 全部可见公告 |
| 我的课程 | 本人负责的课程 | `course.teacher_id` = 本人 |
| 我的课表 | 周课表可视化 | 任课：本人授课安排；班主任：本班完整课表 + 本人授课（合并去重） |
| 考试管理 | 考试安排维护；**按课程 → 班级分组** | 本人负责课程下的考试（`course.teacher_id`） |
| 授课考勤 | 授课考勤维护；**按课程 → 班级分组** | 本人负责课程的考勤记录 |
| 授课成绩 | 授课成绩维护；**按课程 → 考试分组** | 本人负责课程相关考试成绩 |
| AI 学情分析 | 自然语言学情报告 | 按教师身份与所选班级/学生 |

#### 班主任专有（仅 `teacherLevel=2` 可见）

| 菜单 | 说明 | 数据范围（`scopeMode=homeroom`） |
|------|------|----------------------------------|
| 本班学生 | 只读浏览；**按班级分组** | `head_teacher_id` 关联班级学生 |
| 本班家长 | 只读浏览 | 上述学生的家长 |
| 本班请假 | 审批请假 | 本班学生请假 |
| 家访管理 | 登记/维护家访 | 仅本班学生 |
| 本班考勤 | 本班考勤维护；**按课程 → 班级分组** | 本班全部课程考勤 |
| 本班成绩 | 本班成绩维护；**按课程 → 考试分组** | 本班全部考试/成绩 |

测试账号：

| 账号 | 角色 | 默认首页 | 说明 |
|------|------|----------|------|
| `teacher1` | 班主任（张老师） | 公告浏览 | 一年级1班班主任，**兼任**英语口语；侧边栏含「任课老师功能」+「班主任专有」 |
| `teacher2` | 任课教师（李老师） | 公告浏览 | 数学任课；仅「任课老师功能」菜单 |
| `teacher3` | 任课教师（王老师） | 公告浏览 | 语文任课（一年级1班）；仅「任课老师功能」菜单 |

---

### 2.4 后端核心能力

- 全模块标准 CRUD 接口（`/user`、`/teacher`、`/student` 等）
- 家长端聚合 API（`/app/*`）：按家长 ID 过滤学生、课表、考勤、成绩、订单、请假等
- **课程模型扩展**（`Course`）：`targetGrade`、`subject`、`teachMode`（线下/线上/混合）、`location`、`validStart` / `validEnd`、`classTimeDesc`、`maxStudents` / `enrolledCount`、`suitableAge`、`highlights`
- **购课校验**（`CourseService.validatePurchasable`）：上架状态、有效期、名额；支付成功后 `incrementEnrolledCount`
- **考试状态**（`ExamStatusUtil`）：根据 `examDate` + `startTime` / `endTime` 与当前时间自动判定未开始 / 进行中 / 已结束
- **订单接口**：
  - 创建 `POST /app/parent/order`
  - 支付 `PUT /app/parent/order/{id}/pay`
  - 取消 `PUT /app/parent/{parentId}/order/{orderId}/cancel`（仅待支付）
- **请假接口**：提交 `/app/parent/{parentId}/leave`、列表 `/leaves`、撤回 `/leave/{leaveId}/withdraw`；班主任审批 `/leave/list`（`scopeUserId` + `teacherLevel`）
- **家访接口**：班主任维护 `/home-visit/*`（`scopeMode=homeroom` 限定本班学生）；家长浏览 `GET /app/parent/{parentId}/home-visits`
- **教师课表接口**：`GET /schedule/list`（`scopeUserId` + `teacherLevel` + 可选 `semester`）；任课教师返回本人授课安排；班主任返回**本班课表与本人授课合并**；`GET /schedule/semesters` 学期列表
- **教师数据范围**：接口通过 `scopeUserId` + `teacherLevel` 过滤；前端 `scopeMode` 区分 **teaching**（任课，`teacherLevel=1`）与 **homeroom**（班主任，`teacherLevel=2`），支持班主任兼课场景；`teaching` 模式下考试/考勤/成绩按 **`course.teacher_id`（本人负责课程）** 过滤，而非整班全部科目
- **PC 端分组列表**：`utils/groupTeachingData.js` + `assets/manage.css`；学生按班级、考试/考勤按课程→班级、成绩按课程→考试分组展示，管理员与教师共用同一套页面组件
- 登录鉴权：返回用户角色及 `parentId` / `teacherLevel` 等扩展字段
- **AI 智能排课**、**AI 学情分析**：各拆为 A、B 两个子模块，主责划分见第四节

---

## 三、项目结构

```
xthk-training_platform/
├── demo/                 # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/   # Controller / Service / Entity / Mapper
│       └── resources/
│           ├── application.yaml
│           ├── training.sql    # 建库 + 初始数据
│           └── mapper/         # MyBatis XML
├── vue-demo/             # Vue 3 PC 前端
│   ├── package.json
│   └── src/
│       ├── views/        # 页面组件
│       ├── api/          # 接口封装
│       ├── utils/        # request、groupTeachingData（PC 分组列表）等
│       ├── assets/       # manage.css（分组卡片样式）
│       └── router/       # 路由与权限守卫
├── miniprograme/         # 微信小程序（家长端）
│   ├── app.js / app.json
│   ├── pages/            # 各功能页面（含 course、order、leave、schedule、homevisit 等）
│   ├── styles/           # common.wxss、table.wxss（学生分块表格样式）
│   └── utils/            # request、auth、format、studentGroup（按学生分组）等
└── README.md
```

---

## 四、A、B 分工说明

本项目由 **同学 A、同学 B 两人完成**。分工严格按 **功能模块** 划分：每个模块由唯一主责人负责，且必须覆盖该模块的 **PC 端、小程序端、后端、数据库** 全部四层，不得按「端」或「技术层」拆分主责。

### 分工原则

| 原则 | 说明 |
|------|------|
| **按功能模块分** | 以业务功能为划分单位（如「考试管理」「家访管理」），不按 PC/小程序/后端分列主责 |
| **四层一体** | 主责人对所负责模块的 PC 页面、小程序页面、后端接口、数据库表及演示数据负全部责任 |
| **唯一主责** | 每个功能模块（含 AI 子模块）有且只有一个主责人 |
| **A、B 均覆盖全系统** | A 主责 8 个模块、B 主责 9 个模块，合起来覆盖三端全部业务与两项 AI 功能 |

```
  分工维度：功能模块（非按端、非按技术层）
  ┌─────────────────────────────────────────────────────────┐
  │  每个模块 = PC端 + 小程序端 + 后端 + 数据库（四层一体）   │
  └─────────────────────────────────────────────────────────┘
        同学 A 主责 8 个模块          同学 B 主责 9 个模块
   登录/档案/公告/留言/菜单/家访     课程/订单/课表/考试/考勤/成绩/请假
        AI排课·展示  AI学情·展示          AI排课·规则  AI学情·数据
```

---

### 4.1 功能模块主责总表

| 序号 | 功能模块 | 主责 | PC 端 | 小程序端 | 后端 | 数据库 |
|------|----------|------|-------|----------|------|--------|
| 1 | 登录鉴权与权限管理 | **A** | `Login`、`PermissionManage` | `pages/login` | `UserController`、`UserService`、登录鉴权 | `user` 表及演示账号 |
| 2 | 人员档案管理 | **A** | `StudentManage`、`TeacherManage`、`ParentManage`、`ClassManage`；教师端本班学生/家长浏览 | `pages/profile`（关联学生） | `Teacher/Student/Parent/Clazz` 全套 CRUD | `teacher`、`student`、`parent`、`clazz` 表 |
| 3 | 公告管理 | **A** | `AnnouncementManage`、教师公告浏览 | `pages/announcement/list` | `AnnouncementController`；`ParentAppService.getAnnouncements` | `announcement` 表 |
| 4 | 留言管理 | **A** | `MessageManage` | `pages/message` | `MessageController`；家长留言与回复 API | `message` 表 |
| 5 | 教师端菜单与数据范围 | **A** | `Home.vue` 菜单；`router` 中 `scopeMode`、`teacherLevels` | — | `TeacherScopeService`、`TeacherScopedServiceSupport` | `user.teacher_level`、`clazz.head_teacher_id` 等 |
| 6 | 家访管理 | **A** | `HomeVisitManage`（班主任） | `pages/homevisit/list` | `HomeVisitController/Service`；`ParentAppService.getHomeVisits` | `home_visit` 表 |
| 7 | AI 智能排课 · 大模型与展示 | **A** | `ScheduleAiAssistant`（可视化课表、AI 深度分析） | — | `SparkAiService`、`ScheduleAiController`、AI 建议编排 | 读取 `class_schedule`、`clazz`（课表模块所建表） |
| 8 | AI 学情分析 · 大模型与 PC 展示 | **A** | `LearningReport`（ECharts、PDF 导出） | — | `LearningReportServiceImpl` 星火调用、`/report/ai/*` | `learning_report` 表（报告生成写入） |
| 9 | 课程管理 | **B** | `CourseManage`、教师「我的课程」 | `pages/course` | `CourseController/Service`、`validatePurchasable` | `course` 表 |
| 10 | 购课订单 | **B** | — | `pages/order` | `CourseOrder`、`ParentApp` 下单/支付/取消 | `course_order` 表 |
| 11 | 课表管理 | **B** | `TeacherSchedule` | `pages/schedule` | `ClassScheduleController/Service`、`/schedule/list` | `class_schedule` 表 |
| 12 | 考试管理 | **B** | `ExamManage`（管理员 + 教师授课） | `pages/exam` | `ExamController/Service`、`ExamStatusUtil` | `exam` 表 |
| 13 | 考勤管理 | **B** | `AttendanceManage`（管理员 + 教师） | `pages/attendance` | `AttendanceController/Service` | `attendance` 表 |
| 14 | 成绩管理 | **B** | `ScoreManage`（管理员 + 教师） | `pages/score` | `ScoreController/Service` | `score` 表 |
| 15 | 请假管理 | **B** | `LeaveManage`（班主任审批） | `pages/leave` | `LeaveRequest`、`ParentApp` 请假/撤回 | `leave_request` 表 |
| 16 | AI 智能排课 · 规则检测与数据统计 | **B** | `ScheduleAiAssistant`（规则冲突展示） | — | `ScheduleAiServiceImpl.detectConflicts`、`buildRuleSuggestions` | `class_schedule`、`clazz` 表演示冲突数据 |
| 17 | AI 学情分析 · 数据查询与家长端 | **B** | — | `pages/report` | `SqlSafetyValidator`、`LearningReportMapper`、`ParentApp` 报告接口 | `learning_report` 表；`attendance`、`score` 演示数据 |

---

### 4.2 同学 A — 主责模块明细（8 个）

#### 模块 1：登录鉴权与权限管理

| 层级 | 内容 |
|------|------|
| PC 端 | `Login.vue`、`PermissionManage.vue` |
| 小程序端 | `pages/login`（家长登录、`parentId` 绑定） |
| 后端 | `UserController`、`UserServiceImpl`；登录返回 `role`、`teacherLevel`、`parentId` |
| 数据库 | `user` 表；`admin`、`teacher1~3`、`parent1` 演示账号 |

#### 模块 2：人员档案管理

| 层级 | 内容 |
|------|------|
| PC 端 | `StudentManage`（按班级分组）、`TeacherManage`、`ParentManage`、`ClassManage`；教师端本班学生/家长只读浏览 |
| 小程序端 | `pages/profile`（家长资料、关联学生） |
| 后端 | `Teacher/Student/Parent/Clazz` 的 Controller、Service、Mapper |
| 数据库 | `teacher`、`student`、`parent`、`clazz` 表及关联演示数据 |

#### 模块 3：公告管理

| 层级 | 内容 |
|------|------|
| PC 端 | `AnnouncementManage`（管理员 CRUD）；教师公告浏览（卡片展示正文） |
| 小程序端 | `pages/announcement/list` |
| 后端 | `AnnouncementController`；`ParentAppService.getAnnouncements`（按角色过滤） |
| 数据库 | `announcement` 表 |

#### 模块 4：留言管理

| 层级 | 内容 |
|------|------|
| PC 端 | `MessageManage`（查看、回复） |
| 小程序端 | `pages/message` |
| 后端 | `MessageController`；家长留言提交与回复查询 API |
| 数据库 | `message` 表 |

#### 模块 5：教师端菜单与数据范围

| 层级 | 内容 |
|------|------|
| PC 端 | `Home.vue`（任课老师功能 + 班主任专有菜单）；`router/index.js`（`scopeMode`、`teacherLevels`、`readOnly`） |
| 小程序端 | — |
| 后端 | `TeacherScopeService`、`TeacherScopedServiceSupport`；`scopeUserId` + `teacherLevel` 过滤 |
| 数据库 | `user.teacher_level`；`clazz.head_teacher_id`；兼课相关字段设计 |

#### 模块 6：家访管理

| 层级 | 内容 |
|------|------|
| PC 端 | `HomeVisitManage`（班主任登记/维护，仅本班学生） |
| 小程序端 | `pages/homevisit/list`（按学生分块） |
| 后端 | `HomeVisitController/ServiceImpl`；`ParentAppService.getHomeVisits` |
| 数据库 | `home_visit` 表（含王小明演示记录） |

#### 模块 7：AI 智能排课 · 大模型与展示

| 层级 | 内容 |
|------|------|
| PC 端 | `ScheduleAiAssistant.vue`（周课表可视化、「AI 深度分析」） |
| 小程序端 | — |
| 后端 | `SparkAiService`、`ScheduleAiController`；AI 建议解析与 `ScheduleAiResult` 合并 |
| 数据库 | 读取课表模块的 `class_schedule`、`clazz`；`application-local.yaml` 星火配置 |

#### 模块 8：AI 学情分析 · 大模型与 PC 展示

| 层级 | 内容 |
|------|------|
| PC 端 | `LearningReport.vue`（快捷问题、ECharts 图表、PDF 导出、历史报告） |
| 小程序端 | — |
| 后端 | `LearningReportServiceImpl`（`generateSqlWithAi`、`applyInsightsWithAi`）；`/report/ai/analyze` |
| 数据库 | `learning_report` 表（报告标题、图表配置、分析正文等写入） |

---

### 4.3 同学 B — 主责模块明细（9 个）

#### 模块 9：课程管理

| 层级 | 内容 |
|------|------|
| PC 端 | `CourseManage`（扩展字段、上下架）；教师「我的课程」 |
| 小程序端 | `pages/course`（筛选、详情） |
| 后端 | `CourseController/Service`；`validatePurchasable`、`incrementEnrolledCount` |
| 数据库 | `course` 表（含 `targetGrade`、`teachMode`、`maxStudents` 等） |

#### 模块 10：购课订单

| 层级 | 内容 |
|------|------|
| PC 端 | — |
| 小程序端 | `pages/order`（列表、详情、支付、取消待支付） |
| 后端 | `CourseOrder`；`ParentApp` 创建/支付/取消订单 |
| 数据库 | `course_order` 表（含 `ORD20250315002` 待支付演示） |

#### 模块 11：课表管理

| 层级 | 内容 |
|------|------|
| PC 端 | `TeacherSchedule.vue`（周课表；班主任本班+兼课合并；高亮本人授课） |
| 小程序端 | `pages/schedule`（按学生分块表格） |
| 后端 | `ClassScheduleController/ServiceImpl`；`/schedule/list`、`/schedule/semesters` |
| 数据库 | `class_schedule` 表；`training.sql` 课表演示数据 |

#### 模块 12：考试管理

| 层级 | 内容 |
|------|------|
| PC 端 | `ExamManage`（按课程→班级分组；管理员 + 教师授课） |
| 小程序端 | `pages/exam`（按学生分块） |
| 后端 | `ExamController/Service`；`teachingCourseIds` 授课范围过滤 |
| 数据库 | `exam` 表 |

#### 模块 13：考勤管理

| 层级 | 内容 |
|------|------|
| PC 端 | `AttendanceManage`（按课程→班级分组） |
| 小程序端 | `pages/attendance`（按学生分块） |
| 后端 | `AttendanceController/Service` |
| 数据库 | `attendance` 表 |

#### 模块 14：成绩管理

| 层级 | 内容 |
|------|------|
| PC 端 | `ScoreManage`（按课程→考试分组） |
| 小程序端 | `pages/score`（按学生分块） |
| 后端 | `ScoreController/Service` |
| 数据库 | `score` 表 |

#### 模块 15：请假管理

| 层级 | 内容 |
|------|------|
| PC 端 | `LeaveManage`（班主任本班请假审批） |
| 小程序端 | `pages/leave`（申请、撤回、查看状态） |
| 后端 | `LeaveRequest`；`ParentApp` 请假提交/撤回；班主任审批接口 |
| 数据库 | `leave_request` 表（含王小明待审批演示） |

#### 模块 16：AI 智能排课 · 规则检测与数据统计

| 层级 | 内容 |
|------|------|
| PC 端 | `ScheduleAiAssistant.vue`（规则冲突列表、人数/容量统计展示） |
| 小程序端 | — |
| 后端 | `ScheduleAiServiceImpl.detectConflicts`、`buildRuleSuggestions`；`StudentMapper.countByClassId` |
| 数据库 | `class_schedule`、`clazz.capacity` 演示冲突数据（`2025春季` 学期） |

#### 模块 17：AI 学情分析 · 数据查询与家长端

| 层级 | 内容 |
|------|------|
| PC 端 | — |
| 小程序端 | `pages/report/list`、`pages/report/detail` |
| 后端 | `SqlSafetyValidator`、`SchemaProvider`；`LearningReportMapper`；`ParentApp` 报告列表/详情 |
| 数据库 | `learning_report` 表（读取推送记录）；`attendance`、`score` 学情演示数据 |

**B 另负责：** `groupTeachingData.js`、`manage.css`（考试/考勤/成绩 PC 分组列表，归入模块 12~14）；`training.sql` 中课程/课表/订单/兼课（张老师兼英语口语）演示数据。

---

### 4.4 模块数量对照

| 对比项 | 同学 A（8 个模块） | 同学 B（9 个模块） |
|--------|-------------------|-------------------|
| 平台与档案 | 登录权限、人员档案、公告、留言、教师菜单 | — |
| 教学教务 | — | 课程、订单、课表、考试、考勤、成绩、请假 |
| 家校服务 | 家访 | 购课订单、请假、学情报告（小程序） |
| AI 功能 | 排课·大模型与展示、学情·大模型与 PC 展示 | 排课·规则与数据、学情·数据与家长端 |
| 答辩演示 | 模块 1~8 端到端 | 模块 9~17 端到端 |

---

### 4.5 答辩演示建议（按模块）

| 主责 | 推荐演示模块 | 操作要点 |
|------|-------------|----------|
| **A** | 登录鉴权与权限管理 | `admin` 登录 → 权限管理增删用户 |
| **A** | 人员档案管理 | 学生管理按班级分组；`teacher1` 查看本班学生/家长 |
| **A** | 公告管理 | 发布公告 → `teacher1` 公告浏览看正文 → `parent1` 小程序查看 |
| **A** | 家访管理 | `teacher1` PC 登记 → `parent1` 小程序按孩子查看 |
| **A** | AI 智能排课 · 展示 | 管理员 AI 智能排课 → 点击「AI 深度分析」 |
| **A** | AI 学情分析 · PC 展示 | `teacher1` 生成学情报告 → 导出 PDF |
| **B** | 课程管理 + 购课订单 | `parent1` 课程浏览购课 → 订单支付或取消 |
| **B** | 课表管理 | `teacher1` 本班+兼课课表；`parent1` 小程序课表分块 |
| **B** | 考试/考勤/成绩管理 | `teacher2` 授课教务分组列表；`parent1` 小程序浏览 |
| **B** | 请假管理 | `parent1` 提交请假 → `teacher1` 本班请假审批 |
| **B** | AI 智能排课 · 规则 | 管理员 AI 智能排课 → 查看规则冲突检测结果 |
| **B** | AI 学情分析 · 家长端 | A 推送报告后 → `parent1` 小程序学情报告查看 |

---

## 五、环境要求与启动步骤

### 5.1 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+（PC 前端）
- MySQL 8.0+
- 微信开发者工具（小程序）

### 5.2 启动步骤

**1. 初始化数据库**

```bash
# 在 MySQL 中执行
demo/src/main/resources/training.sql
```

**2. 配置并启动后端**

```bash
cd demo
# 修改 src/main/resources/application.yaml 中的数据库用户名和密码
# 配置讯飞星火 API（AI 智能排课、AI 学情分析）：
#   复制 application-local.yaml.example 为 application-local.yaml
#   填入控制台 http 服务接口认证信息中的 APIPassword
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

**AI 星火配置说明（模块 7、8 由 A 配置）**

1. 登录 [讯飞开放平台](https://console.xfyun.cn/) → 我的应用 → 进入已创建应用
2. 在 **http 服务接口认证信息** 中复制 **APIPassword**
3. 创建 `demo/src/main/resources/application-local.yaml`：

```yaml
spark:
  api-password: 你的APIPassword
```

4. **AI 智能排课**：模块 16（B）规则检测 → 模块 7（A）AI 深度分析；`2025春季` 学期含冲突演示数据
5. **AI 学情分析**：模块 8（A）PC 生成报告 → 模块 17（B）家长小程序查看；`training.sql` 已含演示数据

**3. 启动 PC 前端**

```bash
cd vue-demo
npm install
npm run dev
```

**4. 启动小程序**

1. 用微信开发者工具导入 `miniprograme` 目录
2. 若 TabBar 图标缺失，在完整项目中执行 `node generate_icons.cjs` 生成 `assets/icons/`
3. 详情 → 本地设置 → 勾选 **不校验合法域名**
4. 确认 `miniprograme/utils/config.js` 中 `BASE_URL` 为 `http://localhost:8080`（真机调试需改为电脑局域网 IP）

---

## 六、测试账号

| 用户名 | 密码 | 角色 | 使用端 |
|--------|------|------|--------|
| admin | 123456 | 管理员 | PC |
| teacher1 | 123456 | 班主任（张老师，兼英语口语） | PC |
| teacher2 | 123456 | 任课教师（李老师，数学） | PC |
| teacher3 | 123456 | 任课教师（王老师，语文） | PC |
| parent1 | 123456 | 家长（王家长，关联王小明、李小红） | 小程序 |

#### 功能演示建议

| 场景 | 操作 |
|------|------|
| **请假** | `parent1` 小程序「请假申请」查看/提交/撤回；`teacher1` PC「本班请假」审批（初始有一条王小明待审批病假） |
| **家访** | `teacher1` PC「班主任专有 → 家访管理」登记本班家访；`parent1` 小程序「家访记录」查看王小明记录（初始有一条 2025-02-20 电话家访） |
| **我的课表** | `teacher1`：本班完整课表 + 本人英语口语高亮；`teacher2`/`teacher3`：仅本人授课安排 |
| **分组列表展示** | `admin` → 学生管理（按班级）、考试/考勤管理（按课程→班级）；`teacher1` → 考试/授课考勤/授课成绩（仅英语口语分组数据） |
| **班主任兼课** | `teacher1` 登录后可见全部「任课老师功能」+「班主任专有」；「我的课程」含英语口语启蒙；授课教务不混入本班语文/数学 |
| **任课教师菜单** | `teacher2`/`teacher3` 登录后仅「任课老师功能」，无「班主任专有」分支 |
| **购课** | `parent1` → 课程 Tab → 筛选线下/线上 → 详情页查看年级/有效期/方式 → 加入订单或支付 |
| **取消订单** | `parent1` → 订单 Tab → 待支付订单「英语口语启蒙」（`ORD20250315002`）→ 取消订单 |
| **多孩数据** | `parent1` → 课表/考勤/考试/成绩/家访 → 王小明与李小红分块表格展示 |
| **AI 学情分析** | `teacher1` → AI 学情分析 → 选「统计一年级1班各考勤状态的人数分布」→ 生成学情报告 → 可导出 PDF；点「生成并推送家长」后 `parent1` 小程序「学情报告」可查看 |

---

## 七、注意事项

1. **不要提交** `node_modules/`、`demo/target/`、`.idea/`、`application-local.yaml` 等（已在 `.gitignore` 中配置）
2. PC 端家长账号会提示「请使用小程序端访问」
3. 小程序 TabBar 图标需本地生成，未纳入版本库
4. 未配置 `spark.api-password` 时，AI 排课仍可使用规则引擎检测冲突，AI 学情分析仍可使用预置查询模板，但不会调用大模型生成建议/报告
5. **数据库**：请完整执行 `demo/src/main/resources/training.sql`（唯一数据库脚本，含建库、建表及全部演示数据）；含 `teacher3`（王老师）语文任课、**张老师(teacher1) 班主任兼英语口语** 等兼课演示数据
6. **教师角色**：`teacher_level=2` 为班主任，额外显示「班主任专有」菜单；所有教师共用「任课老师功能」菜单。接口通过 `scopeMode`（`teaching` / `homeroom`）区分任课与本班数据范围；`teaching` 模式下考试/考勤/成绩按本人负责课程（`course.teacher_id`）过滤
7. **PC 分组列表**：学生管理按班级分组；考试/考勤按课程→班级分组；成绩按课程→考试分组；分组逻辑见 `vue-demo/src/utils/groupTeachingData.js`，样式见 `assets/manage.css`
8. **A/B 分工**：严格按 **17 个功能模块** 划分，每模块含 PC、小程序、后端、数据库四层（详见第四节 4.1）

---

## 八、提交记录

| 提交 | 说明 | 负责人 |
|------|------|--------|
| `chore: add gitignore` | 添加忽略规则 | A、B |
| `feat(mod-01~06): 登录/档案/公告/留言/菜单/家访` | 模块 1~6 四层一体 | A |
| `feat(mod-07~08): AI排课展示、AI学情PC展示` | 模块 7~8 四层一体 | A |
| `feat(mod-09~15): 课程/订单/课表/考试/考勤/成绩/请假` | 模块 9~15 四层一体 | B |
| `feat(mod-16~17): AI排课规则、AI学情家长端` | 模块 16~17 四层一体 | B |
| `feat: PC分组列表与公告正文展示` | 归入模块 2、12~14 | A、B |

---

**心田花开培训机构综合管理平台** — 综合实验项目
