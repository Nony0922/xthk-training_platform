const { request, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')

Page({
  data: { order: null, courseDesc: '', statusText: ['待支付', '已支付', '已取消'] },
  onLoad(options) {
    if (!requireLogin()) return
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/orders').then(list => {
      const order = (list || []).find(o => String(o.id) === String(options.id))
      this.setData({ order })
      if (order && order.courseId) {
        request('/app/courses/' + order.courseId).then(c => {
          this.setData({ courseDesc: c ? c.description : '' })
        }).catch(() => {})
      }
    })
  },
  payOrder() {
    putAction('/app/parent/order/' + this.data.order.id + '/pay', {})
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.onLoad({ id: this.data.order.id })
      })
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
