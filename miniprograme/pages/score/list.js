const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')

Page({
  data: { list: [] },
  onShow() {
    if (!requireLogin()) return
    request('/app/parent/' + getParentId() + '/scores')
      .then(list => this.setData({ list: list || [] }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
