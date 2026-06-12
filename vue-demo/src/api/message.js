import request from '@/utils/request'

export const getMessageListApi = () => request({ url: '/message/list', method: 'get' })
export const getMessageByIdApi = (id) => request({ url: `/message/${id}`, method: 'get' })
export const addMessageApi = (data) => request({ url: '/message/add', method: 'post', data })
export const updateMessageApi = (data) => request({ url: '/message/update', method: 'put', data })
export const deleteMessageApi = (id) => request({ url: `/message/${id}`, method: 'delete' })
