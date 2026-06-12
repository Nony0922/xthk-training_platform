import request from '@/utils/request'

export const getAttendanceListApi = () => request({ url: '/attendance/list', method: 'get' })
export const getAttendanceByIdApi = (id) => request({ url: `/attendance/${id}`, method: 'get' })
export const addAttendanceApi = (data) => request({ url: '/attendance/add', method: 'post', data })
export const updateAttendanceApi = (data) => request({ url: '/attendance/update', method: 'put', data })
export const deleteAttendanceApi = (id) => request({ url: `/attendance/${id}`, method: 'delete' })
