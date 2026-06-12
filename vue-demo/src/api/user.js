import request from '@/utils/request'

export const loginApi = (data) => request({ url: '/user/login', method: 'post', data })
export const getUserListApi = () => request({ url: '/user/list', method: 'get' })
