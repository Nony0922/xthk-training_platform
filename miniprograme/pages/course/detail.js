const { request, postAction, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: {
    course: null,
    courseId: null,
    fitStudents: [],
    unfitStudents: [],
    canBuy: true,
    buyTip: ''
  },
  onLoad(options) {
    this.setData({ courseId: options.id })
    if (!requireLogin()) return
    this.loadDetail(options.id)
  },
  loadDetail(id) {
    Promise.all([
      request('/app/courses/' + id),
      request('/app/parent/' + getParentId() + '/students')
    ]).then(([course, students]) => {
      const mapped = fmt.mapCourse(course)
      const fitStudents = []
      const unfitStudents = []
      ;(students || []).forEach(s => {
        if (fmt.isGradeMatch(mapped.targetGrade, s.className)) {
          fitStudents.push(s)
        } else {
          unfitStudents.push(s)
        }
      })
      let canBuy = !mapped.full
      let buyTip = ''
      if (mapped.full) {
        buyTip = '当前课程名额已满，暂无法购买'
        canBuy = false
      } else if (fitStudents.length === 0 && (students || []).length > 0) {
        buyTip = '提示：该课程适用「' + (mapped.targetGrade || '指定') + '」，与您孩子的年级可能不完全匹配，请确认后再购买'
      } else if (fitStudents.length > 0) {
        buyTip = '适合您的孩子：' + fitStudents.map(s => s.name + '（' + (s.className || '未分班') + '）').join('、')
      }
      this.setData({ course: mapped, fitStudents, unfitStudents, canBuy, buyTip })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  createOrder(payNow) {
    const { course, canBuy } = this.data
    if (!canBuy) {
      wx.showToast({ title: '当前无法购买', icon: 'none' })
      return
    }
    const parentId = getParentId()
    if (!parentId) return wx.showToast({ title: '未绑定家长', icon: 'none' })

    const summary = [
      '课程：' + course.name,
      '适用年级：' + (course.targetGrade || '-'),
      '授课方式：' + course.teachModeFull,
      '有效日期：' + course.validPeriodText,
      '费用：¥' + course.fee
    ].join('\n')

    wx.showModal({
      title: payNow ? '确认购买并支付' : '确认加入订单',
      content: summary,
      success: (res) => {
        if (!res.confirm) return
        wx.showLoading({ title: '处理中' })
        postAction('/app/parent/order', {
          parentId,
          courseId: course.id,
          courseName: course.name,
          teacherName: course.teacherName,
          hours: course.hours,
          fee: course.fee,
          status: 0
        }).then(result => {
          if (payNow && result.orderId) {
            return putAction('/app/parent/order/' + result.orderId + '/pay', {})
          }
          return result
        }).then(() => {
          wx.showToast({ title: payNow ? '支付成功' : '已加入订单', icon: 'success' })
          setTimeout(() => wx.switchTab({ url: '/pages/order/list' }), 800)
        }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
          .finally(() => wx.hideLoading())
      }
    })
  },
  addOrder() { this.createOrder(false) },
  buyNow() { this.createOrder(true) }
})
