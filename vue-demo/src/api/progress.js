import request from '@/utils/request'

export const getProgressListApi = () => request({ url: '/progress/list', method: 'get' })
export const getProgressByIdApi = (id) => request({ url: `/progress/${id}`, method: 'get' })
export const addProgressApi = (data) => request({ url: '/progress/add', method: 'post', data })
export const updateProgressApi = (data) => request({ url: '/progress/update', method: 'put', data })
export const deleteProgressApi = (id) => request({ url: `/progress/${id}`, method: 'delete' })
