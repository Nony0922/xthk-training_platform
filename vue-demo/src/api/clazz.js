import request from '@/utils/request'

export const getClazzListApi = () => request({ url: '/clazz/list', method: 'get' })
export const getClazzByIdApi = (id) => request({ url: `/clazz/${id}`, method: 'get' })
export const addClazzApi = (data) => request({ url: '/clazz/add', method: 'post', data })
export const updateClazzApi = (data) => request({ url: '/clazz/update', method: 'put', data })
export const deleteClazzApi = (id) => request({ url: `/clazz/${id}`, method: 'delete' })
