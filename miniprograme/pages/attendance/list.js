const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')
const { groupByStudentRecords, hasAnyRows } = require('../../utils/studentGroup')

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
      request('/app/parent/' + parentId + '/attendance')
    ]).then(([students, attendance]) => {
      const groups = groupByStudentRecords(
        students,
        attendance,
        a => ({
          attendDate: a.attendDate || '-',
          courseName: a.courseName || '-',
          className: a.className || '-',
          statusText: fmt.attend(a.status),
          statusClass: fmt.attendClass(a.status)
        }),
        (a, b) => (b.attendDate || '').localeCompare(a.attendDate || '')
      )
      const count = (students || []).length
      this.setData({
        groups,
        studentCount: count,
        hasData: hasAnyRows(groups),
        tip: count > 1
          ? '以下按孩子分别展示考勤记录，依据各学生个人考勤数据汇总。'
          : '展示该孩子的考勤记录。'
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
