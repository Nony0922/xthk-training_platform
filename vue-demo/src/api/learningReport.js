import request from '@/utils/request'

export const getReportPresetsApi = () => request({
  url: '/report/ai/presets',
  method: 'get'
})

export const getReportStatusApi = () => request({
  url: '/report/ai/status',
  method: 'get'
})

export const analyzeLearningReportApi = (data) => request({
  url: '/report/ai/analyze',
  method: 'post',
  data,
  timeout: 180000
})

export const getLearningReportListApi = (params) => request({
  url: '/report/ai/list',
  method: 'get',
  params
})

export const getLearningReportDetailApi = (id) => request({
  url: `/report/ai/${id}`,
  method: 'get'
})

export const deleteLearningReportApi = (id) => request({
  url: `/report/ai/${id}`,
  method: 'delete'
})
