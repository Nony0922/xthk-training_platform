const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { list: [] },
  onShow() {
    if (!requireLogin()) return
    request('/app/parent/' + getParentId() + '/schedules').then(list => {
      const mapped = (list || []).map(i => ({ ...i, weekdayText: fmt.weekday(i.weekday) }))
      this.setData({ list: mapped })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
