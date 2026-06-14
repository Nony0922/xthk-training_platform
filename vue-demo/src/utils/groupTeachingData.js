export function groupByClass(items, { classIdKey = 'classId', classNameKey = 'className', nameSortKey = 'name' } = {}) {
  const map = new Map()
  for (const item of items || []) {
    const classId = item[classIdKey] ?? 'unknown'
    const className = item[classNameKey] || '未分班'
    if (!map.has(classId)) {
      map.set(classId, { classId, className, rows: [] })
    }
    map.get(classId).rows.push(item)
  }
  return Array.from(map.values())
    .sort((a, b) => String(a.className).localeCompare(String(b.className), 'zh-CN'))
    .map((group) => ({
      ...group,
      rows: [...group.rows].sort((a, b) =>
        String(a[nameSortKey] || '').localeCompare(String(b[nameSortKey] || ''), 'zh-CN')
      )
    }))
}

export function groupByCourse(items, { courseIdKey = 'courseId', courseNameKey = 'courseName' } = {}) {
  const map = new Map()
  for (const item of items || []) {
    const courseId = item[courseIdKey] ?? 'unknown'
    const courseName = item[courseNameKey] || '未关联课程'
    if (!map.has(courseId)) {
      map.set(courseId, { courseId, courseName, rows: [] })
    }
    map.get(courseId).rows.push(item)
  }
  return Array.from(map.values()).sort((a, b) =>
    String(a.courseName).localeCompare(String(b.courseName), 'zh-CN')
  )
}

export function groupExamsByCourseClass(items) {
  const courseMap = new Map()
  for (const item of items || []) {
    const courseId = item.courseId ?? 'unknown'
    const courseName = item.courseName || '未关联课程'
    if (!courseMap.has(courseId)) {
      courseMap.set(courseId, { courseId, courseName, classes: new Map() })
    }
    const course = courseMap.get(courseId)
    const classId = item.classId ?? 'unknown'
    const className = item.className || '未分班'
    if (!course.classes.has(classId)) {
      course.classes.set(classId, { classId, className, rows: [] })
    }
    course.classes.get(classId).rows.push(item)
  }
  return Array.from(courseMap.values())
    .sort((a, b) => String(a.courseName).localeCompare(String(b.courseName), 'zh-CN'))
    .map((course) => ({
      courseId: course.courseId,
      courseName: course.courseName,
      classes: Array.from(course.classes.values())
        .sort((a, b) => String(a.className).localeCompare(String(b.className), 'zh-CN'))
        .map((klass) => ({
          ...klass,
          rows: [...klass.rows].sort((a, b) =>
            String(b.examDate || '').localeCompare(String(a.examDate || ''))
          )
        }))
    }))
}

export function groupAttendanceByCourseClass(items) {
  const courseMap = new Map()
  for (const item of items || []) {
    const courseId = item.courseId ?? 'unknown'
    const courseName = item.courseName || '未关联课程'
    if (!courseMap.has(courseId)) {
      courseMap.set(courseId, { courseId, courseName, classes: new Map() })
    }
    const course = courseMap.get(courseId)
    const classId = item.classId ?? 'unknown'
    const className = item.className || '未分班'
    if (!course.classes.has(classId)) {
      course.classes.set(classId, { classId, className, rows: [] })
    }
    course.classes.get(classId).rows.push(item)
  }
  return Array.from(courseMap.values())
    .sort((a, b) => String(a.courseName).localeCompare(String(b.courseName), 'zh-CN'))
    .map((course) => ({
      courseId: course.courseId,
      courseName: course.courseName,
      classes: Array.from(course.classes.values()).sort((a, b) =>
        String(a.className).localeCompare(String(b.className), 'zh-CN')
      )
    }))
}

export function groupScoresByExam(items, exams = []) {
  const examMap = new Map((exams || []).map((exam) => [exam.id, exam]))
  const map = new Map()
  for (const item of items || []) {
    const examId = item.examId ?? 'unknown'
    if (!map.has(examId)) {
      const exam = examMap.get(examId)
      map.set(examId, {
        examId,
        examName: item.examName || exam?.name || '未知考试',
        courseId: exam?.courseId ?? item.courseId ?? 'unknown',
        courseName: exam?.courseName || item.courseName || '未关联课程',
        className: exam?.className || item.className || '-',
        examDate: exam?.examDate || '-',
        rows: []
      })
    }
    map.get(examId).rows.push(item)
  }
  return Array.from(map.values())
    .map((group) => ({
      ...group,
      rows: [...group.rows].sort((a, b) => (a.rankNum || 999) - (b.rankNum || 999))
    }))
    .sort((a, b) => String(b.examDate).localeCompare(String(a.examDate)))
}

export function groupScoresByCourseExam(items, exams = []) {
  const examGroups = groupScoresByExam(items, exams)
  const courseMap = new Map()
  for (const group of examGroups) {
    const courseId = group.courseId ?? 'unknown'
    const courseName = group.courseName || '未关联课程'
    if (!courseMap.has(courseId)) {
      courseMap.set(courseId, { courseId, courseName, exams: [] })
    }
    courseMap.get(courseId).exams.push(group)
  }
  return Array.from(courseMap.values())
    .sort((a, b) => String(a.courseName).localeCompare(String(b.courseName), 'zh-CN'))
    .map((course) => ({
      ...course,
      exams: course.exams.sort((a, b) => String(b.examDate).localeCompare(String(a.examDate)))
    }))
}
