const { request, postAction, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')

Page({
  data: { course: null, courseId: null },
  onLoad(options) {
    this.setData({ courseId: options.id })
    if (!requireLogin()) return
    request('/app/courses/' + options.id).then(course => this.setData({ course }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  createOrder(payNow) {
    const { course } = this.data
    const parentId = getParentId()
    if (!parentId) return wx.showToast({ title: '未绑定家长', icon: 'none' })
    wx.showLoading({ title: '处理中' })
    postAction('/app/parent/order', {
      parentId,
      courseId: course.id,
      courseName: course.name,
      teacherName: course.teacherName,
      hours: course.hours,
      fee: course.fee,
      status: 0
    }).then(res => {
      if (payNow && res.orderId) {
        return putAction('/app/parent/order/' + res.orderId + '/pay', {})
      }
      return res
    }).then(() => {
      wx.showToast({ title: payNow ? '支付成功' : '已加入订单', icon: 'success' })
      setTimeout(() => wx.switchTab({ url: '/pages/order/list' }), 800)
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
      .finally(() => wx.hideLoading())
  },
  addOrder() { this.createOrder(false) },
  buyNow() { this.createOrder(true) }
})
