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
  onShow: function () {
    if (!requireLogin()) return
    this.loadData()
  },
  loadData: function () {
    const self = this
    const parentId = getParentId()
    Promise.all([
      request('/app/parent/' + parentId + '/students'),
      request('/app/parent/' + parentId + '/home-visits')
    ]).then(function (results) {
      const students = results[0]
      const visits = results[1]
      const groups = groupByStudentRecords(
        students,
        visits,
        function (v) {
          return {
            rowKey: String(v.id || v.visitDate || '') + '_' + String(v.teacherId || ''),
            visitDate: v.visitDate || '-',
            visitTypeText: fmt.visitType(v.visitType),
            teacherName: v.teacherName || '-',
            content: v.content || '-',
            feedback: v.feedback || '-',
            nextPlan: v.nextPlan || '',
            showNextPlan: !!(v.nextPlan && v.nextPlan !== '-'),
            createTime: v.createTime || '-'
          }
        },
        function (a, b) {
          return String(b.visitDate).localeCompare(String(a.visitDate))
        }
      )
      const count = (students || []).length
      self.setData({
        groups: groups,
        studentCount: count,
        hasData: hasAnyRows(groups),
        tip: count > 1
          ? '以下按孩子分别展示班主任家访记录，方便了解老师与家庭的沟通情况。'
          : '展示班主任对本孩子的家访记录。'
      })
    }).catch(function (err) {
      wx.showToast({ title: err.message, icon: 'none' })
    })
  }
})
