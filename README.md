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
| **AI 智能排课（共建）** | **A**：讯飞星火 API、优化建议、可视化周课表页面；**B**：规则冲突检测（教师时间、教室容量、学生人数）与排课数据统计 |
| **AI 学情分析（共建）** | **A**：讯飞星火 API、自然语言转分析与报告生成、ECharts 图表、PDF 导出；**B**：SQL 安全校验、学情数据查询、报告持久化、家长端报告浏览 |

> 仓库中还包含课表 CRUD、教学进度、课程订单、异常考勤等管理页面源码；**家访管理**已接入班主任专有菜单与家长端；**教师课表**已统一为「我的课表」并支持班主任兼课；PC 端学生/考试/考勤/成绩等列表已改为**分组卡片**展示（见 2.4）；**AI 智能排课**、**AI 学情分析** 已接入管理员菜单，均由 A、B **各负责一半**（见第四节 4.5、4.6、4.7）。

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

项目按功能模块分为两部分，两人工作量相当：

- **同学 A**：平台基础与档案中心 — 登录鉴权、**权限与用户管理**、人员班级档案、公告留言、小程序登录与个人中心、**班主任专有** PC 端能力（本班学生/家长、**家访管理**），以及 **AI 智能排课之大模型对接与可视化前端**、**AI 学情分析之大模型与 PC 展示**。
- **同学 B**：教学运营与家校服务 — 课程教务、购课订单、家长端聚合 API、**教师共用** PC 端能力（我的课程/课表、考试、授课考勤/成绩），以及 **AI 智能排课之规则检测与数据基础**、**AI 学情分析之数据查询与家长端**（含**家访记录浏览**）。

**协作关系：** B 依赖 A 提供的用户/班级/教师基础数据；教师分级与 `scopeMode` 由 A 的 `TeacherScopeService` 与 B 教务接口共建；**教师课表**与**家访**按「班主任专有 / 教师共用与家长侧」由 A、B 各负责一半（见 4.7）；**AI 智能排课** 由 B 提供规则与统计数据、A 提供星火 API 与页面，通过 `/schedule/ai/analyze` 串联；**AI 学情分析** 由 B 提供考勤/成绩数据与安全查询、A 提供星火分析与图表页面，通过 `/report/ai/analyze` 串联。

---

### 4.1 同学 A — 第一部分：平台基础与档案中心

**定位：** 搭建系统骨架，完成人员组织、**权限与用户全生命周期管理**、公告与留言，定义教师级别与 `scopeMode` 数据范围，实现班主任 **专有菜单**（本班学生/家长、**家访管理**），并完成 **AI 排课大模型对接与可视化前端**、**AI 学情分析大模型与 PC 展示**。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（3）** | 用户登录、通知公告浏览、个人信息维护 | 家长账号绑定、首页与个人中心 |
| **管理员（7）** | 权限管理、公告管理、学生管理、教师管理、家长管理、班级管理、留言管理 | **权限管理含用户增删改查**；**学生管理按班级分组**；班级管理配置班主任与**教室容量** |
| **管理员（共建 ½）** | AI 智能排课 — **大模型与展示** | 讯飞星火 API 对接、AI 优化建议解析、「AI 深度分析」、**可视化周课表**页面 |
| **管理员（共建 ½）** | AI 学情分析 — **大模型与展示** | 讯飞星火 Text-to-SQL 与报告生成、ECharts 图表适配、**一键导出 PDF**、`LearningReport` 页面 |
| **教师 — 班主任专有（6）** | 本班学生、本班家长、**家访管理**、本班请假/考勤/成绩（与 B 共建页面） | 只读浏览 + 家访/本班教务维护，**仅本班数据**（`scopeMode=homeroom`）；学生/考勤/成绩列表**分组展示** |
| **教师 — 共用（7）** | 公告浏览、我的课程、**我的课表**、考试管理、授课考勤、授课成绩、**AI 学情分析** | 所有教师可见；`scopeMode=teaching` 按本人负责课程过滤；列表**分组展示** |
| **共建能力** | 教师级别与数据范围 | `TeacherScopeService` + 前端 `useTeacherScope.js`（`teaching` / `homeroom`）；班主任兼课时可同时使用两类范围 |

#### 主要代码范围

**后端 `demo/`**

- 公共基础：`DemoApplication.java`、`application.yaml`、`training.sql`
- 用户与权限：`User`（含 `teacherLevel`、`teacherId` 登录扩展）、`UserController`（用户 CRUD）、`UserServiceImpl`
- 档案模块：`Teacher`、`Student`、`Parent`、`Clazz`、`Announcement`、`Message` 及对应 Mapper/Service/Controller
- 教师范围：`TeacherScopeService`、`TeacherScopedServiceSupport`、班级/课表班级 ID 查询（`ClazzMapper.findIdsByHeadTeacherId` 等）
- **班主任家访（A 部分）**：`HomeVisitServiceImpl` 教师范围校验；`HomeVisitController` 范围参数；`HomeVisitManage.vue` 班主任模式（自动关联教师、仅本班学生）
- **教师菜单（A 部分）**：`Home.vue` 统一「任课老师功能」+「班主任专有」分栏；`router/index.js` 中 `teacherLevels` 与 `scopeMode` 路由元信息
- **AI 排课（A 部分）**：`ScheduleAiController`、`ScheduleAiService`（分析编排、`includeAi` 参数）；`SparkAiService`、`SparkProperties`；`ScheduleAiResult` 组装与 AI 建议合并；`application-local.yaml.example`
- **AI 学情分析（A 部分）**：`LearningReportController`（`/report/ai/analyze` 等）；`LearningReportServiceImpl` 内星火调用（Text-to-SQL、`applyInsightsWithAi`）；`ChartConfig` 等 DTO 解析；复用 `SparkAiService`

**PC 端 `vue-demo/`**

- 框架：`main.js`、`App.vue`、`router/index.js`（教师 `teacherLevels` 路由守卫）、`utils/request.js`
- 页面：`Login`、`Home`（**任课老师功能 + 班主任专有** 分菜单）、`PermissionManage`（**增删改查**）、`TeacherManage`、**`StudentManage`（按班级分组）**、`ParentManage`、`ClassManage`、`AnnouncementManage`、`MessageManage`、**`TeacherSchedule`**（统一**我的课表**）、**`HomeVisitManage`**（班主任家访）、**`ScheduleAiAssistant`**（管理员可视化课表）、**`LearningReport`**（学情分析）
- 公共：`composables/useTeacherScope.js`（`teaching` / `homeroom`）；**`utils/groupTeachingData.js`**（`groupByClass` 等分组函数）；**`assets/manage.css`**（`.data-group` 分组卡片样式）；API：`user`、`teacher`、`student`、`parent`、`clazz`、`announcement`、`message`、**`homeVisit`**、**`schedule`**、**`scheduleAi`**、**`learningReport`**

**小程序 `miniprograme/`**

- 公共：`app.js`、`app.json`、`app.wxss`、`utils/*`、`styles/common.wxss`
- 页面：`login`、`index`（首页）、`announcement/list`、`profile`

#### Git 提交建议

```
feat(part1): 平台基础、权限用户管理、档案中心、班主任专有菜单/家访与 AI 大模型前端
```

---

### 4.2 同学 B — 第二部分：教学运营与家校服务

**定位：** 完成课程、教务、购课及家长端业务浏览，实现三端业务闭环；负责 **教师共用菜单**（我的课程/课表、考试、授课考勤/成绩）、**家长端家访记录**、**AI 排课规则检测与数据基础**、**AI 学情分析数据查询与家长端**；维护 `training.sql` 演示数据（含班主任兼课场景）。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（10）** | 课程浏览/购买/订单、课程表、考勤、考试、成绩、**学情报告**、**请假**、**家访记录**、留言 | 家长端业务闭环；课表/考勤/考试/成绩/家访等按**学生分块**展示；购课含详情校验与**订单取消**；学情报告仅展示教师推送内容 |
| **管理员（3）** | 课程管理、考试管理、考勤管理 | 课程含年级/授课方式/有效期等扩展字段；考试/考勤列表**按课程→班级分组** |
| **管理员（共建 ½）** | AI 智能排课 — **规则与数据** | 教师时间/教室占用/人数超限**冲突检测**；班级人数、教室容量统计；规则层调整建议 |
| **管理员（共建 ½）** | AI 学情分析 — **数据与安全** | `SqlSafetyValidator` 只读 SQL 校验；`SchemaProvider`；`JdbcTemplate` 查询；`learning_report` 表与 Mapper；`training.sql` 学情演示数据 |
| **教师 — 共用（7）** | 公告浏览、我的课程、**我的课表**、考试管理、授课考勤、授课成绩、**AI 学情分析** | 所有教师可见；`scopeMode=teaching` |
| **共建能力** | 班主任教务页面 | 本班请假、本班考勤、本班成绩（复用 B 页面，经 A 的 `TeacherScope` + `homeroom` 过滤） |
| **共建能力（½+½）** | **教师课表** | **B**：`ClassScheduleMapper` 查询、`/schedule/list` 与 `schedule.js`；**A**：`TeacherSchedule.vue`、统一路由 `teacher/schedule` |
| **共建能力（½+½）** | **家长端家访** | **B**：`ParentAppService.getHomeVisits`、`pages/homevisit/list`；**A**：家访数据由班主任 PC 登记后同步展示 |

#### 主要代码范围

**后端 `demo/`**

- 家长端聚合 API：`ParentAppController`、`ParentAppService`、`ParentAppServiceImpl`（请假、**订单支付/取消**、购课校验、**家访记录**）
- 教务模块：`Course`（扩展购课字段）、`Exam`、`Score`、`Attendance`、`LeaveRequest`、`ClassSchedule`、`CourseOrder`
- 扩展模块：`AbnormalAttendance`、`HomeVisit`、`TeachingProgress`
- **任课教师授课范围（B 部分）**：`TeacherScopedServiceSupport.teachingCourseIds`；`ExamServiceImpl` / `AttendanceServiceImpl` / `ScoreServiceImpl` 在 `teacherLevel=1` 时按 **`course.teacher_id`** 过滤，支持班主任兼课仅看本人授课科目
- **任课教师课表（B 部分）**：`ClassScheduleMapper.findByTeacherId` / `findByClassIds`；`ClassScheduleController` 范围列表与 `/schedule/semesters`；`ClassScheduleServiceImpl` 合并本班与本人授课
- **PC 分组列表（B 部分）**：`utils/groupTeachingData.js`（`groupExamsByCourseClass`、`groupAttendanceByCourseClass`、`groupScoresByCourseExam` 等）；`ExamManage`、`AttendanceManage`、`ScoreManage` 分组卡片布局
- **演示数据（B 部分）**：`training.sql` 中 `teacher3`（王老师）语文任课；`teacher1`（张老师）班主任**兼**英语口语；课程/课表/订单/考试/考勤/成绩与授课教师对齐
- **AI 排课（B 部分）**：`ScheduleConflict` 等 DTO；`ScheduleAiServiceImpl` 内**规则冲突检测**（`detectConflicts`）、**规则建议**（`buildRuleSuggestions`）；`StudentMapper.countByClassId`；课表学期查询（`findBySemester` / `findSemesters`）；演示冲突初始数据（`training.sql` 课表记录）
- **AI 学情分析（B 部分）**：`SqlSafetyValidator`、`SchemaProvider`；`LearningReportMapper`；`LearningReportServiceImpl` 内 SQL 执行、教师范围过滤、报告持久化、预置查询模板；`ParentAppController` 家长报告接口；`training.sql` 中考勤/成绩/学生演示数据

**PC 端 `vue-demo/`**

- 页面：`CourseManage`（含课程扩展字段表单）、**`ExamManage`（课程→班级分组）**、**`AttendanceManage`（课程→班级分组）**、**`ScoreManage`（课程→考试分组）**、`LeaveManage`、`ScheduleManage`、`ProgressManage`、`CourseOrderManage`、**`TeacherSchedule`**（**我的课表**）等
- API：`course`、`exam`、`attendance`、`score`、`leave`、**`schedule`** 等（教师端按 `scopeMode` 附带范围参数）
- 共享更新：`router/index.js`（教师共用 + 班主任专有路由）、`Home.vue`（**任课老师功能 + 班主任专有** 分菜单）、**`utils/groupTeachingData.js`**、**`assets/manage.css`**

**小程序 `miniprograme/`**

- 页面：`course`（列表筛选 + 详情购课）、`order`（列表/详情，**取消待支付**）、`schedule`、`attendance`、`exam`、`score`、**`report`**（学情报告列表/详情）、`message`、**`leave`**、**`homevisit`**（家访记录）
- 公共：`styles/table.wxss`、`utils/studentGroup.js`、`utils/format.js`（课程/考勤/**家访方式**等格式化）
- 共享更新：`app.json`、首页与个人中心入口（含家访记录）

#### Git 提交建议

```
feat(part2): 教学运营、家长服务、任课课表/家访浏览与 AI 排课/学情分析数据层
```

---

### 4.3 分工对照总表

| 对比项 | 同学 A（第一部分） | 同学 B（第二部分） |
|--------|-------------------|-------------------|
| 核心职责 | 谁能登录、管哪些人、管哪个班 | 教什么课、怎么展示、家长看什么 |
| 管理员侧重 | 权限用户 CRUD、人员班级档案（**学生按班级分组**）、留言公告 | 课程（含扩展字段）/ 考试 / 考勤维护（**分组列表**） |
| **PC 分组列表** | **`StudentManage`** 按班级分组（A） | **`groupTeachingData.js`**、考试/考勤/成绩分组页面（B） |
| **AI 智能排课** | **星火 API**、AI 建议解析、**可视化课表**、`ScheduleAiAssistant` | **规则冲突检测**、人数/容量统计、规则建议、`training.sql` 演示数据 |
| **AI 学情分析** | **星火 API**、Text-to-SQL/报告生成、**ECharts 图表**、PDF 导出、`LearningReport` | **SQL 安全校验**、只读查询、报告持久化、`training.sql` 学情数据、家长端 `report` 页面 |
| **教师课表** | **TeacherSchedule.vue**、班主任课表合并逻辑、`Home.vue` 统一菜单 | **ClassScheduleMapper** 查询、`/schedule/list`、`schedule.js` |
| **家访管理** | **班主任 PC** 登记/维护、`HomeVisitManage` | **家长端浏览**、`/home-visits`、`pages/homevisit/list` |
| **班主任兼课** | 菜单统一、`scopeMode` 路由元信息 | `training.sql` 张老师兼英语口语；`teachingCourseIds` 按课程过滤授课数据 |
| 教师 PC — 班主任 | 本班学生/家长/家访 + 共用授课菜单 | 本班请假/考勤/成绩（页面与接口，数据由 scope 过滤） |
| 教师 PC — 任课教师 | 共用「任课老师功能」菜单（与班主任相同部分） | 我的课程、我的课表、考试、授课考勤/成绩（按本人负责课程） |
| 后端关键点 | 登录扩展、`User` CRUD、`TeacherScopeService`、**`SparkAiService`**、`/schedule/ai/*`、`/report/ai/*` 星火编排、**家访/菜单班主任侧** | `ParentApp`、**购课校验**、订单支付/取消、**`teachingCourseIds`**、**`/schedule/list`**、**`detectConflicts`**、**`SqlSafetyValidator`**、`learning_report` |
| 小程序 | 登录、首页、公告、个人中心 | 课程/订单、课表/考勤/考试/成绩（**按学生表格**）、**学情报告**、**请假**、**家访记录**、留言 |
| 依赖关系 | **先完成**，提供档案与用户基础 | 依赖 A 的登录与档案；提供规则检测与学情数据；与 A **共建** AI 排课、AI 学情分析与教师分级 |

### 4.4 教师级别 — 二人协作边界（答辩可参考）

```
                    ┌─────────────────────────────────────┐
                    │           同学 A 负责               │
                    │  用户角色、教师级别、班级班主任       │
                    │  TeacherScopeService + scopeMode        │
                    │  班主任专有：本班学生/家长、家访管理    │
                    │  任课老师功能菜单 + 班主任专有分支          │
                    │  AI 排课：星火 API 与可视化前端       │
                    │  AI 学情分析：星火分析与 PC 展示      │
                    └─────────────────┬───────────────────┘
                                      │ 范围参数 scopeUserId + teacherLevel
                    ┌─────────────────▼───────────────────┐
                    │           同学 B 负责               │
                    │  教务 CRUD 接口 + 教师端列表过滤    │
                    │  教师共用：我的课程/课表、授课教务    │
                    │  家长端：家访记录浏览 + 聚合 API    │
                    │  AI 排课：规则检测与数据；学情数据层  │
                    └─────────────────────────────────────┘
```

| 登录账号 | 级别 | 谁主要实现 | 登录后看到什么 |
|----------|------|------------|----------------|
| `teacher1` | 班主任（张老师，兼英语口语） | A 菜单 + B 教务页 | **任课老师功能**全部 + **班主任专有**（本班学生/家长、请假、家访、本班考勤/成绩）；**我的课表**含本班课表与本人授课；授课教务仅见英语口语 |
| `teacher2` | 任课教师（李老师） | B 菜单 + B 授课过滤 | 仅 **任课老师功能**（我的课程/课表、考试、授课考勤/成绩、AI 学情） |
| `teacher3` | 任课教师（王老师） | B 菜单 + B 授课过滤 | 仅 **任课老师功能**；语文任课（一年级1班） |

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

### 4.7 教师课表与家访管理 — A、B 分工（答辩可参考）

教师课表与家长家访为 **2 个完整功能**，各按「班主任专有 / 教师共用与家长侧」协作：

#### 教师课表（统一「我的课表」）

```
  ┌──────────────── 同学 A：菜单与班主任视图 ────────────────┐
  │ • Home.vue「任课老师功能」+「班主任专有」分栏                  │
  │ • ClassScheduleService 班主任分支（本班课表 + 本人授课合并）│
  │ • TeacherSchedule.vue 统一页面（高亮「我授课」）           │
  └──────────────────────┬────────────────────────────┘
                         │ GET /schedule/list?scopeUserId&teacherLevel
  ┌──────────────────────▼────────────────────────────┐
  │           同学 B：课表查询与任课范围               │
  │ • ClassScheduleMapper.findByTeacherId / findByClassIds │
  │ • /schedule/semesters、schedule.js、scopeMode 封装 │
  │ • 路由 teacher/schedule（所有教师共用）            │
  └───────────────────────────────────────────────────┘
```

| 分工 | 负责内容 | 主要文件（参考） |
|------|----------|------------------|
| **同学 A** | 统一菜单、班主任课表合并逻辑 | `ClassScheduleServiceImpl`（level=2 合并）；`TeacherSchedule.vue`；`Home.vue` |
| **同学 B** | 课表查询 API 与 scopeMode | `ClassScheduleMapper.xml`；`ClassScheduleController`；`api/schedule.js`；`useTeacherScope.js` |
| **共同** | 课表页面与演示数据 | `TeacherSchedule.vue`；`training.sql` 课表记录 |

**演示建议：** `teacher1` → **我的课表**（本班全部课程 + 本人英语口语高亮）；`teacher2` / `teacher3` → **我的课表**（仅本人授课时段）。

#### 家访管理

```
  ┌──────────────── 同学 A：班主任家访登记 ────────────────┐
  │ • HomeVisitService 本班学生范围校验与教师自动关联      │
  │ • HomeVisitController scopeUserId + teacherLevel     │
  │ • HomeVisitManage.vue、路由 teacher/home-visit       │
  └──────────────────────┬────────────────────────────┘
                         │ 数据写入 home_visit 表
  ┌──────────────────────▼────────────────────────────┐
  │           同学 B：家长端家访浏览                     │
  │ • ParentAppService.getHomeVisits 按孩子过滤         │
  │ • GET /app/parent/{parentId}/home-visits           │
  │ • pages/homevisit/list（按学生分块）                │
  └───────────────────────────────────────────────────┘
```

| 分工 | 负责内容 | 主要文件（参考） |
|------|----------|------------------|
| **同学 A** | **班主任 PC** 家访增删改查 | `HomeVisitServiceImpl`、`HomeVisitController`；`HomeVisitManage.vue`；`api/homeVisit.js` |
| **同学 B** | **家长端** 家访记录展示 | `ParentAppServiceImpl.getHomeVisits`；`pages/homevisit/list.*`；`utils/format.js` `visitType` |
| **共同** | 演示数据 | `training.sql` 王小明家访记录（班主任张老师登记） |

**演示建议：** `teacher1` PC「家访管理」新增或查看记录 → `parent1` 小程序「家访记录」按王小明分块查看。

#### 班主任兼课（统一菜单模型）

| 分工 | 负责内容 |
|------|----------|
| **同学 A** | PC 菜单：`任课老师功能` + `班主任专有`；`scopeMode` 路由；`StudentManage` 按班级分组；`TeacherSchedule` 合并视图 |
| **同学 B** | `training.sql`：张老师(teacher1) 任班主任并兼任英语口语；`teachingCourseIds` 按课程过滤；考试/考勤/成绩分组列表 |

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
| `feat: 教师统一菜单与班主任专有分支` | 任课老师功能共用 + 班主任专有菜单、`scopeMode` 数据范围 | 共同 |
| `feat(teacher-schedule): 统一我的课表与班主任兼课` | 课表合并查询、TeacherSchedule、training.sql 兼课数据 | 共同 |
| `feat(home-visit-a): 班主任家访管理` | 家访范围校验、PC 家访管理页与菜单 | 同学 A |
| `feat(home-visit-b): 家长端家访记录浏览` | 家长聚合 API、小程序 homevisit 页面 | 同学 B |
| `feat: 班主任兼课演示数据` | training.sql 张老师兼任英语口语、课表/订单/考试/考勤/成绩对齐 | 同学 B |
| `feat: PC 分组列表展示` | 学生按班级、考试/考勤按课程→班级、成绩按课程→考试分组；`groupTeachingData.js` | 共同 |

---

**心田花开培训机构综合管理平台** — 综合实验项目
