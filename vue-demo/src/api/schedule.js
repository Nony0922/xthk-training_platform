import request from '@/utils/request'

export const getScheduleListApi = () => request({ url: '/schedule/list', method: 'get' })
export const getScheduleByIdApi = (id) => request({ url: `/schedule/${id}`, method: 'get' })
export const addScheduleApi = (data) => request({ url: '/schedule/add', method: 'post', data })
export const updateScheduleApi = (data) => request({ url: '/schedule/update', method: 'put', data })
export const deleteScheduleApi = (id) => request({ url: `/schedule/${id}`, method: 'delete' })
