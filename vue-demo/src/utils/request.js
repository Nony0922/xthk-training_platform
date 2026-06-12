import axios from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000
})

const getToken = () => {
  const token = localStorage.getItem('token')
  if (token) return token
  const loginUser = localStorage.getItem('loginUser')
  if (!loginUser) return ''
  try {
    const parsed = JSON.parse(loginUser)
    return parsed?.token || ''
  } catch {
    return ''
  }
}

const getErrorMessage = (payload, fallback = '请求失败') => {
  return payload?.msg || payload?.message || fallback
}

service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' &&
      Object.prototype.hasOwnProperty.call(res, 'code')) {
      if (res.code !== 200) {
        return Promise.reject(new Error(getErrorMessage(res)))
      }
      return res
    }
    return { code: 200, msg: 'success', data: res }
  },
  (error) => {
    console.error('响应错误：', error)
    const status = error?.response?.status
    let message = getErrorMessage(error?.response?.data, '服务器错误')

    if (error.code === 'ECONNABORTED') {
      message = '请求超时，请确认后端已启动；若正在调用 AI 分析，请稍后重试'
    } else if (!error.response) {
      message = '无法连接后端服务，请确认 Spring Boot 已在 http://localhost:8080 启动'
    } else if (status === 401) {
      message = '登录已失效，请重新登录'
    }

    return Promise.reject(new Error(message))
  }
)

export default service
