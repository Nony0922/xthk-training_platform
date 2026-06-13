const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const { go } = require('../../utils/nav')

Page({
  data: {
    reports: [],
    hasData: false
  },
  onShow() {
    if (!requireLogin()) return
    this.loadData()
  },
  loadData() {
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/reports').then(list => {
      this.setData({
        reports: list || [],
        hasData: (list || []).length > 0
      })
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    go('/pages/report/detail?id=' + id)
  }
})
