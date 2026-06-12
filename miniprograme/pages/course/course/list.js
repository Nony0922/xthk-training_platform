const { request } = require('../../utils/request')
const { requireLogin } = require('../../utils/auth')

Page({
  data: { list: [] },
  onShow() {
    if (!requireLogin()) return
    request('/app/courses').then(list => this.setData({ list: list || [] }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  goDetail(e) {
    wx.navigateTo({ url: '/pages/course/detail?id=' + e.currentTarget.dataset.id })
  }
})
