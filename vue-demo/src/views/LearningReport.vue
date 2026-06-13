<template>
  <div class="learning-report-page">
    <div class="page-desc">
      <p>
        输入自然语言问题，系统将调用 <strong>讯飞星火大模型</strong> 自动生成 SQL 查询考勤、成绩等学情数据，
        智能适配柱状图、饼图等可视化形式，并撰写学情分析报告。支持一键导出 PDF，推送后家长可在小程序查看。
      </p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <label>分析问题</label>
        <input
          v-model="question"
          class="question-input"
          placeholder="例如：统计一年级1班各考勤状态的人数分布"
          :disabled="loading"
          @keyup.enter="runAnalyze(false)"
        />
        <button class="btn btn-primary" :disabled="loading" @click="runAnalyze(false)">
          {{ loading ? 'AI 分析中...' : '生成学情报告' }}
        </button>
        <button class="btn" :disabled="loading" @click="runAnalyze(true)">
          生成并推送家长
        </button>
      </div>
      <div class="toolbar-right">
        <span class="tag tag-ai" :class="{ off: !aiEnabled }">
          {{ aiEnabled ? '星火大模型已连接' : '降级模式（未配置 API）' }}
        </span>
      </div>
    </div>

    <div v-if="loading" class="loading-tip">
      {{ loadingHint }}（大模型分析约需 20–60 秒，请耐心等待）
    </div>
    <div v-if="errorMsg" class="error-tip">{{ errorMsg }}</div>

    <div class="filter-row">
      <label>快捷问题</label>
      <div class="preset-tags">
        <button
          v-for="item in presets"
          :key="item"
          class="preset-btn"
          :disabled="loading"
          @click="usePreset(item)"
        >
          {{ item }}
        </button>
      </div>
    </div>

    <div class="filter-row">
      <label>数据范围</label>
      <select v-model="classId" :disabled="loading">
        <option value="">全部班级</option>
        <option v-for="c in classes" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
      </select>
      <select v-model="studentId" :disabled="loading">
        <option value="">全部学生</option>
        <option v-for="s in filteredStudents" :key="s.id" :value="String(s.id)">{{ s.name }}</option>
      </select>
    </div>

    <div v-if="result" ref="exportArea" class="report-area">
      <div class="report-header">
        <div>
          <h2>{{ result.reportTitle || '学情分析报告' }}</h2>
          <p class="meta">问题：{{ result.question }}</p>
          <p v-if="result.publishedToParent" class="meta published">已推送给家长，可在小程序「学情报告」中查看</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-export" @click="exportPdf">一键导出 PDF</button>
        </div>
      </div>

      <div v-if="result.summary" class="summary-box">
        <div class="box-title">总体概述</div>
        <p>{{ result.summary }}</p>
      </div>

      <div v-if="result.charts?.length" class="charts-panel">
        <div class="box-title">数据可视化</div>
        <div class="charts-grid">
          <div v-for="(chart, idx) in result.charts" :key="'chart-' + idx" class="chart-card">
            <div :id="'learning-chart-' + idx" class="chart-container"></div>
          </div>
        </div>
      </div>

      <div v-if="result.columns?.length" class="table-panel">
        <div class="box-title">查询结果（{{ result.rows?.length || 0 }} 条）</div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th v-for="col in result.columns" :key="col">{{ col }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, rIdx) in result.rows" :key="rIdx">
                <td v-for="(cell, cIdx) in row" :key="cIdx">{{ cell }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="result.sections?.length" class="sections-panel">
        <div class="box-title">学情分析</div>
        <div v-for="(sec, idx) in result.sections" :key="idx" class="section-item">
          <h3>{{ sec.heading }}</h3>
          <p>{{ sec.content }}</p>
        </div>
      </div>
    </div>

    <div class="history-panel">
      <div class="box-title">历史报告</div>
      <div v-if="!history.length" class="empty-tip">暂无历史报告</div>
      <div v-for="item in history" :key="item.id" class="history-item">
        <div class="history-main" @click="loadHistory(item.id)">
          <div class="history-title">{{ item.title }}</div>
          <div class="history-meta">
            {{ item.createTime }} · {{ item.creatorName || '-' }}
            <span v-if="item.parentVisible === 1" class="badge">已推送家长</span>
          </div>
        </div>
        <button class="btn-link danger" @click.stop="removeReport(item.id)">删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import html2canvas from 'html2canvas'
import { jsPDF } from 'jspdf'
import { getClazzListApi } from '@/api/clazz'
import { getStudentListApi } from '@/api/student'
import {
  analyzeLearningReportApi,
  deleteLearningReportApi,
  getLearningReportDetailApi,
  getLearningReportListApi,
  getReportPresetsApi,
  getReportStatusApi
} from '@/api/learningReport'
import { getTeacherScopeParams } from '@/composables/useTeacherScope'

const question = ref('')
const classId = ref('')
const studentId = ref('')
const loading = ref(false)
const loadingHint = ref('')
const errorMsg = ref('')
const aiEnabled = ref(false)
const result = ref(null)
const presets = ref([])
const classes = ref([])
const students = ref([])
const history = ref([])
const exportArea = ref(null)
const chartInstances = []

const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('loginUser'))
  } catch {
    return null
  }
})

const filteredStudents = computed(() => {
  if (!classId.value) return students.value
  const cid = Number(classId.value)
  return students.value.filter(s => s.classId === cid)
})

const loadAiStatus = async () => {
  try {
    const res = await getReportStatusApi()
    aiEnabled.value = !!res.data?.aiEnabled
  } catch {
    aiEnabled.value = false
  }
}

const loadPresets = async () => {
  const res = await getReportPresetsApi()
  presets.value = res.data || []
}

const loadClasses = async () => {
  const res = await getClazzListApi()
  classes.value = res.data || []
}

const loadStudents = async () => {
  const res = await getStudentListApi()
  students.value = res.data || []
}

const loadHistory = async (id) => {
  if (id) {
    errorMsg.value = ''
    const res = await getLearningReportDetailApi(id)
    result.value = res.data
    await renderCharts()
    return
  }
  const params = {
    creatorRole: user.value?.role,
    creatorId: user.value?.id
  }
  const res = await getLearningReportListApi(params)
  history.value = res.data || []
}

const buildPayload = (publishToParent) => ({
  question: question.value.trim(),
  classId: classId.value ? Number(classId.value) : null,
  studentId: studentId.value ? Number(studentId.value) : null,
  publishToParent,
  creatorRole: user.value?.role,
  creatorId: user.value?.id,
  creatorName: user.value?.name,
  ...getTeacherScopeParams()
})

const usePreset = (text) => {
  question.value = text
  errorMsg.value = ''
}

const runAnalyze = async (publishToParent) => {
  if (!question.value.trim()) {
    errorMsg.value = '请输入分析问题'
    return
  }
  loading.value = true
  errorMsg.value = ''
  loadingHint.value = aiEnabled.value ? '正在调用星火大模型分析学情数据...' : '正在使用预置模板查询数据...'
  disposeCharts()
  result.value = null
  try {
    const res = await analyzeLearningReportApi(buildPayload(publishToParent))
    result.value = res.data
    aiEnabled.value = !!res.data?.aiEnabled
    await renderCharts()
    await loadHistory()
  } catch (e) {
    errorMsg.value = e.message || '分析失败，请稍后重试'
  } finally {
    loading.value = false
    loadingHint.value = ''
  }
}

const removeReport = async (id) => {
  if (!confirm('确定删除该报告？')) return
  try {
    await deleteLearningReportApi(id)
    if (result.value?.id === id) {
      result.value = null
      disposeCharts()
    }
    await loadHistory()
  } catch (e) {
    errorMsg.value = e.message || '删除失败'
  }
}

const buildChartOption = (chart) => {
  if (chart.type === 'pie') {
    const data = (chart.xAxis || []).map((name, i) => ({
      name,
      value: chart.series?.[0]?.data?.[i] ?? 0
    }))
    return {
      title: { text: chart.title, left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '55%', data }]
    }
  }
  return {
    title: { text: chart.title, textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis' },
    legend: { data: (chart.series || []).map(s => s.name) },
    xAxis: { type: 'category', data: chart.xAxis || [], axisLabel: { rotate: 30 } },
    yAxis: { type: 'value' },
    grid: { left: 48, right: 24, bottom: 48, top: 48 },
    series: (chart.series || []).map(s => ({
      name: s.name,
      type: chart.type === 'line' ? 'line' : 'bar',
      data: s.data || [],
      smooth: chart.type === 'line'
    }))
  }
}

const renderCharts = async () => {
  disposeCharts()
  if (!result.value?.charts?.length) return
  await nextTick()
  await new Promise(resolve => requestAnimationFrame(resolve))
  result.value.charts.forEach((chart, idx) => {
    const el = document.getElementById('learning-chart-' + idx)
    if (!el) return
    const instance = echarts.init(el)
    instance.setOption(buildChartOption(chart))
    chartInstances.push(instance)
  })
}

const disposeCharts = () => {
  chartInstances.forEach(c => c.dispose())
  chartInstances.length = 0
}

const exportPdf = async () => {
  if (!exportArea.value) return
  try {
    const canvas = await html2canvas(exportArea.value, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff'
    })
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const imgWidth = pageWidth - 20
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    let heightLeft = imgHeight
    let position = 10

    pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight)
    heightLeft -= pageHeight

    while (heightLeft > 0) {
      pdf.addPage()
      position = heightLeft - imgHeight + 10
      pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight)
      heightLeft -= pageHeight
    }

    pdf.save(`${result.value?.reportTitle || '学情分析报告'}.pdf`)
  } catch (e) {
    errorMsg.value = '导出失败：' + (e.message || '未知错误')
  }
}

watch(classId, () => {
  if (studentId.value && !filteredStudents.value.some(s => String(s.id) === studentId.value)) {
    studentId.value = ''
  }
})

onMounted(async () => {
  await Promise.all([loadAiStatus(), loadPresets(), loadClasses(), loadStudents(), loadHistory()])
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  disposeCharts()
  window.removeEventListener('resize', handleResize)
})

const handleResize = () => {
  chartInstances.forEach(c => c.resize())
}
</script>

<style scoped>
.learning-report-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-desc {
  background: #faf5ff;
  border: 1px solid #e9d5ff;
  border-radius: 8px;
  padding: 14px 18px;
  color: #4c1d95;
  font-size: 14px;
  line-height: 1.6;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  flex: 1;
}

.toolbar-left label {
  font-size: 14px;
  color: #374151;
  white-space: nowrap;
}

.question-input {
  flex: 1;
  min-width: 280px;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.loading-tip {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1d4ed8;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13px;
}

.error-tip {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-row label {
  font-size: 13px;
  color: #6b7280;
}

.filter-row select {
  padding: 6px 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 13px;
}

.preset-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preset-btn {
  padding: 4px 10px;
  border: 1px solid #ddd6fe;
  background: #f5f3ff;
  color: #6d28d9;
  border-radius: 999px;
  font-size: 12px;
  cursor: pointer;
}

.preset-btn:hover:not(:disabled) {
  background: #ede9fe;
}

.preset-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn {
  padding: 8px 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #7c3aed;
  border-color: #7c3aed;
  color: #fff;
}

.btn-export {
  background: #059669;
  border-color: #059669;
  color: #fff;
}

.tag {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: #dcfce7;
  color: #166534;
}

.tag.off {
  background: #fef3c7;
  color: #92400e;
}

.report-area,
.history-panel {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.report-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.report-header h2 {
  margin: 0 0 8px;
  color: #1f2937;
}

.meta {
  margin: 4px 0;
  font-size: 13px;
  color: #6b7280;
}

.meta.published {
  color: #059669;
}

.box-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.summary-box {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 16px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 16px;
}

.chart-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

th, td {
  border: 1px solid #e5e7eb;
  padding: 8px 10px;
  text-align: left;
}

th {
  background: #f9fafb;
}

.sections-panel {
  margin-top: 16px;
}

.section-item {
  margin-bottom: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid #f3f4f6;
}

.section-item h3 {
  margin: 0 0 6px;
  font-size: 15px;
  color: #7c3aed;
}

.section-item p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #374151;
  white-space: pre-wrap;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6;
}

.history-main {
  flex: 1;
  cursor: pointer;
}

.history-title {
  font-size: 14px;
  color: #1f2937;
}

.history-meta {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.badge {
  margin-left: 8px;
  padding: 2px 6px;
  background: #ede9fe;
  color: #6d28d9;
  border-radius: 4px;
}

.btn-link {
  border: none;
  background: none;
  color: #7c3aed;
  cursor: pointer;
  font-size: 13px;
}

.btn-link.danger {
  color: #dc2626;
}

.empty-tip {
  color: #9ca3af;
  font-size: 13px;
}
</style>
