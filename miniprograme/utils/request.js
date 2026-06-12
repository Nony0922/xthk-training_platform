const { BASE_URL } = require('./config')

function getErrorMessage(err) {
  const msg = (err && err.errMsg) || (err && err.message) || ''
  if (msg.indexOf('timeout') !== -1 || msg.indexOf('超时') !== -1) {
    return '连接服务器超时，请检查：1.后端是否已启动 2.MySQL是否运行 3.config.js地址是否正确'
  }
  if (msg.indexOf('fail') !== -1 || msg.indexOf('connect') !== -1) {
    return '无法连接服务器，请确认后端地址与网络'
  }
  return msg || '网络错误'
}

function request(url, method, data) {
  if (method === undefined) method = 'GET'
  if (data === undefined) data = {}
  return new Promise(function (resolve, reject) {
    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      timeout: 30000,
      header: { 'content-type': 'application/json' },
      success: function (res) {
        const body = res.data
        if (Array.isArray(body)) {
          resolve(body)
          return
        }
        if (body && typeof body === 'object' && body.code !== undefined) {
          if (body.code === 200) {
            resolve(body.data !== undefined ? body.data : body)
          } else {
            reject(new Error(body.msg || '请求失败'))
          }
        } else {
          resolve(body)
        }
      },
      fail: function (err) {
        reject(new Error(getErrorMessage(err)))
      }
    })
  })
}

function postAction(url, data) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: BASE_URL + url,
      method: 'POST',
      data: data,
      timeout: 30000,
      header: { 'content-type': 'application/json' },
      success: function (res) {
        const body = res.data
        if (body && body.code === 200) resolve(body)
        else reject(new Error((body && body.msg) || '请求失败'))
      },
      fail: function (err) {
        reject(new Error(getErrorMessage(err)))
      }
    })
  })
}

function putAction(url, data) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: BASE_URL + url,
      method: 'PUT',
      data: data,
      timeout: 30000,
      header: { 'content-type': 'application/json' },
      success: function (res) {
        const body = res.data
        if (body && body.code === 200) resolve(body)
        else reject(new Error((body && body.msg) || '请求失败'))
      },
      fail: function (err) {
        reject(new Error(getErrorMessage(err)))
      }
    })
  })
}

module.exports = { request, postAction, putAction }
