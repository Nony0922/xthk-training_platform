const { request, putAction } = require('../../utils/request')
const { requireLogin, getParentId, getUser, clearUser } = require('../../utils/auth')
const { go: navigateToPage } = require('../../utils/nav')

Page({
  data: { user: {}, profile: null, form: {}, students: [] },
  onShow() {
    const user = requireLogin()
    if (!user) return
    this.setData({ user })
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/profile').then(profile => {
      this.setData({ profile: profile, form: Object.assign({}, profile) })
    })
    request('/app/parent/' + parentId + '/students').then(students => {
      this.setData({ students: students || [] })
    })
  },
  onField(e) {
    const key = e.currentTarget.dataset.key
    this.setData({ ['form.' + key]: e.detail.value })
  },
  save() {
    putAction('/app/parent/profile', this.data.form)
      .then(() => wx.showToast({ title: '保存成功', icon: 'success' }))
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  },
  go(e) {
    navigateToPage(e.currentTarget.dataset.url)
  },
  logout() {
    clearUser()
    wx.reLaunch({ url: '/pages/login/login' })
  }
})
