const { request, postAction } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')
const fmt = require('../../utils/format')

Page({
  data: { list: [], content: '' },
  onShow() {
    if (!requireLogin()) return
    this.loadList()
  },
  loadList() {
    request('/app/parent/' + getParentId() + '/messages').then(list => {
      const mapped = (list || []).map(i => ({ ...i, statusText: fmt.msg(i.status) }))
      this.setData({ list: mapped })
    })
  },
  onInput(e) { this.setData({ content: e.detail.value }) },
  submit() {
    const content = this.data.content.trim()
    if (!content) return wx.showToast({ title: '请输入留言', icon: 'none' })
    postAction('/app/parent/message', { parentId: getParentId(), content })
      .then(() => {
        wx.showToast({ title: '提交成功', icon: 'success' })
        this.setData({ content: '' })
        this.loadList()
      })
      .catch(err => wx.showToast({ title: err.message, icon: 'none' }))
  }
})
