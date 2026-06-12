<template>
  <div class="ai-schedule-page">
    <div class="page-desc">
      <p>
        根据教师空闲时间、教室容量与班级学生人数，自动检测排课冲突，并调用
        <strong>讯飞星火大模型</strong> 生成优化建议。下方为可视化周课表展示。
      </p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <label>学期</label>
        <select v-model="semester" @change="loadAnalysis(false)">
          <option value="">全部</option>
          <option v-for="s in semesters" :key="s" :value="s">{{ s }}</option>
        </select>
        <button class="btn btn-primary" :disabled="loading || aiLoading" @click="runAiAnalysis">
          {{ aiLoading ? 'AI 分析中...' : 'AI 深度分析' }}
        </button>
        <button class="btn" :disabled="loading || aiLoading" @click="loadAnalysis(false)">
          刷新检测
        </button>
      </div>
      <div class="toolbar-right">
        <span class="tag tag-ai" :class="{ off: !analysis?.aiEnabled }">
          {{ analysis?.aiEnabled ? '星火大模型已连接' : '仅规则检测（未配置 API）' }}
        </span>
      </div>
    </div>

    <div v-if="analysis?.aiSummary" class="ai-summary">
      <div class="ai-summary-title">AI 综合分析</div>
      <p>{{ analysis.aiSummary }}</p>
    </div>

    <div class="main-layout">
      <div class="timetable-panel">
        <div class="panel-title">可视化课表</div>
        <div class="timetable-wrap">
          <table class="timetable">
            <thead>
              <tr>
                <th class="time-col">时间</th>
                <th v-for="day in weekdays" :key="day.value">{{ day.label }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="slot in timeSlots" :key="slot.key">
                <td class="time-col">{{ slot.label }}</td>
                <td v-for="day in weekdays" :key="day.value" class="slot-cell">
                  <div
                    v-for="item in getCellSchedules(day.value, slot.key)"
                    :key="item.id"
                    class="schedule-block"
                    :class="blockClass(item.id)"
                  >
                    <div class="block-title">{{ item.courseName }}</div>
                    <div class="block-meta">{{ item.className }} · {{ item.teacherName }}</div>
                    <div class="block-meta">{{ item.room }}</div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="legend">
          <span><i class="dot normal"></i>正常</span>
          <span><i class="dot conflict"></i>冲突</span>
        </div>
      </div>

      <div class="side-panel">
        <div class="panel-block">
          <div class="panel-title">冲突检测（{{ conflicts.length }}）</div>
          <div v-if="!conflicts.length" class="empty-tip">暂无冲突</div>
          <div v-for="(c, idx) in conflicts" :key="idx" class="conflict-item" :class="c.severity">
            <div class="conflict-type">{{ conflictTypeText(c.type) }}</div>
            <div class="conflict-msg">{{ c.message }}</div>
            <div v-if="c.detail" class="conflict-detail">{{ c.detail }}</div>
          </div>
        </div>

        <div class="panel-block">
          <div class="panel-title">优化建议（{{ suggestions.length }}）</div>
          <div v-if="!suggestions.length" class="empty-tip">暂无建议</div>
          <div v-for="(s, idx) in suggestions" :key="idx" class="suggestion-item">
            <div class="suggestion-head">
              <span>课表 #{{ s.scheduleId }}</span>
              <span class="source-tag" :class="s.source">{{ s.source === 'ai' ? 'AI' : '规则' }}</span>
            </div>
            <div class="suggestion-action">{{ actionText(s) }}</div>
            <div class="suggestion-reason">{{ s.reason }}</div>
          </div>
        </div>

        <div class="panel-block stats-block">
          <div class="panel-title">资源概览</div>
          <div v-for="item in resourceStats" :key="item.label" class="stat-row">
            <span>{{ item.label }}</span>
            <span>{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getScheduleSemestersApi, analyzeScheduleAiApi } from '@/api/scheduleAi'

const weekdays = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]

const semesters = ref([])
const semester = ref('')
const loading = ref(false)
const aiLoading = ref(false)
const analysis = ref(null)

const schedules = computed(() => analysis.value?.schedules || [])
const conflicts = computed(() => analysis.value?.conflicts || [])
const suggestions = computed(() => analysis.value?.suggestions || [])

const conflictScheduleIds = computed(() => {
  const ids = new Set()
  conflicts.value.forEach(c => (c.scheduleIds || []).forEach(id => ids.add(id)))
  return ids
})

const timeSlots = computed(() => {
  const map = new Map()
  schedules.value.forEach(s => {
    const key = `${shortTime(s.startTime)}-${shortTime(s.endTime)}`
    if (!map.has(key)) {
      map.set(key, { key, label: `${shortTime(s.startTime)} - ${shortTime(s.endTime)}`, start: shortTime(s.startTime) })
    }
  })
  return Array.from(map.values()).sort((a, b) => a.start.localeCompare(b.start))
})

const resourceStats = computed(() => {
  const counts = analysis.value?.classStudentCounts || {}
  const caps = analysis.value?.classCapacities || {}
  const rooms = analysis.value?.roomCapacities || {}
  const classLines = Object.keys(counts).map(id => `班级${id}：${counts[id]}/${caps[id] || '-'}人`)
  const roomLines = Object.keys(rooms).map(r => `${r}：容量${rooms[r]}`)
  return [
    { label: '课表条目', value: schedules.value.length },
    { label: '冲突数量', value: conflicts.value.length },
    { label: '班级人数', value: classLines.join('；') || '-' },
    { label: '教室容量', value: roomLines.join('；') || '-' }
  ]
})

const shortTime = (time) => (time || '').substring(0, 5)

const getCellSchedules = (weekday, slotKey) => {
  return schedules.value.filter(s => {
    const key = `${shortTime(s.startTime)}-${shortTime(s.endTime)}`
    return s.weekday === weekday && key === slotKey
  })
}

const blockClass = (id) => ({
  conflict: conflictScheduleIds.value.has(id)
})

const conflictTypeText = (type) => ({
  teacher: '教师冲突',
  room: '教室冲突',
  capacity: '容量冲突'
}[type] || type)

const actionText = (s) => {
  const dayMap = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const parts = []
  if (s.action === 'move') parts.push(`调整至${dayMap[s.newWeekday] || ''} ${shortTime(s.newStartTime)}-${shortTime(s.newEndTime)}`)
  if (s.action === 'change_room') parts.push(`更换教室为 ${s.newRoom}`)
  if (s.action === 'change_teacher') parts.push(`更换教师 ID=${s.newTeacherId}`)
  return parts.join('，') || '优化排课'
}

const loadSemesters = async () => {
  try {
    const res = await getScheduleSemestersApi()
    semesters.value = res.data || []
    if (semesters.value.length && !semester.value) {
      semester.value = semesters.value[0]
    }
  } catch (e) {
    console.error(e)
  }
}

const loadAnalysis = async (includeAi = false) => {
  try {
    if (includeAi) {
      aiLoading.value = true
    } else {
      loading.value = true
    }
    const res = await analyzeScheduleAiApi(semester.value, includeAi)
    analysis.value = res.data
  } catch (e) {
    alert(e.message)
  } finally {
    loading.value = false
    aiLoading.value = false
  }
}

const runAiAnalysis = () => loadAnalysis(true)

onMounted(async () => {
  await loadSemesters()
  await loadAnalysis(false)
})
</script>

<style scoped>
@import '@/assets/manage.css';

.ai-schedule-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-desc {
  padding: 12px 16px;
  background: linear-gradient(90deg, #f5f3ff, #eef2ff);
  border-radius: 8px;
  border-left: 4px solid #7c3aed;
}

.page-desc p {
  margin: 0;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-left label {
  font-size: 14px;
  color: #374151;
}

.toolbar-left select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  min-width: 140px;
}

.tag-ai {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  background: #d1fae5;
  color: #065f46;
  font-size: 12px;
  font-weight: 600;
}

.tag-ai.off {
  background: #fef3c7;
  color: #92400e;
}

.ai-summary {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
}

.ai-summary-title {
  font-weight: 600;
  color: #7c3aed;
  margin-bottom: 8px;
}

.ai-summary p {
  margin: 0;
  color: #374151;
  line-height: 1.7;
}

.main-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 16px;
  align-items: start;
}

.timetable-panel,
.panel-block {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.timetable-wrap {
  overflow-x: auto;
}

.timetable {
  width: 100%;
  border-collapse: collapse;
  min-width: 760px;
}

.timetable th,
.timetable td {
  border: 1px solid #e5e7eb;
  vertical-align: top;
  padding: 8px;
}

.timetable th {
  background: #f9fafb;
  text-align: center;
  font-size: 13px;
}

.time-col {
  width: 110px;
  background: #fafafa;
  font-size: 12px;
  color: #6b7280;
  white-space: nowrap;
}

.slot-cell {
  min-width: 110px;
  min-height: 72px;
  background: #fcfcff;
}

.schedule-block {
  background: linear-gradient(135deg, #ede9fe, #e0e7ff);
  border-left: 3px solid #7c3aed;
  border-radius: 6px;
  padding: 6px 8px;
  margin-bottom: 6px;
  font-size: 12px;
}

.schedule-block.conflict {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  border-left-color: #ef4444;
  box-shadow: inset 0 0 0 1px #fca5a5;
}

.block-title {
  font-weight: 600;
  color: #1f2937;
}

.block-meta {
  color: #6b7280;
  margin-top: 2px;
}

.legend {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  font-size: 12px;
  color: #6b7280;
}

.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 6px;
  vertical-align: middle;
}

.dot.normal {
  background: #7c3aed;
}

.dot.conflict {
  background: #ef4444;
}

.side-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.conflict-item,
.suggestion-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px 12px;
  margin-bottom: 8px;
  background: #fafafa;
}

.conflict-item.high {
  border-color: #fca5a5;
  background: #fef2f2;
}

.conflict-type {
  font-size: 12px;
  font-weight: 600;
  color: #ef4444;
}

.conflict-msg,
.suggestion-action {
  margin-top: 4px;
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
}

.conflict-detail,
.suggestion-reason {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.suggestion-head {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
}

.source-tag {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}

.source-tag.ai {
  background: #ede9fe;
  color: #7c3aed;
}

.source-tag.rule {
  background: #e5e7eb;
  color: #374151;
}

.empty-tip {
  color: #9ca3af;
  font-size: 13px;
  padding: 8px 0;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px dashed #e5e7eb;
  font-size: 13px;
}

.stat-row:last-child {
  border-bottom: none;
}

@media (max-width: 1100px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
}
</style>
