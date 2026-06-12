import request from '@/utils/request'

export const getScheduleSemestersApi = () => request({
  url: '/schedule/ai/semesters',
  method: 'get',
  timeout: 15000
})

export const analyzeScheduleAiApi = (semester, includeAi = false) => request({
  url: '/schedule/ai/analyze',
  method: 'get',
  params: {
    ...(semester ? { semester } : {}),
    includeAi
  },
  timeout: includeAi ? 120000 : 15000
})
