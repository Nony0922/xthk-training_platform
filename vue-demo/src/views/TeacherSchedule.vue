<template>
  <div class="teacher-schedule-page">
    <PageSkeleton v-if="pageLoading" variant="grouped" />
    <template v-else>
    <div class="page-desc">
      <p>展示您本人负责授课的全部课程安排，按星期与时间排列，方便确认每节课的班级与教室。</p>
    </div>

    <div class="toolbar">
      <label>学期</label>
      <select v-model="semester" @change="loadSchedules">
        <option value="">全部学期</option>
        <option v-for="s in semesters" :key="s" :value="s">{{ s }}</option>
      </select>
      <button class="btn btn-primary" :disabled="pageLoading" @click="loadSchedules">
        {{ pageLoading ? '加载中...' : '刷新' }}
      </button>
      <span class="stat-tag">共 {{ schedules.length }} 节课</span>
    </div>

    <div v-if="!pageLoading && !schedules.length" class="empty-tip">
      暂无课程表数据，请联系管理员在排课系统中安排课程。
    </div>

    <div v-else class="main-layout">
      <div class="timetable-panel">
        <div class="panel-title">周课表</div>
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
                    class="schedule-block mine"
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
      </div>

      <div class="list-panel">
        <div class="panel-title">课程列表</div>
        <div class="schedule-list">
          <div v-for="item in sortedSchedules" :key="item.id" class="list-item mine">
            <div class="list-day">{{ weekdayText(item.weekday) }}</div>
            <div class="list-body">
              <div class="list-title">{{ item.courseName }}</div>
              <div class="list-meta">{{ shortTime(item.startTime) }} - {{ shortTime(item.endTime) }} · {{ item.className }}</div>
              <div class="list-meta">{{ item.teacherName }} · {{ item.room }} · {{ item.semester || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getScheduleListApi, getScheduleSemestersApi } from '@/api/schedule'
import PageSkeleton from '@/components/PageSkeleton.vue'
import { usePageLoading } from '@/composables/usePageLoading'

const { pageLoading, withLoading } = usePageLoading()

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
const schedules = ref([])

const sortedSchedules = computed(() => {
  return [...schedules.value].sort((a, b) => {
    const dayDiff = (a.weekday || 0) - (b.weekday || 0)
    if (dayDiff !== 0) return dayDiff
    return shortTime(a.startTime).localeCompare(shortTime(b.startTime))
  })
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

const shortTime = (time) => (time || '').substring(0, 5)

const weekdayText = (weekday) => weekdays.find(d => d.value === weekday)?.label || '-'

const getCellSchedules = (weekday, slotKey) => {
  return schedules.value.filter(s => {
    const key = `${shortTime(s.startTime)}-${shortTime(s.endTime)}`
    return s.weekday === weekday && key === slotKey
  })
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

const loadSchedules = () => withLoading(async () => {
  const params = semester.value ? { semester: semester.value } : {}
  const res = await getScheduleListApi(params)
  schedules.value = res.data || []
})

onMounted(async () => {
  await loadSemesters()
  await loadSchedules()
})
</script>

<style scoped>
@import '@/assets/manage.css';

.teacher-schedule-page {
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
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.toolbar label {
  font-size: 14px;
  color: #374151;
}

.toolbar select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  min-width: 140px;
}

.stat-tag {
  padding: 6px 12px;
  border-radius: 999px;
  background: #ede9fe;
  color: #5b21b6;
  font-size: 12px;
  font-weight: 600;
}

.empty-tip {
  padding: 48px 24px;
  text-align: center;
  color: #6b7280;
  background: #fff;
  border: 1px dashed #d1d5db;
  border-radius: 10px;
}

.main-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 16px;
  align-items: start;
}

.timetable-panel,
.list-panel {
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
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  border-left: 3px solid #9ca3af;
  border-radius: 6px;
  padding: 6px 8px;
  margin-bottom: 6px;
  font-size: 12px;
}

.schedule-block.mine,
.list-item.mine {
  background: linear-gradient(135deg, #ede9fe, #e0e7ff);
  border-left-color: #7c3aed;
}

.schedule-block.mine {
  border-left: 3px solid #7c3aed;
}

.block-title,
.list-title {
  font-weight: 600;
  color: #1f2937;
}

.block-meta,
.list-meta {
  color: #6b7280;
  margin-top: 2px;
  font-size: 12px;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 640px;
  overflow-y: auto;
}

.list-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  border-left: 3px solid #9ca3af;
}

.list-day {
  width: 42px;
  flex-shrink: 0;
  font-weight: 700;
  color: #7c3aed;
  font-size: 13px;
}

.list-body {
  min-width: 0;
}

@media (max-width: 1100px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
}
</style>
