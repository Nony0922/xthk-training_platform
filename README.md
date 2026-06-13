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

系统共包含 **31 个功能模块**（含 AI 智能排课、AI 学情分析），按使用端与角色划分如下。

### 2.1 小程序端（家长）— 共 12 项

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
| 留言 | 向机构提交留言、查看回复 |
| 个人信息维护 | 查看/修改家长资料、关联学生信息 |

小程序 TabBar：**首页 | 课程 | 订单 | 我的**

其他入口：请假在首页「功能服务」与「我的」页；课表 / 考勤 / 考试 / 成绩 / **学情报告**在首页「功能服务」。

#### 家长端核心流程

**请假：** 家长（`parent1` / 王家长）小程序提交 → 班主任 PC「本班请假」审批 → 家长查看结果；待审批可撤回。

**购课：** 课程列表筛选 → 详情确认信息 → 创建订单 → 支付（支付成功更新课程 `enrolled_count`）或取消待支付订单。

**多孩家庭：** 王家长关联王小明（一年级1班）、李小红（二年级数学提高班）；课表 / 考勤 / 考试 / 成绩页面均按孩子独立分块，避免数据混淆。

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
| 学生管理 | 学生档案增删改查 |
| 教师管理 | 教师档案增删改查（任课教师 / 班主任） |
| 家长管理 | 家长档案增删改查 |
| 班级管理 | 班级信息及班主任配置 |
| 课程管理 | 课程信息维护、上下架；含适用年级、学科、授课方式、有效日期、地点、名额、亮点等字段 |
| 考试管理 | 考试安排维护 |
| 考勤管理 | 学生考勤记录维护 |
| **AI 智能排课（共建）** | **A**：讯飞星火 API、优化建议、可视化周课表页面；**B**：规则冲突检测（教师时间、教室容量、学生人数）与排课数据统计 |
| **AI 学情分析（共建）** | **A**：讯飞星火 API、自然语言转分析与报告生成、ECharts 图表、PDF 导出；**B**：SQL 安全校验、学情数据查询、报告持久化、家长端报告浏览 |

> 仓库中还包含课表 CRUD、教学进度、课程订单、家访、异常考勤等管理页面源码；**AI 智能排课**、**AI 学情分析** 已接入管理员菜单，均由 A、B **各负责一半**（见第四节 4.5、4.6）。

---

### 2.3 PC 端 — 教师 — 按级别划分（共 9 项能力，菜单不同）

教师端与管理端共用页面组件，通过路由 `readOnly` 控制只读。**班主任**与**任课教师**菜单、数据范围、默认首页均不同：

| 教师级别 | 菜单与能力 | 数据范围 |
|----------|------------|----------|
| **班主任**（`teacherLevel=2`） | 公告浏览、**本班学生**、**本班家长**、**本班请假**、**本班考勤**、**本班成绩**、**AI 学情分析** | 仅 `head_teacher_id` 关联的本班学生及家长；**本班请假**为审批家长提交的申请 |
| **任课教师**（`teacherLevel=1`） | 公告浏览、**我的课程**、**考试管理**、**授课考勤**、**授课成绩**、**AI 学情分析** | 本人**任课课程**及课表涉及班级；考试列表按「授课班级 **或** 本人课程」匹配 |

测试账号：`teacher1`（班主任）默认进入本班学生；`teacher2`（任课教师）默认进入我的课程。

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
- 登录鉴权：返回用户角色及 `parentId` / `teacherLevel` 等扩展字段
- **AI 智能排课**（`/schedule/ai/*`，A+B 共建）：
  - **A**：讯飞星火 HTTP API 调用、AI 建议解析与合并、PC 可视化课表与「AI 深度分析」交互
  - **B**：规则引擎（教师/教室/容量冲突）、班级人数与教室容量统计、规则层优化建议
- **AI 学情分析**（`/report/ai/*` 与 `/app/parent/*/report*`，A+B 共建）：
  - **A**：讯飞星火自然语言转 SQL、学情报告与图表配置生成、PC `LearningReport` 页面（ECharts、PDF 导出）
  - **B**：`SqlSafetyValidator` 只读查询、考勤/成绩演示数据、`learning_report` 持久化、教师数据范围、家长端报告列表/详情

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
│       └── router/       # 路由与权限守卫
├── miniprograme/         # 微信小程序（家长端）
│   ├── app.js / app.json
│   ├── pages/            # 各功能页面（含 course、order、leave、schedule 等）
│   ├── styles/           # common.wxss、table.wxss（学生分块表格样式）
│   └── utils/            # request、auth、format、studentGroup（按学生分组）等
└── README.md
```

---

## 四、A、B 分工说明

项目按功能模块分为两部分，两人工作量相当：

- **同学 A**：平台基础与档案中心 — 登录鉴权、**权限与用户管理**、人员班级档案、公告留言、小程序登录与个人中心、**班主任** PC 端能力，以及 **AI 智能排课之大模型对接与可视化前端**、**AI 学情分析之大模型与 PC 展示**。
- **同学 B**：教学运营与家校服务 — 课程教务、购课订单、家长端聚合 API、**任课教师** PC 端能力，以及 **AI 智能排课之规则检测与数据基础**、**AI 学情分析之数据查询与家长端**。

**协作关系：** B 依赖 A 提供的用户/班级/教师基础数据；教师分级由 A 的 `TeacherScopeService` 与 B 教务接口共建；**AI 智能排课** 由 B 提供规则与统计数据、A 提供星火 API 与页面，通过 `/schedule/ai/analyze` 串联；**AI 学情分析** 由 B 提供考勤/成绩数据与安全查询、A 提供星火分析与图表页面，通过 `/report/ai/analyze` 串联。

---

### 4.1 同学 A — 第一部分：平台基础与档案中心

**定位：** 搭建系统骨架，完成人员组织、**权限与用户全生命周期管理**、公告与留言，定义教师级别与班主任数据范围，并完成 **AI 排课大模型对接与可视化前端**、**AI 学情分析大模型与 PC 展示**。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（3）** | 用户登录、通知公告浏览、个人信息维护 | 家长账号绑定、首页与个人中心 |
| **管理员（7）** | 权限管理、公告管理、学生管理、教师管理、家长管理、班级管理、留言管理 | **权限管理含用户增删改查**；班级管理配置班主任与**教室容量** |
| **管理员（共建 ½）** | AI 智能排课 — **大模型与展示** | 讯飞星火 API 对接、AI 优化建议解析、「AI 深度分析」、**可视化周课表**页面 |
| **管理员（共建 ½）** | AI 学情分析 — **大模型与展示** | 讯飞星火 Text-to-SQL 与报告生成、ECharts 图表适配、**一键导出 PDF**、`LearningReport` 页面 |
| **教师 — 班主任（4）** | 公告浏览、本班学生、本班家长、**AI 学情分析** | 只读浏览，**仅本班数据**；学情分析按本班范围查询 |
| **教师 — 任课教师（1）** | **AI 学情分析** | 按授课班级范围生成学情报告 |
| **共建能力** | 教师级别与数据范围 | `TeacherScopeService`：班主任按本班、任课教师按授课班级过滤（供 B 教务与 AI 排课复用） |

#### 主要代码范围

**后端 `demo/`**

- 公共基础：`DemoApplication.java`、`application.yaml`、`training.sql`
- 用户与权限：`User`（含 `teacherLevel`、`teacherId` 登录扩展）、`UserController`（用户 CRUD）、`UserServiceImpl`
- 档案模块：`Teacher`、`Student`、`Parent`、`Clazz`、`Announcement`、`Message` 及对应 Mapper/Service/Controller
- 教师范围：`TeacherScopeService`、`TeacherScopedServiceSupport`、班级/课表班级 ID 查询（`ClazzMapper.findIdsByHeadTeacherId` 等）
- **AI 排课（A 部分）**：`ScheduleAiController`、`ScheduleAiService`（分析编排、`includeAi` 参数）；`SparkAiService`、`SparkProperties`；`ScheduleAiResult` 组装与 AI 建议合并；`application-local.yaml.example`
- **AI 学情分析（A 部分）**：`LearningReportController`（`/report/ai/analyze` 等）；`LearningReportServiceImpl` 内星火调用（Text-to-SQL、`applyInsightsWithAi`）；`ChartConfig` 等 DTO 解析；复用 `SparkAiService`

**PC 端 `vue-demo/`**

- 框架：`main.js`、`App.vue`、`router/index.js`（教师 `teacherLevels` 路由守卫）、`utils/request.js`
- 页面：`Login`、`Home`（班主任 / 任课教师分菜单）、`PermissionManage`（**增删改查**）、`TeacherManage`、`StudentManage`、`ParentManage`、`ClassManage`、`AnnouncementManage`、`MessageManage`、**`ScheduleAiAssistant`**（可视化课表）、**`LearningReport`**（学情分析）
- 公共：`composables/useTeacherScope.js`；API：`user`、`teacher`、`student`、`parent`、`clazz`、`announcement`、`message`、**`scheduleAi`**、**`learningReport`**

**小程序 `miniprograme/`**

- 公共：`app.js`、`app.json`、`app.wxss`、`utils/*`、`styles/common.wxss`
- 页面：`login`、`index`（首页）、`announcement/list`、`profile`

#### Git 提交建议

```
feat(part1): 平台基础、权限用户管理、档案中心、班主任范围与 AI 排课/学情分析大模型前端
```

---

### 4.2 同学 B — 第二部分：教学运营与家校服务

**定位：** 完成课程、教务、购课及家长端业务浏览，实现三端业务闭环；负责 **AI 排课规则检测与数据基础**、**AI 学情分析数据查询与家长端**，以及**任课教师** PC 端菜单及授课侧数据过滤。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（9）** | 课程浏览/购买/订单、课程表、考勤、考试、成绩、**学情报告**、**请假**、留言 | 家长端业务闭环；课表等按**学生分块表格**展示；购课含详情校验与**订单取消**；学情报告仅展示教师推送内容 |
| **管理员（3）** | 课程管理、考试管理、考勤管理 | 课程含年级/授课方式/有效期等扩展字段 |
| **管理员（共建 ½）** | AI 智能排课 — **规则与数据** | 教师时间/教室占用/人数超限**冲突检测**；班级人数、教室容量统计；规则层调整建议 |
| **管理员（共建 ½）** | AI 学情分析 — **数据与安全** | `SqlSafetyValidator` 只读 SQL 校验；`SchemaProvider`；`JdbcTemplate` 查询；`learning_report` 表与 Mapper；`training.sql` 学情演示数据 |
| **教师 — 任课教师（6）** | 公告浏览、我的课程、考试管理、授课考勤、授课成绩、**AI 学情分析** | **仅本人课程与授课班级数据** |
| **共建能力** | 班主任教务页面 | 本班请假、本班考勤、本班成绩（复用 B 页面，经 A 的 `TeacherScope` 过滤） |

#### 主要代码范围

**后端 `demo/`**

- 家长端聚合 API：`ParentAppController`、`ParentAppService`、`ParentAppServiceImpl`（请假、**订单支付/取消**、购课校验）
- 教务模块：`Course`（扩展购课字段）、`Exam`、`Score`、`Attendance`、`LeaveRequest`、`ClassSchedule`、`CourseOrder`
- 扩展模块：`AbnormalAttendance`、`HomeVisit`、`TeachingProgress`
- **AI 排课（B 部分）**：`ScheduleConflict` 等 DTO；`ScheduleAiServiceImpl` 内**规则冲突检测**（`detectConflicts`）、**规则建议**（`buildRuleSuggestions`）；`StudentMapper.countByClassId`；课表学期查询（`findBySemester` / `findSemesters`）；演示冲突初始数据（`training.sql` 课表记录）
- **AI 学情分析（B 部分）**：`SqlSafetyValidator`、`SchemaProvider`；`LearningReportMapper`；`LearningReportServiceImpl` 内 SQL 执行、教师范围过滤、报告持久化、预置查询模板；`ParentAppController` 家长报告接口；`training.sql` 中考勤/成绩/学生演示数据

**PC 端 `vue-demo/`**

- 页面：`CourseManage`（含课程扩展字段表单）、`ExamManage`、`AttendanceManage`、`ScoreManage`、`LeaveManage`、`ScheduleManage`、`ProgressManage`、`CourseOrderManage` 等
- API：`course`、`exam`、`attendance`、`score`、`leave`、`schedule` 等（教师端自动附带范围参数）
- 共享更新：`router/index.js`（任课 / 班主任分路由）、`Home.vue`（分菜单）

**小程序 `miniprograme/`**

- 页面：`course`（列表筛选 + 详情购课）、`order`（列表/详情，**取消待支付**）、`schedule`、`attendance`、`exam`、`score`、**`report`**（学情报告列表/详情）、`message`、**`leave`**
- 公共：`styles/table.wxss`、`utils/studentGroup.js`、`utils/format.js`（课程/考勤等格式化）
- 共享更新：`app.json`、首页与个人中心入口

#### Git 提交建议

```
feat(part2): 教学运营、家长服务、任课教师功能与 AI 排课/学情分析数据层
```

---

### 4.3 分工对照总表

| 对比项 | 同学 A（第一部分） | 同学 B（第二部分） |
|--------|-------------------|-------------------|
| 核心职责 | 谁能登录、管哪些人、管哪个班 | 教什么课、怎么展示、家长看什么 |
| 管理员侧重 | 权限用户 CRUD、人员班级档案、留言公告 | 课程（含扩展字段）/ 考试 / 考勤维护 |
| **AI 智能排课** | **星火 API**、AI 建议解析、**可视化课表**、`ScheduleAiAssistant` | **规则冲突检测**、人数/容量统计、规则建议、`training.sql` 演示数据 |
| **AI 学情分析** | **星火 API**、Text-to-SQL/报告生成、**ECharts 图表**、PDF 导出、`LearningReport` | **SQL 安全校验**、只读查询、报告持久化、`training.sql` 学情数据、家长端 `report` 页面 |
| 教师 PC — 班主任 | 本班学生 / 家长浏览；班级与 `head_teacher_id` 配置 | 本班请假（审批家长申请）/ 考勤 / 成绩（页面与接口，数据由 A 过滤） |
| 教师 PC — 任课教师 | — | 我的课程、考试、授课考勤 / 成绩（按授课班级与课程过滤） |
| 后端关键点 | 登录扩展、`User` CRUD、`TeacherScopeService`、**`SparkAiService`**、`/schedule/ai/*`、`/report/ai/*` 星火编排 | `ParentApp`、**购课校验**、订单支付/取消、**`detectConflicts`**、**`SqlSafetyValidator`**、`learning_report` |
| 小程序 | 登录、首页、公告、个人中心 | 课程/订单、课表/考勤/考试/成绩（**按学生表格**）、**学情报告**、**请假**、留言 |
| 依赖关系 | **先完成**，提供档案与用户基础 | 依赖 A 的登录与档案；提供规则检测与学情数据；与 A **共建** AI 排课、AI 学情分析与教师分级 |

### 4.4 教师级别 — 二人协作边界（答辩可参考）

```
                    ┌─────────────────────────────────────┐
                    │           同学 A 负责               │
                    │  用户角色、教师级别、班级班主任       │
                    │  TeacherScopeService（本班/授课范围）│
                    │  AI 排课：星火 API 与可视化前端       │
                    │  AI 学情分析：星火分析与 PC 展示      │
                    └─────────────────┬───────────────────┘
                                      │ 范围参数 scopeUserId + teacherLevel
                    ┌─────────────────▼───────────────────┐
                    │           同学 B 负责               │
                    │  教务 CRUD 接口 + 教师端列表过滤    │
                    │  任课教师菜单路由 + AI 规则与数据   │
                    │  AI 学情分析：SQL 安全与家长端报告  │
                    └─────────────────────────────────────┘
```

| 登录账号 | 级别 | 谁主要实现 | 登录后看到什么 |
|----------|------|------------|----------------|
| `teacher1` | 班主任 | A 菜单 + B 本班教务页 | 本班学生、家长、请假、考勤、成绩、**AI 学情分析** |
| `teacher2` | 任课教师 | B 菜单 + B 授课过滤 | 我的课程、考试、授课考勤、成绩、**AI 学情分析** |

### 4.5 AI 智能排课 — A、B 分工（答辩可参考）

AI 智能排课为 **1 个完整功能**，按「大模型与展示 / 数据与规则」平分给 A、B：

```
  ┌──────────────── 同学 B：规则与数据层 ────────────────┐
  │ • 教师时间冲突、教室占用冲突、人数/容量超限检测      │
  │ • 班级人数统计、教室容量映射、规则层调整建议         │
  │ • 课表学期查询、演示冲突数据（training.sql）       │
  └──────────────────────┬────────────────────────────┘
                         │ GET /schedule/ai/analyze
                         │ （先规则检测，includeAi=false 即可出结果）
  ┌──────────────────────▼────────────────────────────┐
  │           同学 A：大模型与展示层                     │
  │ • SparkAiService 调用讯飞星火 HTTP API             │
  │ • AI 建议 JSON 解析、与规则建议合并                │
  │ • ScheduleAiAssistant 可视化周课表 + AI 深度分析   │
  └───────────────────────────────────────────────────┘
```

| 分工 | 负责内容 | 主要文件（参考） |
|------|----------|------------------|
| **同学 A** | **星火 API** 与 PC **可视化页面** | `SparkAiServiceImpl`、`ScheduleAiController`；`ScheduleAiAssistant.vue`、`api/scheduleAi.js` |
| **同学 B** | 三类约束的**规则检测**与统计 | `ScheduleAiServiceImpl.detectConflicts`、`buildRuleSuggestions`；`StudentMapper.countByClassId`；`Clazz.capacity` |
| **共同** | 分析接口与结果 DTO | `ScheduleAiService.analyze`、`ScheduleAiResult`；`includeAi` 参数（快速检测 / AI 深度分析） |

**演示建议：** 进入页面先自动完成规则检测（B）；点击「AI 深度分析」触发星火建议（A，需配置 `application-local.yaml`）。

### 4.6 AI 学情分析 — A、B 分工（答辩可参考）

AI 学情分析为 **1 个完整功能**，按「大模型与展示 / 数据与安全」平分给 A、B：

```
  ┌──────────────── 同学 B：数据与安全层 ────────────────┐
  │ • SqlSafetyValidator 只读 SQL 白名单校验             │
  │ • SchemaProvider 学情表结构、JdbcTemplate 查询执行   │
  │ • learning_report 持久化、教师/家长数据范围过滤      │
  │ • training.sql 考勤/成绩演示数据、小程序 report 页   │
  └──────────────────────┬────────────────────────────┘
                         │ POST /report/ai/analyze
                         │ （先安全查询，再由 A 调用星火生成报告）
  ┌──────────────────────▼────────────────────────────┐
  │           同学 A：大模型与展示层                   │
  │ • SparkAiService 自然语言转 SQL、学情报告生成      │
  │ • 图表配置 JSON 解析、ECharts 柱状图/饼图/折线图   │
  │ • LearningReport 页面、一键导出 PDF、历史报告      │
  └───────────────────────────────────────────────────┘
```

| 分工 | 负责内容 | 主要文件（参考） |
|------|----------|------------------|
| **同学 A** | **星火 API** 与 PC **学情分析页面** | `SparkAiServiceImpl`（复用）、`LearningReportServiceImpl`（`generateSqlWithAi`、`applyInsightsWithAi`）；`LearningReport.vue`、`api/learningReport.js` |
| **同学 B** | **SQL 安全**、查询执行与**家长端** | `SqlSafetyValidator`、`SchemaProvider`；`LearningReportMapper`；`ParentAppController` 报告接口；`pages/report/list`、`detail`；`training.sql` 学情数据 |
| **共同** | 分析接口与结果 DTO | `LearningReportService.analyze`、`LearningReportResult`；`publishToParent` 参数（仅生成 / 生成并推送家长） |

**演示建议：**

1. 使用 `teacher1` 登录 PC → **AI 学情分析**
2. 点击快捷问题填入分析问题（**不会自动生成**，需手动点击按钮）
3. 点击 **「生成学情报告」** 或 **「生成并推送家长」**
4. 推荐问题（数据范围选「一年级1班」）：
   - `统计一年级1班各考勤状态的人数分布`（饼图/柱状图 + 表格 + 分析正文）
   - `查询一年级1班语文期中考试各学生成绩及排名`（柱状图 + 排名表 + 分析正文）
5. 推送后使用 `parent1` 小程序 → **学情报告** 查看

**说明：** 界面不展示 SQL 语句；报告正文为自然语言，不含数据库术语。未配置 `spark.api-password` 时自动降级为预置查询模板 + 本地图表。

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

**AI 星火配置说明（同学 A 负责配置，同学 B 提供演示数据）**

1. 登录 [讯飞开放平台](https://console.xfyun.cn/) → 我的应用 → 进入已创建应用
2. 在 **http 服务接口认证信息** 中复制 **APIPassword**
3. 创建 `demo/src/main/resources/application-local.yaml`：

```yaml
spark:
  api-password: 你的APIPassword
```

4. **AI 智能排课**：管理员登录 PC → **学校管理 → AI 智能排课** → 点击「AI 深度分析」；初始数据中 `2025春季` 学期含教师时间冲突，便于演示
5. **AI 学情分析**：`teacher1` 登录 → **AI 学情分析** → 选择快捷问题 → 点击「生成学情报告」；`training.sql` 已含一年级1班 5 名学生考勤/成绩演示数据

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
| teacher1 | 123456 | 班主任（本班管理） | PC |
| teacher2 | 123456 | 任课教师（授课管理） | PC |
| parent1 | 123456 | 家长（王家长，关联王小明、李小红） | 小程序 |

#### 功能演示建议

| 场景 | 操作 |
|------|------|
| **请假** | `parent1` 小程序「请假申请」查看/提交/撤回；`teacher1` PC「本班请假」审批（初始有一条王小明待审批病假） |
| **购课** | `parent1` → 课程 Tab → 筛选线下/线上 → 详情页查看年级/有效期/方式 → 加入订单或支付 |
| **取消订单** | `parent1` → 订单 Tab → 待支付订单「英语口语启蒙」（`ORD20250315002`）→ 取消订单 |
| **多孩数据** | `parent1` → 课表/考勤/考试/成绩 → 王小明与李小红分块表格展示 |
| **AI 学情分析** | `teacher1` → AI 学情分析 → 选「统计一年级1班各考勤状态的人数分布」→ 生成学情报告 → 可导出 PDF；点「生成并推送家长」后 `parent1` 小程序「学情报告」可查看 |

---

## 七、注意事项

1. **不要提交** `node_modules/`、`demo/target/`、`.idea/`、`application-local.yaml` 等（已在 `.gitignore` 中配置）
2. PC 端家长账号会提示「请使用小程序端访问」
3. 小程序 TabBar 图标需本地生成，未纳入版本库
4. 未配置 `spark.api-password` 时，AI 排课仍可使用规则引擎检测冲突，AI 学情分析仍可使用预置查询模板，但不会调用大模型生成建议/报告
5. **数据库**：请完整执行 `demo/src/main/resources/training.sql`（唯一数据库脚本，含建库、建表及全部演示数据）

---

## 八、协作提交记录

| 提交 | 说明 | 负责人 |
|------|------|--------|
| `chore: add gitignore` | 添加忽略规则 | 共同 |
| `feat(part1): 平台基础、权限用户管理、档案中心、班主任范围与 AI 排课大模型前端` | 第一部分代码 | 同学 A |
| `feat(part2): 教学运营、家长服务、任课教师功能与 AI 排课规则检测` | 第二部分代码 | 同学 B |
| `feat(ai-schedule-spark): AI 排课星火 API 与可视化课表` | 大模型对接、前端页面 | 同学 A |
| `feat(ai-schedule-rules): AI 排课规则冲突检测与数据统计` | 规则引擎、容量/人数统计 | 同学 B |
| `feat(ai-learning-report-spark): AI 学情分析星火 API 与 PC 展示` | Text-to-SQL、报告生成、ECharts、PDF 导出 | 同学 A |
| `feat(ai-learning-report-data): AI 学情分析 SQL 安全与家长端报告` | 只读查询、报告持久化、小程序学情报告 | 同学 B |
| `feat: 教师分级菜单与数据范围` | 班主任 / 任课教师分权（A+B 协作） | 共同 |
| `feat: 家长端请假与班主任审批` | 小程序请假申请/撤回，本班请假审批 | 共同 |
| `feat: 家长端教务表格与购课增强` | 按学生分块表格、课程扩展字段、订单取消 | 同学 B |

---

**心田花开培训机构综合管理平台** — 综合实验项目
