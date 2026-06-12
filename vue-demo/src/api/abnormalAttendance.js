import request from '@/utils/request'

export const getAbnormalAttendanceListApi = () => request({ url: '/abnormal-attendance/list', method: 'get' })
export const getAbnormalAttendanceByIdApi = (id) => request({ url: `/abnormal-attendance/${id}`, method: 'get' })
export const addAbnormalAttendanceApi = (data) => request({ url: '/abnormal-attendance/add', method: 'post', data })
export const updateAbnormalAttendanceApi = (data) => request({ url: '/abnormal-attendance/update', method: 'put', data })
export const deleteAbnormalAttendanceApi = (id) => request({ url: `/abnormal-attendance/${id}`, method: 'delete' })
