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

系统共包含 **27 个功能模块**，按使用端与角色划分如下。

### 2.1 小程序端（家长）— 共 9 项

| 功能 | 说明 |
|------|------|
| 用户登录 | 家长账号登录，自动绑定家长信息 |
| 通知公告浏览 | 查看面向家长的公告列表 |
| 课程浏览 | 浏览已上架课程，查看课程详情 |
| 课程购买 | 创建课程订单、模拟支付 |
| 课程表浏览 | 查看关联学生的上课安排 |
| 考勤浏览 | 查看学生考勤记录 |
| 成绩浏览 | 查看学生考试成绩 |
| 留言 | 向机构提交留言、查看回复 |
| 个人信息维护 | 查看/修改家长资料、关联学生信息 |

小程序 TabBar：**首页 | 课程 | 订单 | 我的**

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
| **AI 智能排课** | 基于教师时间、教室容量、学生人数进行冲突检测，调用讯飞星火大模型生成优化建议，可视化周课表展示 |

> 仓库中还包含课表 CRUD、教学进度、课程订单、家访、异常考勤等管理页面源码；其中 **AI 智能排课** 已接入管理员菜单。

---

### 2.3 PC 端 — 教师 — 共 8 项

| 功能 | 说明 |
|------|------|
| 公告浏览 | 只读查看公告 |
| 学生浏览 | 只读查看学生信息 |
| 家长浏览 | 只读查看家长信息 |
| 课程浏览 | 只读查看课程信息 |
| 考勤管理 | 录入、维护学生考勤 |
| 考试管理 | 维护考试安排 |
| 成绩管理 | 录入、维护学生成绩 |
| 请假管理 | 处理学生请假申请 |

教师端部分浏览类页面与管理端共用组件，通过路由 `readOnly` 控制为只读模式。

---

### 2.4 后端核心能力

- 全模块标准 CRUD 接口（`/user`、`/teacher`、`/student` 等）
- 家长端聚合 API（`/app/*`）：按家长 ID 过滤学生、课表、考勤、成绩、订单等数据
- 登录鉴权：返回用户角色及 `parentId` / `teacherLevel` 等扩展字段
- 课程订单：自动生成订单号、模拟支付流程
- **AI 智能排课**（`/schedule/ai/*`）：规则引擎检测教师/教室/容量冲突，调用讯飞星火 HTTP API 生成排课建议

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

项目按功能模块平均分为两部分，两人工作量相当。**A 负责平台基础与档案中心（13 个模块），B 负责教学运营与家校服务（14 个模块）。** B 的开发依赖 A 提供的基础数据与登录能力。

---

### 4.1 同学 A — 第一部分：平台基础与档案中心（13 个模块）

**定位：** 搭建系统骨架，完成人员组织、权限、公告与留言基础能力。

#### 负责的功能模块

| 端 | 模块 |
|----|------|
| **小程序（3）** | 用户登录、通知公告浏览、个人信息维护 |
| **管理员（7）** | 权限管理、公告管理、学生管理、教师管理、家长管理、班级管理、留言管理 |
| **教师（3）** | 公告浏览、学生浏览、家长浏览 |

#### 主要代码范围

**后端 `demo/`**

- 公共基础：`DemoApplication.java`、`application.yaml`、`training.sql`、`fix_parent_bind.sql`
- 业务模块：User、Teacher、Student、Parent、Clazz、Announcement、Message

**PC 端 `vue-demo/`**

- 框架：`main.js`、`App.vue`、`router/index.js`（初版）、`utils/request.js`
- 页面：Login、Home、PermissionManage、TeacherManage、StudentManage、ParentManage、ClassManage、AnnouncementManage、MessageManage
- API：user、teacher、student、parent、clazz、announcement、message

**小程序 `miniprograme/`**

- 公共：`app.js`、`app.json`、`app.wxss`、`utils/*`、`styles/common.wxss`
- 页面：login、index（首页）、announcement/list、profile

#### Git 提交

```
feat(part1): 上传平台基础与档案管理模块
```

---

### 4.2 同学 B — 第二部分：教学运营与家校服务（14 个模块）

**定位：** 完成课程、教务、购课及家长端业务浏览，实现三端业务闭环。

#### 负责的功能模块

| 端 | 模块 |
|----|------|
| **小程序（6）** | 课程购买、课程浏览、课程表浏览、考勤浏览、成绩浏览、留言 |
| **管理员（3）** | 课程管理、考试管理、考勤管理 |
| **教师（5）** | 课程浏览、考勤管理、考试管理、成绩管理、请假管理 |

#### 主要代码范围

**后端 `demo/`**

- 家长端聚合 API：`ParentAppController`、`ParentAppService`、`ParentAppServiceImpl`
- 业务模块：Course、Exam、Score、Attendance、LeaveRequest、ClassSchedule、CourseOrder
- 扩展模块：AbnormalAttendance、HomeVisit、TeachingProgress

**PC 端 `vue-demo/`**

- 页面：CourseManage、ExamManage、AttendanceManage、ScoreManage、LeaveManage、ScheduleManage、ProgressManage、CourseOrderManage、HomeVisitManage、AbnormalAttendanceManage
- API：course、exam、attendance、score、leave、schedule、progress、courseOrder、homeVisit、abnormalAttendance
- 共享更新：`router/index.js`（完整路由）、`Home.vue`（完整菜单）

**小程序 `miniprograme/`**

- 页面：course、order、schedule、attendance、exam、score、message
- 共享更新：`app.json`、首页 `pages/index/*`

#### Git 提交

```
feat(part2): 上传教学运营与家长服务模块
```

---

### 4.3 分工对照总表

| 对比项 | 同学 A（第一部分） | 同学 B（第二部分） |
|--------|-------------------|-------------------|
| 模块数量 | 13 个 | 14 个 |
| 业务重心 | 登录、权限、人员档案、公告 | 课程、教务、购课、家长端浏览 |
| 后端难点 | 登录与家长账号绑定 | ParentApp 聚合 API、订单支付 |
| 小程序 | 登录、首页、公告、个人中心 | 课程、订单、课表、考勤、成绩、留言 |
| 依赖关系 | 先完成，供 B 使用 | 依赖 A 的基础数据与登录 |

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

**AI 排课配置说明**

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
| teacher1 | 123456 | 班主任 | PC |
| teacher2 | 123456 | 任课教师 | PC |
| parent1 | 123456 | 家长 | 小程序 |

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
| `feat(part1): 上传平台基础与档案管理模块` | 第一部分代码 | 同学 A |
| `feat(part2): 上传教学运营与家长服务模块` | 第二部分代码 | 同学 B |

---

**心田花开培训机构综合管理平台** — 综合实验项目
