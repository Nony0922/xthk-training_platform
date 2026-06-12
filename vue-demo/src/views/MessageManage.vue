<template>
  <div class="manage-page">
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增留言</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>家长</th>
            <th>留言内容</th>
            <th>回复</th>
            <th>状态</th>
            <th>留言时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.parentName ?? '-' }}</td>
            <td>{{ item.content ?? '-' }}</td>
            <td>{{ item.reply ?? '-' }}</td>
            <td>{{ formatCell(item.status, 'msgStatus') }}</td>
            <td>{{ item.createTime ?? '-' }}</td>
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
          <h3>{{ isEdit ? '编辑' : '新增' }}留言</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>家长</label>
            <select v-model="form.parentId"><option :value="null">请选择</option><option v-for="p in parents" :key="p.id" :value="p.id">{{ p.name }}</option></select>
          </div>
          <div class="form-item">
            <label>留言内容 *</label>
            <textarea v-model="form.content" placeholder="请输入留言内容" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label>回复</label>
            <textarea v-model="form.reply" placeholder="请输入回复" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
            <option :value="0">待回复</option>
            <option :value="1">已回复</option>
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
import { getMessageListApi, addMessageApi, updateMessageApi, deleteMessageApi } from '@/api/message'
import { getParentListApi } from '@/api/parent'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const parents = ref([])

const form = reactive({
  id: null,
  parentId: null,
  content: '',
  reply: '',
  status: 0
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
  Object.assign(form, { id: null, parentId: null,
  content: '',
  reply: '',
  status: 0 })
}

const loadList = async () => {
  try {
    const res = await getMessageListApi()
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
  if (!form.content) { alert('请填写留言内容'); return }
  try {
    loading.value = true
    if (isEdit.value) await updateMessageApi(form)
    else await addMessageApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteMessageApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  parents.value = (await getParentListApi()).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
