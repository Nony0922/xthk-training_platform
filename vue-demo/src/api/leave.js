import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

export const getLeaveListApi = () => request({ url: '/leave/list', method: 'get', params: getTeacherScopeParams() })
export const getLeaveByIdApi = (id) => request({ url: `/leave/${id}`, method: 'get' })
export const addLeaveApi = (data) => request({ url: '/leave/add', method: 'post', data })
export const updateLeaveApi = (data) => request({ url: '/leave/update', method: 'put', data })
export const deleteLeaveApi = (id) => request({ url: `/leave/${id}`, method: 'delete' })
