import request from '@/utils/request'

export const loginApi = (data) => request({ url: '/user/login', method: 'post', data })
export const getUserListApi = () => request({ url: '/user/list', method: 'get' })
export const getUserByIdApi = (id) => request({ url: `/user/${id}`, method: 'get' })
export const addUserApi = (data) => request({ url: '/user/add', method: 'post', data })
export const updateUserApi = (data) => request({ url: '/user/update', method: 'put', data })
export const deleteUserApi = (id) => request({ url: `/user/${id}`, method: 'delete' })
