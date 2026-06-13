const { request } = require('../../utils/request')
const { requireLogin } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: {
    allList: [],
    list: [],
    filterMode: 0,
    modes: ['全部', '线下', '线上', '混合']
  },
  onShow() {
    if (!requireLogin()) return
    this.loadList()
  },
  loadList() {
    request('/app/courses').then(list => {
      const mapped = (list || []).map(fmt.mapCourse)
      this.setData({ allList: mapped })
      this.applyFilter(mapped, this.data.filterMode)
    }).catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  setFilter(e) {
    const mode = Number(e.currentTarget.dataset.mode)
    this.setData({ filterMode: mode })
    this.applyFilter(this.data.allList, mode)
  },
  applyFilter(allList, mode) {
    let list = allList || []
    if (mode > 0) list = list.filter(c => c.teachMode === mode)
    this.setData({ list })
  },
  goDetail(e) {
    wx.navigateTo({ url: '/pages/course/detail?id=' + e.currentTarget.dataset.id })
  }
})
