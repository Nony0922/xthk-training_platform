const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')
const { trimTime, groupByStudentClass, hasAnyRows } = require('../../utils/studentGroup')

Page({
  data: {
    groups: [],
    studentCount: 0,
    hasData: false,
    tip: ''
  },
  onShow() {
    if (!requireLogin()) return
    this.loadData()
  },
  loadData() {
    const parentId = getParentId()
    Promise.all([
      request('/app/parent/' + parentId + '/students'),
      request('/app/parent/' + parentId + '/schedules')
    ]).then(([students, schedules]) => {
      const groups = groupByStudentClass(
        students,
        schedules,
        s => ({
          weekday: s.weekday || 0,
          weekdayText: fmt.weekday(s.weekday),
          timeText: trimTime(s.startTime) + ' - ' + trimTime(s.endTime),
          courseName: s.courseName || '-',
          teacherName: s.teacherName || '-',
          room: s.room || '-',
          semester: s.semester || '-'
        }),
        (a, b) => (a.weekday - b.weekday) || a.timeText.localeCompare(b.timeText)
      )
      const count = (students || []).length
      this.setData({
        groups,
        studentCount: count,
        hasData: hasAnyRows(groups),
        tip: count > 1
          ? '以下按孩子分别展示课程表，依据各孩子所在班级匹配上课安排。'
          : '依据孩子所在班级展示课程表。'
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
