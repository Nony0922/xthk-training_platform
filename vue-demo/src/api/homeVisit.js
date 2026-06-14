import request from '@/utils/request'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

const scopeParams = () => getTeacherScopeParams('homeroom')

export const getHomeVisitListApi = () => request({ url: '/home-visit/list', method: 'get', params: scopeParams() })
export const getHomeVisitByIdApi = (id) => request({ url: `/home-visit/${id}`, method: 'get' })
export const addHomeVisitApi = (data) => request({ url: '/home-visit/add', method: 'post', data, params: scopeParams() })
export const updateHomeVisitApi = (data) => request({ url: '/home-visit/update', method: 'put', data, params: scopeParams() })
export const deleteHomeVisitApi = (id) => request({ url: `/home-visit/${id}`, method: 'delete', params: scopeParams() })
