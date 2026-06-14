<template>
  <div class="manage-page">
    <div v-if="!readOnly" class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增学生</button>
    </div>
    <div v-if="!groups.length" class="empty-tip">暂无学生数据</div>

    <div v-for="group in groups" :key="group.classId" class="data-group">
      <div class="group-header">
        <span class="group-title">{{ group.className }}</span>
        <span class="group-meta">{{ group.rows.length }} 名学生</span>
      </div>
      <div class="table-container">
        <table class="data-table group-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>姓名</th>
              <th>性别</th>
              <th>家长</th>
              <th>电话</th>
              <th>入学日期</th>
              <th>状态</th>
              <th v-if="!readOnly">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in group.rows" :key="item.id">
              <td>{{ item.id ?? '-' }}</td>
              <td>{{ item.name ?? '-' }}</td>
              <td>{{ formatCell(item.gender, 'gender') }}</td>
              <td>{{ item.parentName ?? '-' }}</td>
              <td>{{ item.phone ?? '-' }}</td>
              <td>{{ item.enrollDate ?? '-' }}</td>
              <td>{{ formatCell(item.status, 'status') }}</td>
              <td v-if="!readOnly" class="actions">
                <button class="btn btn-sm btn-info" @click="handleEdit(item)">编辑</button>
                <button class="btn btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div v-if="!readOnly && dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑' : '新增' }}学生</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>姓名 *</label>
            <input v-model="form.name" type="text" placeholder="请输入姓名" />
          </div>
          <div class="form-item">
            <label>性别</label>
            <select v-model="form.gender">
            <option :value="1">男</option>
            <option :value="2">女</option>
          </select>
          </div>
          <div class="form-item">
            <label>生日</label>
            <input v-model="form.birthday" type="date" placeholder="请输入生日" />
          </div>
          <div class="form-item">
            <label>电话</label>
            <input v-model="form.phone" type="text" placeholder="请输入电话" />
          </div>
          <div class="form-item">
            <label>班级</label>
            <select v-model="form.classId"><option :value="null">请选择</option><option v-for="c in classes" :key="c.id" :value="c.id">{{ c.name }}</option></select>
          </div>
          <div class="form-item">
            <label>家长</label>
            <select v-model="form.parentId"><option :value="null">请选择</option><option v-for="p in parents" :key="p.id" :value="p.id">{{ p.name }}</option></select>
          </div>
          <div class="form-item">
            <label>入学日期</label>
            <input v-model="form.enrollDate" type="date" placeholder="请输入入学日期" />
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
            <option :value="1">在读</option>
            <option :value="0">离校</option>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStudentListApi, addStudentApi, updateStudentApi, deleteStudentApi } from '@/api/student'
import { getClazzListApi } from '@/api/clazz'
import { getParentListApi } from '@/api/parent'
import { useReadOnly } from '@/composables/useReadOnly'
import { getScopeModeFromRoute } from '@/composables/useTeacherScope'
import { groupByClass } from '@/utils/groupTeachingData'

const route = useRoute()
const readOnly = useReadOnly()
const scopeMode = () => getScopeModeFromRoute(route)

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const classes = ref([])
const parents = ref([])

const groups = computed(() => groupByClass(list.value))

const form = reactive({
  id: null,
  name: '',
  gender: 1,
  birthday: '',
  phone: '',
  classId: null,
  parentId: null,
  enrollDate: '',
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
    msgStatus: v => v === 1 ? '已回复' : '待回复',
  }
  return (m[type] ? m[type](v) : v) ?? '-'
}
const onCourseChange = () => {}

const resetForm = () => {
  Object.assign(form, { id: null, name: '',
  gender: 1,
  birthday: '',
  phone: '',
  classId: null,
  parentId: null,
  enrollDate: '',
  status: 1 })
}

const loadList = async () => {
  try {
    const res = await getStudentListApi(scopeMode())
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
  if (!form.name) { alert('请填写姓名'); return }
  try {
    loading.value = true
    if (isEdit.value) await updateStudentApi(form)
    else await addStudentApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteStudentApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  classes.value = (await getClazzListApi()).data || []
parents.value = (await getParentListApi()).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
