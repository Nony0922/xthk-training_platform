const { request } = require('../../utils/request')
const { requireLogin } = require('../../utils/auth')

Page({
  data: { list: [] },
  onShow() {
    if (!requireLogin()) return
    request('/app/announcements').then(list => this.setData({ list: list || [] }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
