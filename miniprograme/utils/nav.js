/** TabBar 页面路径（必须用 switchTab 跳转） */
const TAB_PAGES = [
  '/pages/index/index',
  '/pages/course/list',
  '/pages/order/list',
  '/pages/profile/profile'
]

function isTabPage(url) {
  return TAB_PAGES.indexOf(url) !== -1
}

/**
 * 统一页面跳转：TabBar 页用 switchTab，其余用 navigateTo
 * @param {string} url 以 / 开头的页面路径
 */
function go(url) {
  if (!url) {
    wx.showToast({ title: '页面地址无效', icon: 'none' })
    return
  }
  const navigate = isTabPage(url) ? wx.switchTab : wx.navigateTo
  navigate({
    url: url,
    fail: function (err) {
      console.error('navigate fail:', url, err)
      wx.showToast({ title: '页面跳转失败', icon: 'none' })
    }
  })
}

module.exports = { go, TAB_PAGES, isTabPage }
