const { request, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { list: [], statusText: ['待支付', '已支付', '已取消'] },
  onShow() {
    if (!requireLogin()) return
    this.loadOrders()
  },
  loadOrders() {
    const parentId = getParentId()
    Promise.all([
      request('/app/parent/' + parentId + '/orders'),
      request('/app/courses')
    ]).then(([orders, courses]) => {
      const courseMap = {}
      ;(courses || []).forEach(c => { courseMap[c.id] = fmt.mapCourse(c) })
      const list = (orders || []).map(o => {
        const course = courseMap[o.courseId] || {}
        return {
          ...o,
          teachModeText: course.teachModeText || '-',
          targetGrade: course.targetGrade || '-',
          validPeriodText: course.validPeriodText || '-',
          location: course.location || '-'
        }
      })
      this.setData({ list })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  goDetail(e) {
    wx.navigateTo({ url: '/pages/order/detail?id=' + e.currentTarget.dataset.id })
  },
  payOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showLoading({ title: '支付中' })
    putAction('/app/parent/order/' + id + '/pay', {})
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.loadOrders()
      })
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
      .finally(() => wx.hideLoading())
  },
  cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '取消订单',
      content: '确定取消该待支付订单吗？',
      success: (res) => {
        if (!res.confirm) return
        wx.showLoading({ title: '处理中' })
        putAction('/app/parent/' + getParentId() + '/order/' + id + '/cancel', {})
          .then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.loadOrders()
          })
          .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
          .finally(() => wx.hideLoading())
      }
    })
  }
})
