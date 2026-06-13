const weekdays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
const attendStatus = ['', '正常', '迟到', '早退', '缺勤', '请假']
const orderStatus = ['待支付', '已支付', '已取消']
const examStatus = ['未开始', '进行中', '已结束']

function teachMode(n) {
  return ['', '线下授课', '线上直播', '线上线下混合'][n] || '-'
}

function teachModeShort(n) {
  return ['', '线下', '线上', '混合'][n] || '-'
}

function teachModeClass(n) {
  return ['', 'tag-purple', 'tag-green', 'tag-orange'][n] || 'tag-gray'
}

function parseHighlights(str) {
  return str ? str.split('|').filter(Boolean) : []
}

function validPeriod(c) {
  if (!c) return '-'
  const s = c.validStart || ''
  const e = c.validEnd || ''
  if (s && e) return s + ' 至 ' + e
  return s || e || '-'
}

function remainSeats(c) {
  if (!c || !c.maxStudents || c.maxStudents <= 0) return '名额充足'
  const remain = c.maxStudents - (c.enrolledCount || 0)
  if (remain <= 0) return '已满员'
  return '剩余 ' + remain + ' 名'
}

function remainClass(c) {
  if (!c || !c.maxStudents || c.maxStudents <= 0) return 'tag-green'
  const remain = c.maxStudents - (c.enrolledCount || 0)
  if (remain <= 0) return 'tag-red'
  if (remain <= 5) return 'tag-orange'
  return 'tag-green'
}

function isGradeMatch(targetGrade, className) {
  if (!targetGrade || targetGrade === '全年级' || targetGrade === '不限') return true
  return className && className.indexOf(targetGrade) !== -1
}

function mapCourse(c) {
  if (!c) return c
  return {
    ...c,
    teachModeText: teachModeShort(c.teachMode),
    teachModeFull: teachMode(c.teachMode),
    teachModeClass: teachModeClass(c.teachMode),
    validPeriodText: validPeriod(c),
    remainSeatsText: remainSeats(c),
    remainSeatsClass: remainClass(c),
    highlightList: parseHighlights(c.highlights),
    full: remainSeats(c) === '已满员'
  }
}

module.exports = {
  weekday: (n) => weekdays[n] || '-',
  attend: (n) => attendStatus[n] || '-',
  order: (n) => orderStatus[n] || '-',
  exam: (n) => examStatus[n] || '-',
  msg: (n) => (n === 1 ? '已回复' : '待回复'),
  leaveType: (n) => ['', '事假', '病假', '其他'][n] || '-',
  leaveStatus: (n) => ['待审批', '已通过', '已驳回', '已撤回'][n] || '-',
  attendClass: (n) => ['', 'tag-green', 'tag-orange', 'tag-orange', 'tag-red', 'tag-purple'][n] || 'tag-gray',
  examClass: (n) => ['tag-orange', 'tag-green', 'tag-gray'][n] || 'tag-gray',
  /** 根据考试日期时间计算状态（与后端 ExamStatusUtil 规则一致） */
  examResolve: (exam) => {
    if (!exam || !exam.examDate) return exam?.status ?? 0
    const now = new Date()
    const dateStr = exam.examDate.substring(0, 10)
    const startStr = exam.startTime ? exam.startTime.substring(0, 5) : '00:00'
    const endStr = exam.endTime ? exam.endTime.substring(0, 5) : '23:59'
    const start = new Date(dateStr + 'T' + startStr + ':00')
    const end = new Date(dateStr + 'T' + endStr + ':00')
    if (now < start) return 0
    if (now > end) return 2
    return 1
  },
  teachMode,
  teachModeShort,
  teachModeClass,
  parseHighlights,
  validPeriod,
  remainSeats,
  remainClass,
  isGradeMatch,
  mapCourse
}
