const { request, putAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { list: [], statusText: ['待支付', '已支付', '已取消'] },
  onShow() {
    const user = requireLogin()
    if (!user) return
    this.loadOrders()
  },
  loadOrders() {
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/orders')
      .then(list => this.setData({ list: list || [] }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
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
  }
})
