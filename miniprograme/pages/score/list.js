const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
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
      request('/app/parent/' + parentId + '/scores')
    ]).then(([students, scores]) => {
      const groups = groupByStudentRecords(
        students,
        scores,
        s => ({
          examName: s.examName || '-',
          score: s.score != null ? s.score : '-',
          rankNum: s.rankNum != null ? '第 ' + s.rankNum + ' 名' : '-'
        }),
        (a, b) => (a.examName || '').localeCompare(b.examName || '')
      )
      const count = (students || []).length
      this.setData({
        groups,
        studentCount: count,
        hasData: hasAnyRows(groups),
        tip: count > 1
          ? '以下按孩子分别展示考试成绩，依据各学生个人成绩记录汇总。'
          : '展示该孩子的考试成绩。'
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
