function getUser() {
  return wx.getStorageSync('loginUser') || null
}

function getParentId() {
  const user = getUser()
  return user ? user.parentId : null
}

function setUser(user) {
  wx.setStorageSync('loginUser', user)
}

function clearUser() {
  wx.removeStorageSync('loginUser')
}

function requireLogin() {
  const user = getUser()
  if (!user || user.role !== 'parent') {
    wx.reLaunch({ url: '/pages/login/login' })
    return null
  }
  return user
}

module.exports = { getUser, getParentId, setUser, clearUser, requireLogin }
