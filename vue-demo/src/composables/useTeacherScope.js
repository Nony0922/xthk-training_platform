export function getTeacherScopeParams() {
  try {
    const user = JSON.parse(localStorage.getItem('loginUser'))
    if (user?.role === 'teacher') {
      return {
        scopeUserId: user.id,
        teacherLevel: user.teacherLevel || 1
      }
    }
  } catch {
    // ignore
  }
  return {}
}

export function isHeadTeacher(user) {
  return user?.role === 'teacher' && user?.teacherLevel === 2
}

export function isSubjectTeacher(user) {
  return user?.role === 'teacher' && (user?.teacherLevel || 1) === 1
}
