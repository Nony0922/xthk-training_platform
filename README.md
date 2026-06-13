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

系统共包含 **29 个功能模块**（含 AI 智能排课），按使用端与角色划分如下。

### 2.1 小程序端（家长）— 共 10 项

| 功能 | 说明 |
|------|------|
| 用户登录 | 家长账号登录，自动绑定家长信息 |
| 通知公告浏览 | 查看面向家长的公告列表 |
| 课程浏览 | 浏览已上架课程，查看课程详情 |
| 课程购买 | 创建课程订单、模拟支付 |
| 课程表浏览 | 查看关联学生的上课安排 |
| 考勤浏览 | 查看学生考勤记录 |
| 成绩浏览 | 查看学生考试成绩 |
| **请假申请** | 为关联学生提交请假；查看审批状态与备注；**待审批**时可撤回 |
| 留言 | 向机构提交留言、查看回复 |
| 个人信息维护 | 查看/修改家长资料、关联学生信息 |

小程序 TabBar：**首页 | 课程 | 订单 | 我的**（请假入口在首页「功能服务」与「我的」页）

**请假管理流程：** 家长（如 `parent1` / 王家长）在小程序提交请假 → 班主任在 PC「本班请假」中查看并审批 → 家长在小程序查看审批结果；仅**待审批**状态可撤回。

---

### 2.2 PC 端 — 管理员 — 共 11 项

#### 权限管理（1 项）

| 功能 | 说明 |
|------|------|
| 权限管理 | 系统用户增删改查，分配角色（管理员 / 教师 / 家长）及教师级别 |

#### 学校管理（10 项）

| 功能 | 说明 |
|------|------|
| 留言管理 | 查看、回复家长留言 |
| 公告管理 | 发布、编辑、删除通知公告 |
| 学生管理 | 学生档案增删改查 |
| 教师管理 | 教师档案增删改查（任课教师 / 班主任） |
| 家长管理 | 家长档案增删改查 |
| 班级管理 | 班级信息及班主任配置 |
| 课程管理 | 课程信息维护、上下架 |
| 考试管理 | 考试安排维护 |
| 考勤管理 | 学生考勤记录维护 |
| **AI 智能排课（共建）** | **A**：讯飞星火 API、优化建议、可视化周课表页面；**B**：规则冲突检测（教师时间、教室容量、学生人数）与排课数据统计 |

> 仓库中还包含课表 CRUD、教学进度、课程订单、家访、异常考勤等管理页面源码；**AI 智能排课** 已接入管理员菜单，由 A、B **各负责一半**（见第四节 4.5）。

---

### 2.3 PC 端 — 教师 — 按级别划分（共 8 项能力，菜单不同）

教师端与管理端共用页面组件，通过路由 `readOnly` 控制只读。**班主任**与**任课教师**菜单、数据范围、默认首页均不同：

| 教师级别 | 菜单与能力 | 数据范围 |
|----------|------------|----------|
| **班主任**（`teacherLevel=2`） | 公告浏览、**本班学生**、**本班家长**、**本班请假**、**本班考勤**、**本班成绩** | 仅 `head_teacher_id` 关联的本班学生及家长；**本班请假**为审批家长提交的申请 |
| **任课教师**（`teacherLevel=1`） | 公告浏览、**我的课程**、**考试管理**、**授课考勤**、**授课成绩** | 仅本人授课课程及课表涉及的班级数据 |

测试账号：`teacher1`（班主任）默认进入本班学生；`teacher2`（任课教师）默认进入我的课程。

---

### 2.4 后端核心能力

- 全模块标准 CRUD 接口（`/user`、`/teacher`、`/student` 等）
- 家长端聚合 API（`/app/*`）：按家长 ID 过滤学生、课表、考勤、成绩、订单、**请假**等数据
- 请假接口：家长提交 `/app/parent/{parentId}/leave`、列表 `/leaves`、撤回 `/leave/{leaveId}/withdraw`；班主任审批 `/leave/list`（带 `scopeUserId` + `teacherLevel` 过滤）
- 登录鉴权：返回用户角色及 `parentId` / `teacherLevel` 等扩展字段
- 课程订单：自动生成订单号、模拟支付流程
- **AI 智能排课**（`/schedule/ai/*`，A+B 共建）：
  - **A**：讯飞星火 HTTP API 调用、AI 建议解析与合并、PC 可视化课表与「AI 深度分析」交互
  - **B**：规则引擎（教师/教室/容量冲突）、班级人数与教室容量统计、规则层优化建议

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
│   ├── pages/            # 各功能页面
│   └── utils/            # 请求、鉴权、导航等工具
└── README.md
```

---

## 四、A、B 分工说明

项目按功能模块分为两部分，两人工作量相当：

- **同学 A**：平台基础与档案中心 — 登录鉴权、**权限与用户管理**、人员班级档案、公告留言、小程序登录与个人中心、**班主任** PC 端能力，以及 **AI 智能排课之大模型对接与可视化前端**。
- **同学 B**：教学运营与家校服务 — 课程教务、购课订单、家长端聚合 API、**任课教师** PC 端能力，以及 **AI 智能排课之规则检测与数据基础**。

**协作关系：** B 依赖 A 提供的用户/班级/教师基础数据；教师分级由 A 的 `TeacherScopeService` 与 B 教务接口共建；**AI 智能排课** 由 B 提供规则与统计数据、A 提供星火 API 与页面，通过 `/schedule/ai/analyze` 串联。

---

### 4.1 同学 A — 第一部分：平台基础与档案中心

**定位：** 搭建系统骨架，完成人员组织、**权限与用户全生命周期管理**、公告与留言，定义教师级别与班主任数据范围，并完成 **AI 排课大模型对接与可视化前端**。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（3）** | 用户登录、通知公告浏览、个人信息维护 | 家长账号绑定、首页与个人中心 |
| **管理员（7）** | 权限管理、公告管理、学生管理、教师管理、家长管理、班级管理、留言管理 | **权限管理含用户增删改查**；班级管理配置班主任与**教室容量** |
| **管理员（共建 ½）** | AI 智能排课 — **大模型与展示** | 讯飞星火 API 对接、AI 优化建议解析、「AI 深度分析」、**可视化周课表**页面 |
| **教师 — 班主任（3）** | 公告浏览、本班学生、本班家长 | 只读浏览，**仅本班数据** |
| **共建能力** | 教师级别与数据范围 | `TeacherScopeService`：班主任按本班、任课教师按授课班级过滤（供 B 教务与 AI 排课复用） |

#### 主要代码范围

**后端 `demo/`**

- 公共基础：`DemoApplication.java`、`application.yaml`、`training.sql`
- 用户与权限：`User`（含 `teacherLevel`、`teacherId` 登录扩展）、`UserController`（用户 CRUD）、`UserServiceImpl`
- 档案模块：`Teacher`、`Student`、`Parent`、`Clazz`、`Announcement`、`Message` 及对应 Mapper/Service/Controller
- 教师范围：`TeacherScopeService`、`TeacherScopedServiceSupport`、班级/课表班级 ID 查询（`ClazzMapper.findIdsByHeadTeacherId` 等）
- **AI 排课（A 部分）**：`ScheduleAiController`、`ScheduleAiService`（分析编排、`includeAi` 参数）；`SparkAiService`、`SparkProperties`；`ScheduleAiResult` 组装与 AI 建议合并；`application-local.yaml.example`

**PC 端 `vue-demo/`**

- 框架：`main.js`、`App.vue`、`router/index.js`（教师 `teacherLevels` 路由守卫）、`utils/request.js`
- 页面：`Login`、`Home`（班主任 / 任课教师分菜单）、`PermissionManage`（**增删改查**）、`TeacherManage`、`StudentManage`、`ParentManage`、`ClassManage`、`AnnouncementManage`、`MessageManage`、**`ScheduleAiAssistant`**（可视化课表）
- 公共：`composables/useTeacherScope.js`；API：`user`、`teacher`、`student`、`parent`、`clazz`、`announcement`、`message`、**`scheduleAi`**

**小程序 `miniprograme/`**

- 公共：`app.js`、`app.json`、`app.wxss`、`utils/*`、`styles/common.wxss`
- 页面：`login`、`index`（首页）、`announcement/list`、`profile`

#### Git 提交建议

```
feat(part1): 平台基础、权限用户管理、档案中心、班主任范围与 AI 排课大模型前端
```

---

### 4.2 同学 B — 第二部分：教学运营与家校服务

**定位：** 完成课程、教务、购课及家长端业务浏览，实现三端业务闭环；负责 **AI 排课规则检测与数据基础**，以及**任课教师** PC 端菜单及授课侧数据过滤。

#### 负责的功能模块

| 端 | 模块 | 说明 |
|----|------|------|
| **小程序（7）** | 课程购买、课程浏览、课程表浏览、考勤浏览、成绩浏览、**请假申请**、留言 | 家长端业务浏览与购课；**请假**由家长发起，班主任 PC 端审批 |
| **管理员（3）** | 课程管理、考试管理、考勤管理 | 课程与教务维护 |
| **管理员（共建 ½）** | AI 智能排课 — **规则与数据** | 教师时间/教室占用/人数超限**冲突检测**；班级人数、教室容量统计；规则层调整建议 |
| **教师 — 任课教师（5）** | 公告浏览、我的课程、考试管理、授课考勤、授课成绩 | **仅本人课程与授课班级数据** |
| **共建能力** | 班主任教务页面 | 本班请假、本班考勤、本班成绩（复用 B 页面，经 A 的 `TeacherScope` 过滤） |

#### 主要代码范围

**后端 `demo/`**

- 家长端聚合 API：`ParentAppController`、`ParentAppService`、`ParentAppServiceImpl`（含请假提交、列表、撤回）
- 教务模块：`Course`、`Exam`、`Score`、`Attendance`、`LeaveRequest`、`ClassSchedule`、`CourseOrder`（列表接口支持 `scopeUserId` + `teacherLevel` 过滤）
- 扩展模块：`AbnormalAttendance`、`HomeVisit`、`TeachingProgress`
- **AI 排课（B 部分）**：`ScheduleConflict` 等 DTO；`ScheduleAiServiceImpl` 内**规则冲突检测**（`detectConflicts`）、**规则建议**（`buildRuleSuggestions`）；`StudentMapper.countByClassId`；课表学期查询（`findBySemester` / `findSemesters`）；演示冲突初始数据（`training.sql` 课表记录）

**PC 端 `vue-demo/`**

- 页面：`CourseManage`、`ExamManage`、`AttendanceManage`、`ScoreManage`、`LeaveManage`、`ScheduleManage`、`ProgressManage`、`CourseOrderManage` 等
- API：`course`、`exam`、`attendance`、`score`、`leave`、`schedule` 等（教师端自动附带范围参数）
- 共享更新：`router/index.js`（任课 / 班主任分路由）、`Home.vue`（分菜单）

**小程序 `miniprograme/`**

- 页面：`course`、`order`、`schedule`、`attendance`、`exam`、`score`、`message`、**`leave`**
- 共享更新：`app.json`、首页 `pages/index/*`

#### Git 提交建议

```
feat(part2): 教学运营、家长服务、任课教师功能与 AI 排课规则检测
```

---

### 4.3 分工对照总表

| 对比项 | 同学 A（第一部分） | 同学 B（第二部分） |
|--------|-------------------|-------------------|
| 核心职责 | 谁能登录、管哪些人、管哪个班 | 教什么课、怎么展示、家长看什么 |
| 管理员侧重 | 权限用户 CRUD、人员班级档案、留言公告 | 课程考试考勤维护 |
| **AI 智能排课** | **星火 API**、AI 建议解析、**可视化课表**、`ScheduleAiAssistant` | **规则冲突检测**、人数/容量统计、规则建议、`training.sql` 演示数据 |
| 教师 PC — 班主任 | 本班学生 / 家长浏览；班级与 `head_teacher_id` 配置 | 本班请假（审批家长申请）/ 考勤 / 成绩（页面与接口，数据由 A 过滤） |
| 教师 PC — 任课教师 | — | 我的课程、考试、授课考勤 / 成绩（按授课班级与课程过滤） |
| 后端关键点 | 登录扩展、`User` CRUD、`TeacherScopeService`、**`SparkAiService`**、`/schedule/ai/*` 编排 | `ParentApp`、订单支付、**`detectConflicts`** |
| 小程序 | 登录、首页、公告、个人中心 | 课程、订单、课表、考勤、成绩、**请假**、留言 |
| 依赖关系 | **先完成**，提供档案与用户基础 | 依赖 A 的登录与档案；提供规则检测数据；与 A **共建** AI 排课与教师分级 |

### 4.4 教师级别 — 二人协作边界（答辩可参考）

```
                    ┌─────────────────────────────────────┐
                    │           同学 A 负责               │
                    │  用户角色、教师级别、班级班主任       │
                    │  TeacherScopeService（本班/授课范围）│
                    │  AI 排课：星火 API 与可视化前端       │
                    └─────────────────┬───────────────────┘
                                      │ 范围参数 scopeUserId + teacherLevel
                    ┌─────────────────▼───────────────────┐
                    │           同学 B 负责               │
                    │  教务 CRUD 接口 + 教师端列表过滤    │
                    │  任课教师菜单路由 + AI 规则与数据   │
                    └─────────────────────────────────────┘
```

| 登录账号 | 级别 | 谁主要实现 | 登录后看到什么 |
|----------|------|------------|----------------|
| `teacher1` | 班主任 | A 菜单 + B 本班教务页 | 本班学生、家长、请假、考勤、成绩 |
| `teacher2` | 任课教师 | B 菜单 + B 授课过滤 | 我的课程、考试、授课考勤、成绩 |

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
# 配置讯飞星火 API（AI 智能排课助手）：
#   复制 application-local.yaml.example 为 application-local.yaml
#   填入控制台 http 服务接口认证信息中的 APIPassword
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

**AI 排课配置说明（同学 A 负责配置，同学 B 提供演示数据）**

1. 登录 [讯飞开放平台](https://console.xfyun.cn/) → 我的应用 → 进入已创建应用
2. 在 **http 服务接口认证信息** 中复制 **APIPassword**
3. 创建 `demo/src/main/resources/application-local.yaml`：

```yaml
spark:
  api-password: 你的APIPassword
```

4. 管理员登录 PC 端 → **学校管理 → AI 智能排课** → 点击「AI 智能分析」
5. 初始数据中 `2025春季` 学期含一条教师时间冲突（张老师周一 09:00 段重叠），便于演示

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

**请假演示：** 初始数据中 `parent1` 已为王小明提交一条待审批病假；家长登录小程序「请假申请」可查看/撤回，班主任 `teacher1` 登录 PC「本班请假」可审批。

---

## 七、注意事项

1. **不要提交** `node_modules/`、`demo/target/`、`.idea/`、`application-local.yaml` 等（已在 `.gitignore` 中配置）
2. PC 端家长账号会提示「请使用小程序端访问」
3. 小程序 TabBar 图标需本地生成，未纳入版本库
4. 未配置 `spark.api-password` 时，AI 排课仍可使用规则引擎检测冲突，但不会调用大模型生成建议

---

## 八、协作提交记录

| 提交 | 说明 | 负责人 |
|------|------|--------|
| `chore: add gitignore` | 添加忽略规则 | 共同 |
| `feat(part1): 平台基础、权限用户管理、档案中心、班主任范围与 AI 排课大模型前端` | 第一部分代码 | 同学 A |
| `feat(part2): 教学运营、家长服务、任课教师功能与 AI 排课规则检测` | 第二部分代码 | 同学 B |
| `feat(ai-schedule-spark): AI 排课星火 API 与可视化课表` | 大模型对接、前端页面 | 同学 A |
| `feat(ai-schedule-rules): AI 排课规则冲突检测与数据统计` | 规则引擎、容量/人数统计 | 同学 B |
| `feat: 教师分级菜单与数据范围` | 班主任 / 任课教师分权（A+B 协作） | 共同 |

---

**心田花开培训机构综合管理平台** — 综合实验项目
