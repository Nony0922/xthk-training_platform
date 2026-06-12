import request from '@/utils/request'

export const getCourseListApi = () => request({ url: '/course/list', method: 'get' })
export const getCourseByIdApi = (id) => request({ url: `/course/${id}`, method: 'get' })
export const addCourseApi = (data) => request({ url: '/course/add', method: 'post', data })
export const updateCourseApi = (data) => request({ url: '/course/update', method: 'put', data })
export const deleteCourseApi = (id) => request({ url: `/course/${id}`, method: 'delete' })
