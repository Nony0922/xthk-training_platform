const { request, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { order: null, course: null, statusText: ['待支付', '已支付', '已取消'] },
  onLoad(options) {
    if (!requireLogin()) return
    this.loadDetail(options.id)
  },
  loadDetail(id) {
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/orders').then(list => {
      const order = (list || []).find(o => String(o.id) === String(id))
      this.setData({ order })
      if (order && order.courseId) {
        request('/app/courses/' + order.courseId).then(c => {
          this.setData({ course: fmt.mapCourse(c) })
        }).catch(() => {})
      }
    })
  },
  payOrder() {
    putAction('/app/parent/order/' + this.data.order.id + '/pay', {})
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.loadDetail(this.data.order.id)
      })
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  cancelOrder() {
    const order = this.data.order
    wx.showModal({
      title: '取消订单',
      content: '确定取消该待支付订单吗？',
      success: (res) => {
        if (!res.confirm) return
        putAction('/app/parent/' + getParentId() + '/order/' + order.id + '/cancel', {})
          .then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.loadDetail(order.id)
          })
          .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
      }
    })
  }
})
