import request from '@/utils/request'

export const getAnnouncementListApi = () => request({ url: '/announcement/list', method: 'get' })
export const getAnnouncementByIdApi = (id) => request({ url: `/announcement/${id}`, method: 'get' })
export const addAnnouncementApi = (data) => request({ url: '/announcement/add', method: 'post', data })
export const updateAnnouncementApi = (data) => request({ url: '/announcement/update', method: 'put', data })
export const deleteAnnouncementApi = (id) => request({ url: `/announcement/${id}`, method: 'delete' })
