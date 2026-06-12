<template>
  <div class="manage-page">
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增课程购买</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>订单号</th>
            <th>家长</th>
            <th>课程名称</th>
            <th>任课教师</th>
            <th>学时</th>
            <th>费用</th>
            <th>状态</th>
            <th>支付时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.orderNo ?? '-' }}</td>
            <td>{{ item.parentName ?? '-' }}</td>
            <td>{{ item.courseName ?? '-' }}</td>
            <td>{{ item.teacherName ?? '-' }}</td>
            <td>{{ item.hours ?? '-' }}</td>
            <td>{{ item.fee ?? '-' }}</td>
            <td>{{ formatCell(item.status, 'orderStatus') }}</td>
            <td>{{ item.payTime ?? '-' }}</td>
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
          <h3>{{ isEdit ? '编辑' : '新增' }}课程购买</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>订单号 *</label>
            <input v-model="form.orderNo" type="text" placeholder="请输入订单号" />
          </div>
          <div class="form-item">
            <label>家长</label>
            <select v-model="form.parentId"><option :value="null">请选择</option><option v-for="p in parents" :key="p.id" :value="p.id">{{ p.name }}</option></select>
          </div>
          <div class="form-item">
            <label>课程</label>
            <select v-model="form.courseId" @change="onCourseChange"><option :value="null">请选择</option><option v-for="c in courses" :key="c.id" :value="c.id">{{ c.name }}</option></select>
          </div>
          <div class="form-item">
            <label>课程名称</label>
            <input v-model="form.courseName" type="text" placeholder="请输入课程名称" />
          </div>
          <div class="form-item">
            <label>任课教师</label>
            <input v-model="form.teacherName" type="text" placeholder="请输入任课教师" />
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
            <option :value="0">待支付</option>
            <option :value="1">已支付</option>
            <option :value="2">已取消</option>
          </select>
          </div>
          <div class="form-item">
            <label>支付时间</label>
            <input v-model="form.payTime" type="datetime-local" placeholder="请输入支付时间" />
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
import { getCourseOrderListApi, addCourseOrderApi, updateCourseOrderApi, deleteCourseOrderApi } from '@/api/courseOrder'
import { getParentListApi } from '@/api/parent'
import { getCourseListApi } from '@/api/course'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const parents = ref([])
const courses = ref([])

const form = reactive({
  id: null,
  orderNo: '',
  parentId: null,
  courseId: null,
  courseName: '',
  teacherName: '',
  hours: '',
  fee: '',
  status: 0,
  payTime: ''
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

const onCourseChange = () => {
  const c = courses.value.find(x => x.id == form.courseId)
  if (c) {
    form.courseName = c.name
    form.teacherName = c.teacherName
    form.hours = c.hours
    form.fee = c.fee
  }
}

const resetForm = () => {
  Object.assign(form, { id: null, orderNo: '',
  parentId: null,
  courseId: null,
  courseName: '',
  teacherName: '',
  hours: '',
  fee: '',
  status: 0,
  payTime: '' })
}

const loadList = async () => {
  try {
    const res = await getCourseOrderListApi()
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
  if (!form.orderNo) { alert('请填写订单号'); return }
  try {
    loading.value = true
    if (isEdit.value) await updateCourseOrderApi(form)
    else await addCourseOrderApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteCourseOrderApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  parents.value = (await getParentListApi()).data || []
courses.value = (await getCourseListApi()).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
