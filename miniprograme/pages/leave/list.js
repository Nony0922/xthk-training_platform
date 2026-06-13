const { request, postAction, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

const STATUS_CLASS = ['tag-orange', 'tag-green', 'tag-red', 'tag-gray']

Page({
  data: {
    list: [],
    students: [],
    studentNames: [],
    studentIndex: 0,
    leaveTypes: ['事假', '病假', '其他'],
    leaveTypeIndex: 0,
    startDate: '',
    endDate: '',
    reason: '',
    showForm: false
  },
  onShow() {
    if (!requireLogin()) return
    this.loadStudents()
    this.loadList()
  },
  loadStudents() {
    request('/app/parent/' + getParentId() + '/students').then(students => {
      const list = students || []
      this.setData({
        students: list,
        studentNames: list.map(s => s.name + (s.className ? '（' + s.className + '）' : '')),
        studentIndex: 0
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  loadList() {
    request('/app/parent/' + getParentId() + '/leaves').then(list => {
      const mapped = (list || []).map(i => ({
        ...i,
        leaveTypeText: fmt.leaveType(i.leaveType),
        statusText: fmt.leaveStatus(i.status),
        statusClass: STATUS_CLASS[i.status] || 'tag-gray'
      }))
      this.setData({ list: mapped })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  toggleForm() {
    this.setData({ showForm: !this.data.showForm })
  },
  onStudentChange(e) {
    this.setData({ studentIndex: Number(e.detail.value) })
  },
  onLeaveTypeChange(e) {
    this.setData({ leaveTypeIndex: Number(e.detail.value) })
  },
  onStartDateChange(e) {
    this.setData({ startDate: e.detail.value })
  },
  onEndDateChange(e) {
    this.setData({ endDate: e.detail.value })
  },
  onReasonInput(e) {
    this.setData({ reason: e.detail.value })
  },
  submit() {
    const { students, studentIndex, leaveTypeIndex, startDate, endDate, reason } = this.data
    if (!students.length) {
      wx.showToast({ title: '暂无关联学生', icon: 'none' })
      return
    }
    if (!startDate || !endDate) {
      wx.showToast({ title: '请选择请假日期', icon: 'none' })
      return
    }
    if (startDate > endDate) {
      wx.showToast({ title: '开始日期不能晚于结束日期', icon: 'none' })
      return
    }
    if (!reason.trim()) {
      wx.showToast({ title: '请填写请假原因', icon: 'none' })
      return
    }
    postAction('/app/parent/' + getParentId() + '/leave', {
      studentId: students[studentIndex].id,
      leaveType: leaveTypeIndex + 1,
      startDate,
      endDate,
      reason: reason.trim()
    }).then(() => {
      wx.showToast({ title: '提交成功', icon: 'success' })
      this.setData({ reason: '', startDate: '', endDate: '', showForm: false })
      this.loadList()
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  withdraw(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认撤回',
      content: '确定撤回该请假申请吗？撤回后班主任将无法审批。',
      success: (res) => {
        if (!res.confirm) return
        putAction('/app/parent/' + getParentId() + '/leave/' + id + '/withdraw')
          .then(() => {
            wx.showToast({ title: '已撤回', icon: 'success' })
            this.loadList()
          })
          .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
      }
    })
  }
})
