const { getUser } = require('../../utils/auth')
const { request } = require('../../utils/request')
const { go } = require('../../utils/nav')

Page({
  data: {
    user: {},
    menus: [
      { name: '通知公告', icon: '📢', url: '/pages/announcement/list' },
      { name: '课程浏览', icon: '📚', url: '/pages/course/list', tab: true },
      { name: '课程购买', icon: '🛒', url: '/pages/course/list', tab: true },
      { name: '课程订单', icon: '📋', url: '/pages/order/list', tab: true },
      { name: '课程表', icon: '📅', url: '/pages/schedule/list' },
      { name: '考勤信息', icon: '✅', url: '/pages/attendance/list' },
      { name: '考试安排', icon: '📝', url: '/pages/exam/list' },
      { name: '考试成绩', icon: '🏆', url: '/pages/score/list' },
      { name: '留言', icon: '💬', url: '/pages/message/list' }
    ],
    announcements: []
  },
  onLoad: function () {
    this.checkLogin()
  },
  onShow: function () {
    const user = getUser()
    if (!user || user.role !== 'parent') return
    this.setData({ user: user })
    this.loadAnnouncements()
  },
  checkLogin: function () {
    const user = getUser()
    if (!user || user.role !== 'parent') {
      wx.redirectTo({ url: '/pages/login/login' })
    }
  },
  loadAnnouncements: function () {
    const self = this
    request('/app/announcements').then(function (list) {
      self.setData({ announcements: (list || []).slice(0, 3) })
    }).catch(function () {})
  },
  goPage: function (e) {
    go(e.currentTarget.dataset.url)
  },
  goAnnouncement: function () {
    go('/pages/announcement/list')
  }
})
