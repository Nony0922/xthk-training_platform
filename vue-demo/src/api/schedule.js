import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

export const getScheduleListApi = (params = {}) => request({
  url: '/schedule/list',
  method: 'get',
  params: { ...getTeacherScopeParams(), ...params }
})
export const getScheduleSemestersApi = () => request({ url: '/schedule/semesters', method: 'get' })
export const getScheduleByIdApi = (id) => request({ url: `/schedule/${id}`, method: 'get' })
export const addScheduleApi = (data) => request({ url: '/schedule/add', method: 'post', data })
export const updateScheduleApi = (data) => request({ url: '/schedule/update', method: 'put', data })
export const deleteScheduleApi = (id) => request({ url: `/schedule/${id}`, method: 'delete' })
