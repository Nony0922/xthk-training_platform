const { request } = require('../../utils/request')
const { requireLogin, getParentId } = require('../../utils/auth')

function buildBars(chart) {
  if (!chart || !chart.xAxis || !chart.series || !chart.series[0]) return []
  const data = chart.series[0].data || []
  const max = Math.max.apply(null, data.map(v => Number(v) || 0).concat([1]))
  return chart.xAxis.map((label, i) => ({
    label,
    value: data[i],
    width: Math.round(((Number(data[i]) || 0) / max) * 100)
  }))
}

Page({
  data: {
    loading: true,
    title: '',
    summary: '',
    sections: [],
    columns: [],
    rows: [],
    barCharts: []
  },
  onLoad(options) {
    if (!requireLogin()) return
    if (!options.id) {
      wx.showToast({ title: '报告不存在', icon: 'none' })
      return
    }
    this.loadDetail(options.id)
  },
  loadDetail(reportId) {
    const parentId = getParentId()
    request('/app/parent/' + parentId + '/report/' + reportId).then(data => {
      const charts = data.charts || []
      const barCharts = charts.map(chart => ({
        title: chart.title,
        type: chart.type,
        bars: buildBars(chart)
      }))
      this.setData({
        loading: false,
        title: data.reportTitle || '学情分析报告',
        summary: data.summary || '',
        sections: data.sections || [],
        columns: data.columns || [],
        rows: data.rows || [],
        barCharts
      })
    }).catch(err => {
      this.setData({ loading: false })
      wx.showToast({ title: err.message, icon: 'none' })
    })
  }
})
