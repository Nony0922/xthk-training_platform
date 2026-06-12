import request from '@/utils/request'

export const getScoreListApi = () => request({ url: '/score/list', method: 'get' })
export const getScoreByIdApi = (id) => request({ url: `/score/${id}`, method: 'get' })
export const addScoreApi = (data) => request({ url: '/score/add', method: 'post', data })
export const updateScoreApi = (data) => request({ url: '/score/update', method: 'put', data })
export const deleteScoreApi = (id) => request({ url: `/score/${id}`, method: 'delete' })
