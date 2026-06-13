<template>
  <div class="manage-page">
    <div v-if="!readOnly" class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增课程</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课程名称</th>
            <th>适用年级</th>
            <th>授课方式</th>
            <th>任课教师</th>
            <th>学时</th>
            <th>费用</th>
            <th>状态</th>
            <th v-if="!readOnly">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.name ?? '-' }}</td>
            <td>{{ item.targetGrade ?? '-' }}</td>
            <td>{{ formatCell(item.teachMode, 'teachMode') }}</td>
            <td>{{ item.teacherName ?? '-' }}</td>
            <td>{{ item.hours ?? '-' }}</td>
            <td>{{ item.fee ?? '-' }}</td>
            <td>{{ formatCell(item.status, 'shelf') }}</td>
            <td v-if="!readOnly" class="actions">
              <button class="btn btn-sm btn-info" @click="handleEdit(item)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="!readOnly && dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑' : '新增' }}课程</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>课程名称 *</label>
            <input v-model="form.name" type="text" placeholder="请输入课程名称" />
          </div>
          <div class="form-item">
            <label>课程介绍</label>
            <textarea v-model="form.description" placeholder="请输入课程介绍" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label>任课教师</label>
            <select v-model="form.teacherId"><option :value="null">请选择</option><option v-for="t in teachers" :key="t.id" :value="t.id">{{ t.name }}</option></select>
          </div>
          <div class="form-item">
            <label>适用年级</label>
            <input v-model="form.targetGrade" type="text" placeholder="如：一年级、二年级、全年级" />
          </div>
          <div class="form-item">
            <label>学科</label>
            <input v-model="form.subject" type="text" placeholder="如：语文、数学、英语" />
          </div>
          <div class="form-item">
            <label>授课方式</label>
            <select v-model="form.teachMode">
              <option :value="1">线下授课</option>
              <option :value="2">线上直播</option>
              <option :value="3">线上线下混合</option>
            </select>
          </div>
          <div class="form-item">
            <label>上课地点</label>
            <input v-model="form.location" type="text" placeholder="线下填教室，线上填平台名称" />
          </div>
          <div class="form-item">
            <label>有效开始日期</label>
            <input v-model="form.validStart" type="date" />
          </div>
          <div class="form-item">
            <label>有效结束日期</label>
            <input v-model="form.validEnd" type="date" />
          </div>
          <div class="form-item">
            <label>上课时间说明</label>
            <input v-model="form.classTimeDesc" type="text" placeholder="如：每周一、三 09:00-10:30" />
          </div>
          <div class="form-item">
            <label>适合年龄</label>
            <input v-model="form.suitableAge" type="text" placeholder="如：6-7岁" />
          </div>
          <div class="form-item">
            <label>招生名额（0 表示不限）</label>
            <input v-model="form.maxStudents" type="number" placeholder="请输入招生名额" />
          </div>
          <div class="form-item">
            <label>已报名人数</label>
            <input v-model="form.enrolledCount" type="number" placeholder="请输入已报名人数" />
          </div>
          <div class="form-item">
            <label>课程亮点（| 分隔）</label>
            <input v-model="form.highlights" type="text" placeholder="如：拼音启蒙|阅读训练|写作提升" />
          </div>
          <div class="form-item">
            <label>学时</label>
            <input v-model="form.hours" type="number" placeholder="请输入学时" />
          </div>
          <div class="form-item">
            <label>费用</label>
            <input v-model="form.fee" type="number" placeholder="请输入费用" />
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </select>
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
import { getCourseListApi, addCourseApi, updateCourseApi, deleteCourseApi } from '@/api/course'
import { getTeacherListApi } from '@/api/teacher'
import { useReadOnly } from '@/composables/useReadOnly'

const readOnly = useReadOnly()

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const teachers = ref([])

const form = reactive({
  id: null,
  name: '',
  description: '',
  teacherId: null,
  targetGrade: '',
  subject: '',
  teachMode: 1,
  location: '',
  validStart: '',
  validEnd: '',
  classTimeDesc: '',
  maxStudents: 0,
  enrolledCount: 0,
  suitableAge: '',
  highlights: '',
  hours: 0,
  fee: 0,
  status: 1
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
    teachMode: v => ['','线下授课','线上直播','线上线下混合'][v] || '-',
    msgStatus: v => v === 1 ? '已回复' : '待回复',
  }
  return (m[type] ? m[type](v) : v) ?? '-'
}
const onCourseChange = () => {}

const resetForm = () => {
  Object.assign(form, { id: null, name: '',
  description: '',
  teacherId: null,
  targetGrade: '',
  subject: '',
  teachMode: 1,
  location: '',
  validStart: '',
  validEnd: '',
  classTimeDesc: '',
  maxStudents: 0,
  enrolledCount: 0,
  suitableAge: '',
  highlights: '',
  hours: 0,
  fee: 0,
  status: 1 })
}

const loadList = async () => {
  try {
    const res = await getCourseListApi()
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
  if (!form.name) { alert('请填写课程名称'); return }
  try {
    loading.value = true
    if (isEdit.value) await updateCourseApi(form)
    else await addCourseApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteCourseApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  teachers.value = (await getTeacherListApi()).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
