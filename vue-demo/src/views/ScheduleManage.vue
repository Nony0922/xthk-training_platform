<template>
  <div class="manage-page">
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增课程表</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>班级</th>
            <th>课程</th>
            <th>教师</th>
            <th>星期</th>
            <th>开始</th>
            <th>结束</th>
            <th>教室</th>
            <th>学期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.className ?? '-' }}</td>
            <td>{{ item.courseName ?? '-' }}</td>
            <td>{{ item.teacherName ?? '-' }}</td>
            <td>{{ formatCell(item.weekday, 'weekday') }}</td>
            <td>{{ item.startTime ?? '-' }}</td>
            <td>{{ item.endTime ?? '-' }}</td>
            <td>{{ item.room ?? '-' }}</td>
            <td>{{ item.semester ?? '-' }}</td>
            <td class="actions">
              <button class="btn btn-sm btn-info" @click="handleEdit(item)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑' : '新增' }}课程表</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>班级</label>
            <select v-model="form.classId"><option :value="null">请选择</option><option v-for="c in classes" :key="c.id" :value="c.id">{{ c.name }}</option></select>
          </div>
          <div class="form-item">
            <label>课程</label>
            <select v-model="form.courseId" @change="onCourseChange"><option :value="null">请选择</option><option v-for="c in courses" :key="c.id" :value="c.id">{{ c.name }}</option></select>
          </div>
          <div class="form-item">
            <label>教师</label>
            <select v-model="form.teacherId"><option :value="null">请选择</option><option v-for="t in teachers" :key="t.id" :value="t.id">{{ t.name }}</option></select>
          </div>
          <div class="form-item">
            <label>星期</label>
            <select v-model="form.weekday">
            <option :value="1">周一</option>
            <option :value="2">周二</option>
            <option :value="3">周三</option>
            <option :value="4">周四</option>
            <option :value="5">周五</option>
            <option :value="6">周六</option>
            <option :value="7">周日</option>
          </select>
          </div>
          <div class="form-item">
            <label>开始时间</label>
            <input v-model="form.startTime" type="time" placeholder="请输入开始时间" />
          </div>
          <div class="form-item">
            <label>结束时间</label>
            <input v-model="form.endTime" type="time" placeholder="请输入结束时间" />
          </div>
          <div class="form-item">
            <label>教室</label>
            <input v-model="form.room" type="text" placeholder="请输入教室" />
          </div>
          <div class="form-item">
            <label>学期</label>
            <input v-model="form.semester" type="text" placeholder="请输入学期" />
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="loading">{{ loading ? '提交中...' : '确定' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getScheduleListApi, addScheduleApi, updateScheduleApi, deleteScheduleApi } from '@/api/schedule'
import { getClazzListApi } from '@/api/clazz'
import { getCourseListApi } from '@/api/course'
import { getTeacherListApi } from '@/api/teacher'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const classes = ref([])
const courses = ref([])
const teachers = ref([])

const form = reactive({
  id: null,
  classId: null,
  courseId: null,
  teacherId: null,
  weekday: 1,
  startTime: '',
  endTime: '',
  room: '',
  semester: ''
})


const formatCell = (v, type) => {
  const m = {
    gender: v => v === 1 ? '男' : v === 2 ? '女' : '-',
    teacherLevel: v => v === 2 ? '班主任' : v === 1 ? '任课教师' : '-',
    status: v => v === 1 ? '正常' : '停用',
    shelf: v => v === 1 ? '上架' : '下架',
    annStatus: v => v === 1 ? '已发布' : '草稿',
    role: v => ({all:'全部',admin:'管理员',teacher:'教师',parent:'家长'}[v] || v),
    weekday: v => ['','周一','周二','周三','周四','周五','周六','周日'][v] || '-',
    progressStatus: v => ['未开始','进行中','已完成'][v] || '-',
    examStatus: v => ['未开始','进行中','已结束'][v] || '-',
    attendanceStatus: v => ['','正常','迟到','早退','缺勤','请假'][v] || '-',
    abnormalType: v => ['','','迟到','早退','缺勤'][v] || '-',
    handleStatus: v => v === 1 ? '已处理' : '待处理',
    leaveType: v => ['','事假','病假','其他'][v] || '-',
    leaveStatus: v => ['待审批','已通过','已驳回'][v] || '-',
    visitType: v => ['','上门','电话','线上'][v] || '-',
    orderStatus: v => ['待支付','已支付','已取消'][v] || '-',
    msgStatus: v => v === 1 ? '已回复' : '待回复',
  }
  return (m[type] ? m[type](v) : v) ?? '-'
}
const onCourseChange = () => {}

const resetForm = () => {
  Object.assign(form, { id: null, classId: null,
  courseId: null,
  teacherId: null,
  weekday: 1,
  startTime: '',
  endTime: '',
  room: '',
  semester: '' })
}

const loadList = async () => {
  try {
    const res = await getScheduleListApi()
    list.value = res.data || []
  } catch (e) { alert(e.message) }
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (item) => {
  isEdit.value = true
  Object.assign(form, { ...item })
  dialogVisible.value = true
}

const handleSubmit = async () => {

  try {
    loading.value = true
    if (isEdit.value) await updateScheduleApi(form)
    else await addScheduleApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteScheduleApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  classes.value = (await getClazzListApi()).data || []
courses.value = (await getCourseListApi()).data || []
teachers.value = (await getTeacherListApi()).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
