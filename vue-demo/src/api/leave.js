import request from '@/utils/request'

export const getLeaveListApi = () => request({ url: '/leave/list', method: 'get' })
export const getLeaveByIdApi = (id) => request({ url: `/leave/${id}`, method: 'get' })
export const addLeaveApi = (data) => request({ url: '/leave/add', method: 'post', data })
export const updateLeaveApi = (data) => request({ url: '/leave/update', method: 'put', data })
export const deleteLeaveApi = (id) => request({ url: `/leave/${id}`, method: 'delete' })
