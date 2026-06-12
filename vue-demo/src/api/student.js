import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

export const getStudentListApi = () => request({ url: '/student/list', method: 'get', params: getTeacherScopeParams() })
export const getStudentByIdApi = (id) => request({ url: `/student/${id}`, method: 'get' })
export const addStudentApi = (data) => request({ url: '/student/add', method: 'post', data })
export const updateStudentApi = (data) => request({ url: '/student/update', method: 'put', data })
export const deleteStudentApi = (id) => request({ url: `/student/${id}`, method: 'delete' })
