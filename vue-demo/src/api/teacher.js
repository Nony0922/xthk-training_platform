import request from '@/utils/request'

export const getTeacherListApi = () => request({ url: '/teacher/list', method: 'get' })
export const getTeacherByIdApi = (id) => request({ url: `/teacher/${id}`, method: 'get' })
export const addTeacherApi = (data) => request({ url: '/teacher/add', method: 'post', data })
export const updateTeacherApi = (data) => request({ url: '/teacher/update', method: 'put', data })
export const deleteTeacherApi = (id) => request({ url: `/teacher/${id}`, method: 'delete' })
