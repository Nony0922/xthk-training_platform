<template>
  <div class="manage-page">
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增家访记录</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>学生</th>
            <th>教师</th>
            <th>家访日期</th>
            <th>方式</th>
            <th>内容</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.studentName ?? '-' }}</td>
            <td>{{ item.teacherName ?? '-' }}</td>
            <td>{{ item.visitDate ?? '-' }}</td>
            <td>{{ formatCell(item.visitType, 'visitType') }}</td>
            <td>{{ item.content ?? '-' }}</td>
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
          <h3>{{ isEdit ? '编辑' : '新增' }}家访记录</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>学生</label>
            <select v-model="form.studentId"><option :value="null">请选择</option><option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}{{ s.className ? `（${s.className}）` : '' }}</option></select>
          </div>
          <div v-if="!isTeacher" class="form-item">
            <label>教师</label>
            <select v-model="form.teacherId"><option :value="null">请选择</option><option v-for="t in teachers" :key="t.id" :value="t.id">{{ t.name }}</option></select>
          </div>
          <div class="form-item">
            <label>家访日期</label>
            <input v-model="form.visitDate" type="date" placeholder="请输入家访日期" />
          </div>
          <div class="form-item">
            <label>方式</label>
            <select v-model="form.visitType">
            <option :value="1">上门</option>
            <option :value="2">电话</option>
            <option :value="3">线上</option>
          </select>
          </div>
          <div class="form-item">
            <label>内容</label>
            <textarea v-model="form.content" placeholder="请输入内容" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label>反馈</label>
            <textarea v-model="form.feedback" placeholder="请输入反馈" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label>下一步计划</label>
            <input v-model="form.nextPlan" type="text" placeholder="请输入下一步计划" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { getHomeVisitListApi, addHomeVisitApi, updateHomeVisitApi, deleteHomeVisitApi } from '@/api/homeVisit'
import { getStudentListApi } from '@/api/student'
import { getTeacherListApi } from '@/api/teacher'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const students = ref([])
const teachers = ref([])

const isTeacher = computed(() => {
  try {
    const user = JSON.parse(localStorage.getItem('loginUser'))
    return user?.role === 'teacher'
  } catch {
    return false
  }
})

const form = reactive({
  id: null,
  studentId: null,
  teacherId: null,
  visitDate: '',
  visitType: 1,
  content: '',
  feedback: '',
  nextPlan: ''
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
  Object.assign(form, { id: null, studentId: null,
  teacherId: null,
  visitDate: '',
  visitType: 1,
  content: '',
  feedback: '',
  nextPlan: '' })
}

const loadList = async () => {
  try {
    const res = await getHomeVisitListApi()
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
  if (!form.studentId) {
    alert('请选择学生')
    return
  }
  if (!isTeacher.value && !form.teacherId) {
    alert('请选择教师')
    return
  }
  if (!form.visitDate) {
    alert('请选择家访日期')
    return
  }

  try {
    loading.value = true
    if (isEdit.value) await updateHomeVisitApi(form)
    else await addHomeVisitApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteHomeVisitApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  students.value = (await getStudentListApi('homeroom')).data || []
  if (!isTeacher.value) {
    teachers.value = (await getTeacherListApi()).data || []
  }
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
