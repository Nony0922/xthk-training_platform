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
      request('/app/parent/' + parentId + '/exams')
    ]).then(([students, exams]) => {
      const groups = groupByStudentClass(
        students,
        exams,
        e => {
          const status = fmt.examResolve(e)
          return {
          examDate: e.examDate || '-',
          name: e.name || '-',
          courseName: e.courseName || '-',
          timeText: trimTime(e.startTime) + ' - ' + trimTime(e.endTime),
          location: e.location || '-',
          statusText: fmt.exam(status),
          statusClass: fmt.examClass(status)
        }},
        (a, b) => (a.examDate || '').localeCompare(b.examDate || '')
      )
      const count = (students || []).length
      this.setData({
        groups,
        studentCount: count,
        hasData: hasAnyRows(groups),
        tip: count > 1
          ? '以下按孩子分别展示考试安排，依据各孩子所在班级匹配考试计划。'
          : '依据孩子所在班级展示考试安排。'
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
