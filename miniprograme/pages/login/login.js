const { request } = require('../../utils/request')
const { setUser } = require('../../utils/auth')

Page({
  data: { username: '', password: '', loading: false },
  onShow() {
    const user = wx.getStorageSync('loginUser')
    if (user && user.role === 'parent' && user.parentId) {
      setTimeout(function () {
        wx.switchTab({ url: '/pages/index/index' })
      }, 100)
    }
  },
  onUserInput(e) { this.setData({ username: e.detail.value }) },
  onPwdInput(e) { this.setData({ password: e.detail.value }) },
  onLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入账号密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    request('/user/login', 'POST', { username, password })
      .then(user => {
        if (user.role !== 'parent') {
          wx.showToast({ title: '请使用家长账号', icon: 'none' })
          return Promise.reject(new Error('not parent'))
        }
        if (user.parentId) return user
        return request('/app/parent/by-user/' + user.id).then(parent => {
          if (parent && parent.id) {
            user.parentId = parent.id
            return user
          }
          wx.showToast({ title: '未绑定家长信息，请联系管理员', icon: 'none' })
          return Promise.reject(new Error('no parent'))
        })
      })
      .then(user => {
        setUser(user)
        setTimeout(function () {
          wx.switchTab({ url: '/pages/index/index' })
        }, 100)
      })
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
      .finally(() => this.setData({ loading: false }))
  }
})
