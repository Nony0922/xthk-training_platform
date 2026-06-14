<template>
  <div class="home">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">🌸</div>
        <h2>心田花开</h2>
        <p class="logo-sub">PC端 · {{ roleText }}</p>
      </div>

      <nav class="nav-menu">
        <!-- 管理员菜单 -->
        <template v-if="user?.role === 'admin'">
          <div class="nav-section">
            <div class="section-label">权限管理</div>
            <router-link
              to="/home/permission"
              class="nav-item"
              active-class="active"
            >
              <span class="nav-icon">🔐</span>
              权限管理
            </router-link>
          </div>

          <div class="nav-section">
            <div class="section-label">学校管理</div>
            <router-link
              v-for="item in adminSchoolMenus"
              :key="item.path"
              :to="item.path"
              class="nav-item nav-sub"
              active-class="active"
            >
              <span class="nav-icon">{{ item.icon }}</span>
              {{ item.title }}
            </router-link>
          </div>
        </template>

        <!-- 教师菜单 -->
        <template v-if="user?.role === 'teacher'">
          <div class="nav-section">
            <div class="section-label">任课教师功能</div>
            <router-link
              v-for="item in teacherCommonMenus"
              :key="item.path"
              :to="item.path"
              class="nav-item"
              active-class="active"
            >
              <span class="nav-icon">{{ item.icon }}</span>
              {{ item.title }}
            </router-link>
          </div>

          <div v-if="isHeadTeacher" class="nav-section">
            <div class="section-label">班主任专有</div>
            <router-link
              v-for="item in headTeacherOnlyMenus"
              :key="item.path"
              :to="item.path"
              class="nav-item nav-sub"
              active-class="active"
            >
              <span class="nav-icon">{{ item.icon }}</span>
              {{ item.title }}
            </router-link>
          </div>
        </template>
      </nav>

      <div class="user-info">
        <div class="user-avatar">{{ roleLabel }}</div>
        <div class="user-detail">
          <span class="user-name">{{ user?.name || '用户' }}</span>
          <span class="user-role">{{ roleText }}</span>
        </div>
        <button @click="handleLogout">退出</button>
      </div>
    </aside>

    <main class="main-content">
      <header class="header">
        <div class="header-breadcrumb">
          <span v-if="breadcrumb" class="breadcrumb">{{ breadcrumb }}</span>
          <h1>{{ currentTitle }}</h1>
        </div>
      </header>
      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const adminSchoolMenus = [
  { path: '/home/messages', title: '留言管理', icon: '💬' },
  { path: '/home/announcements', title: '公告管理', icon: '📢' },
  { path: '/home/students', title: '学生管理', icon: '👨‍🎓' },
  { path: '/home/teachers', title: '教师管理', icon: '👨‍🏫' },
  { path: '/home/parents', title: '家长管理', icon: '👪' },
  { path: '/home/classes', title: '班级管理', icon: '🏫' },
  { path: '/home/courses', title: '课程管理', icon: '📚' },
  { path: '/home/exams', title: '考试管理', icon: '📝' },
  { path: '/home/attendance', title: '考勤管理', icon: '✅' },
  { path: '/home/schedule-ai', title: 'AI 智能排课', icon: '🤖' },
  { path: '/home/learning-report', title: 'AI 学情分析', icon: '📊' }
]

const teacherCommonMenus = [
  { path: '/home/browse/announcements', title: '公告浏览', icon: '📢', breadcrumb: '教师' },
  { path: '/home/browse/courses', title: '我的课程', icon: '📚', breadcrumb: '教师' },
  { path: '/home/teacher/schedule', title: '我的课表', icon: '📅', breadcrumb: '教师' },
  { path: '/home/teacher/exams', title: '考试管理', icon: '📝', breadcrumb: '教师' },
  { path: '/home/teacher/subject/attendance', title: '授课考勤', icon: '✅', breadcrumb: '教师' },
  { path: '/home/teacher/subject/scores', title: '授课成绩', icon: '🏆', breadcrumb: '教师' },
  { path: '/home/teacher/learning-report', title: 'AI 学情分析', icon: '📊', breadcrumb: '教师' }
]

const headTeacherOnlyMenus = [
  { path: '/home/browse/students', title: '本班学生', icon: '👨‍🎓', breadcrumb: '班主任' },
  { path: '/home/browse/parents', title: '本班家长', icon: '👪', breadcrumb: '班主任' },
  { path: '/home/teacher/leave', title: '本班请假', icon: '📋', breadcrumb: '班主任' },
  { path: '/home/teacher/home-visit', title: '家访管理', icon: '🏠', breadcrumb: '班主任' },
  { path: '/home/teacher/attendance', title: '本班考勤', icon: '✅', breadcrumb: '班主任' },
  { path: '/home/teacher/scores', title: '本班成绩', icon: '🏆', breadcrumb: '班主任' }
]

const isHeadTeacher = computed(() => user.value?.teacherLevel === 2)

const allRouteTitles = [
  { path: '/home/permission', title: '权限管理', breadcrumb: '权限管理' },
  ...adminSchoolMenus.map(m => ({ ...m, breadcrumb: '学校管理' })),
  ...teacherCommonMenus,
  ...headTeacherOnlyMenus
]

const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('loginUser'))
  } catch {
    return null
  }
})

const roleText = computed(() => {
  const u = user.value
  if (!u) return ''
  if (u.role === 'admin') return '管理员'
  if (u.role === 'teacher') return u.teacherLevel === 2 ? '班主任' : '任课教师'
  return u.role
})

const roleLabel = computed(() => {
  const u = user.value
  if (!u) return '👤'
  if (u.role === 'admin') return '管'
  if (u.role === 'teacher') return '师'
  return '👤'
})

const currentTitle = computed(() => {
  const item = allRouteTitles.find(m => m.path === route.path)
  return item?.title || route.meta?.title || '心田花开培训机构综合管理平台'
})

const breadcrumb = computed(() => {
  const item = allRouteTitles.find(m => m.path === route.path)
  return item?.breadcrumb || ''
})

const handleLogout = () => {
  localStorage.removeItem('loginUser')
  router.push('/')
}
</script>

<style scoped>
.home {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #7c3aed 0%, #4c1d95 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.logo {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.logo-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.logo-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.nav-menu {
  flex: 1;
  padding: 8px 0 16px;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 8px;
}

.section-label {
  padding: 10px 24px 6px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.45);
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 24px;
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  transition: all 0.2s;
  border-left: 3px solid transparent;
  font-size: 14px;
}

.nav-sub {
  padding-left: 28px;
  font-size: 13px;
}

.nav-icon {
  font-size: 15px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.nav-item:hover,
.nav-item.active {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border-left-color: #fbbf24;
}

.user-info {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-detail {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.user-info button {
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  background: #ef4444;
  color: #fff;
  cursor: pointer;
  font-size: 12px;
  flex-shrink: 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f3f4f6;
  min-width: 0;
}

.header {
  background: #fff;
  padding: 16px 32px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.breadcrumb {
  display: block;
  font-size: 12px;
  color: #7c3aed;
  margin-bottom: 4px;
  font-weight: 500;
}

.header h1 {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
  font-weight: 600;
}

.content {
  flex: 1;
  padding: 24px 32px;
  overflow: auto;
}
</style>
