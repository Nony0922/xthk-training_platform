import request from '@/utils/request'

export const getParentListApi = () => request({ url: '/parent/list', method: 'get' })
export const getParentByIdApi = (id) => request({ url: `/parent/${id}`, method: 'get' })
export const addParentApi = (data) => request({ url: '/parent/add', method: 'post', data })
export const updateParentApi = (data) => request({ url: '/parent/update', method: 'put', data })
export const deleteParentApi = (id) => request({ url: `/parent/${id}`, method: 'delete' })
