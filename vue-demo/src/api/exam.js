import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

export const getExamListApi = (mode) => request({ url: '/exam/list', method: 'get', params: getTeacherScopeParams(mode) })
export const getExamByIdApi = (id) => request({ url: `/exam/${id}`, method: 'get' })
export const addExamApi = (data) => request({ url: '/exam/add', method: 'post', data })
export const updateExamApi = (data) => request({ url: '/exam/update', method: 'put', data })
export const deleteExamApi = (id) => request({ url: `/exam/${id}`, method: 'delete' })
