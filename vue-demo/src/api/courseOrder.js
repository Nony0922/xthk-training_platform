import request from '@/utils/request'

export const getCourseOrderListApi = () => request({ url: '/course-order/list', method: 'get' })
export const getCourseOrderByIdApi = (id) => request({ url: `/course-order/${id}`, method: 'get' })
export const addCourseOrderApi = (data) => request({ url: '/course-order/add', method: 'post', data })
export const updateCourseOrderApi = (data) => request({ url: '/course-order/update', method: 'put', data })
export const deleteCourseOrderApi = (id) => request({ url: `/course-order/${id}`, method: 'delete' })
