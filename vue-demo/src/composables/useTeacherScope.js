/**
 * @param {'teaching' | 'homeroom' | undefined} mode
 *   teaching  - 按任课教师范围（本人授课课程/班级）
 *   homeroom  - 按班主任范围（所管班级）
 *   undefined - 使用登录账号的 teacherLevel
 */
export function getTeacherScopeParams(mode) {
  try {
    const user = JSON.parse(localStorage.getItem('loginUser'))
    if (user?.role === 'teacher') {
      let teacherLevel = user.teacherLevel || 1
      if (mode === 'teaching') teacherLevel = 1
      else if (mode === 'homeroom') teacherLevel = 2
      return {
        scopeUserId: user.id,
        teacherLevel
      }
    }
  } catch {
    // ignore
  }
  return {}
}

export function getScopeModeFromRoute(route) {
  if (route?.meta?.scopeMode) return route.meta.scopeMode
  return undefined
}

export function isHeadTeacher(user) {
  return user?.role === 'teacher' && user?.teacherLevel === 2
}

export function isSubjectTeacher(user) {
  return user?.role === 'teacher' && (user?.teacherLevel || 1) === 1
}
