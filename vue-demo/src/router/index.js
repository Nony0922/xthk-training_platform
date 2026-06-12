import { createRouter, createWebHistory } from 'vue-router'

const Login = () => import('@/views/Login.vue')
const Home = () => import('@/views/Home.vue')
const PermissionManage = () => import('@/views/PermissionManage.vue')
const MessageManage = () => import('@/views/MessageManage.vue')
const AnnouncementManage = () => import('@/views/AnnouncementManage.vue')
const StudentManage = () => import('@/views/StudentManage.vue')
const TeacherManage = () => import('@/views/TeacherManage.vue')
const ParentManage = () => import('@/views/ParentManage.vue')
const ClassManage = () => import('@/views/ClassManage.vue')
const CourseManage = () => import('@/views/CourseManage.vue')
const ExamManage = () => import('@/views/ExamManage.vue')
const AttendanceManage = () => import('@/views/AttendanceManage.vue')
const ScoreManage = () => import('@/views/ScoreManage.vue')
const LeaveManage = () => import('@/views/LeaveManage.vue')
const ScheduleAiAssistant = () => import('@/views/ScheduleAiAssistant.vue')

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresAuth: false }
  },
  { path: '/login', redirect: '/' },
  {
    path: '/home',
    component: Home,
    meta: { requiresAuth: true },
    children: [
      // 管理员 - 权限管理
      {
        path: 'permission',
        name: 'PermissionManage',
        component: PermissionManage,
        meta: { title: '权限管理', roles: ['admin'], group: 'permission' }
      },
      // 管理员 - 学校管理
      {
        path: 'messages',
        name: 'MessageManage',
        component: MessageManage,
        meta: { title: '留言管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'announcements',
        name: 'AnnouncementManage',
        component: AnnouncementManage,
        meta: { title: '公告管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'students',
        name: 'StudentManage',
        component: StudentManage,
        meta: { title: '学生管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'teachers',
        name: 'TeacherManage',
        component: TeacherManage,
        meta: { title: '教师管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'parents',
        name: 'ParentManage',
        component: ParentManage,
        meta: { title: '家长管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'classes',
        name: 'ClassManage',
        component: ClassManage,
        meta: { title: '班级管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'courses',
        name: 'CourseManage',
        component: CourseManage,
        meta: { title: '课程管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'exams',
        name: 'ExamManage',
        component: ExamManage,
        meta: { title: '考试管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'attendance',
        name: 'AttendanceManage',
        component: AttendanceManage,
        meta: { title: '考勤管理', roles: ['admin'], group: 'school' }
      },
      {
        path: 'schedule-ai',
        name: 'ScheduleAiAssistant',
        component: ScheduleAiAssistant,
        meta: { title: 'AI 智能排课', roles: ['admin'], group: 'school' }
      },
      // 教师 - 浏览类
      {
        path: 'browse/announcements',
        name: 'AnnouncementBrowse',
        component: AnnouncementManage,
        meta: { title: '公告浏览', roles: ['teacher'], readOnly: true }
      },
      {
        path: 'browse/students',
        name: 'StudentBrowse',
        component: StudentManage,
        meta: { title: '学生浏览', roles: ['teacher'], readOnly: true }
      },
      {
        path: 'browse/parents',
        name: 'ParentBrowse',
        component: ParentManage,
        meta: { title: '家长浏览', roles: ['teacher'], readOnly: true }
      },
      {
        path: 'browse/courses',
        name: 'CourseBrowse',
        component: CourseManage,
        meta: { title: '课程浏览', roles: ['teacher'], readOnly: true }
      },
      // 教师 - 管理类
      {
        path: 'teacher/attendance',
        name: 'TeacherAttendance',
        component: AttendanceManage,
        meta: { title: '考勤管理', roles: ['teacher'] }
      },
      {
        path: 'teacher/exams',
        name: 'TeacherExam',
        component: ExamManage,
        meta: { title: '考试管理', roles: ['teacher'] }
      },
      {
        path: 'teacher/scores',
        name: 'TeacherScore',
        component: ScoreManage,
        meta: { title: '成绩管理', roles: ['teacher'] }
      },
      {
        path: 'teacher/leave',
        name: 'TeacherLeave',
        component: LeaveManage,
        meta: { title: '请假管理', roles: ['teacher'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

const getUser = () => {
  try {
    return JSON.parse(localStorage.getItem('loginUser'))
  } catch {
    return null
  }
}

const clearLogin = () => localStorage.removeItem('loginUser')

const isValidPcUser = (user) => user && ['admin', 'teacher'].includes(user.role)

export const getDefaultRoute = (user) => {
  if (user?.role === 'admin') return '/home/permission'
  if (user?.role === 'teacher') return '/home/browse/announcements'
  return null
}

router.beforeEach((to) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - 心田花开培训机构综合管理平台'
  }

  let user = getUser()

  if (user && !['admin', 'teacher', 'parent'].includes(user.role)) {
    clearLogin()
    user = null
  }

  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if ((to.path === '/' || to.path === '/login') && isValidPcUser(user)) {
    return getDefaultRoute(user)
  }

  if (to.path === '/home' || to.path === '/home/') {
    return isValidPcUser(user) ? getDefaultRoute(user) : '/'
  }

  if (requiresAuth) {
    if (!user) return '/'

    if (user.role === 'parent') {
      clearLogin()
      alert('家长请使用小程序端访问')
      return '/'
    }

    if (!isValidPcUser(user)) {
      clearLogin()
      return '/'
    }

    if (to.meta.roles && !to.meta.roles.includes(user.role)) {
      const fallback = getDefaultRoute(user)
      return fallback && to.path !== fallback ? fallback : '/'
    }
  }

  return true
})

export default router
