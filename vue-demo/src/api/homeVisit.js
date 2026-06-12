import request from '@/utils/request'

export const getHomeVisitListApi = () => request({ url: '/home-visit/list', method: 'get' })
export const getHomeVisitByIdApi = (id) => request({ url: `/home-visit/${id}`, method: 'get' })
export const addHomeVisitApi = (data) => request({ url: '/home-visit/add', method: 'post', data })
export const updateHomeVisitApi = (data) => request({ url: '/home-visit/update', method: 'put', data })
export const deleteHomeVisitApi = (id) => request({ url: `/home-visit/${id}`, method: 'delete' })
