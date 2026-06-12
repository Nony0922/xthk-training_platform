const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { list: [] },
  onShow() {
    if (!requireLogin()) return
    request('/app/parent/' + getParentId() + '/attendance').then(list => {
      const mapped = (list || []).map(i => ({ ...i, statusText: fmt.attend(i.status) }))
      this.setData({ list: mapped })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
