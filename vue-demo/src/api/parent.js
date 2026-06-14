import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

export const getParentListApi = () => request({ url: '/parent/list', method: 'get', params: getTeacherScopeParams('homeroom') })
export const getParentByIdApi = (id) => request({ url: `/parent/${id}`, method: 'get' })
export const addParentApi = (data) => request({ url: '/parent/add', method: 'post', data })
export const updateParentApi = (data) => request({ url: '/parent/update', method: 'put', data })
export const deleteParentApi = (id) => request({ url: `/parent/${id}`, method: 'delete' })
