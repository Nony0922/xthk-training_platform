<template>
  <div class="manage-page">
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>学生</th>
            <th>申请人</th>
            <th>类型</th>
            <th>开始</th>
            <th>结束</th>
            <th>状态</th>
            <th>原因</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id ?? '-' }}</td>
            <td>{{ item.studentName ?? '-' }}</td>
            <td>{{ item.applicantName ?? '-' }}</td>
            <td>{{ formatCell(item.leaveType, 'leaveType') }}</td>
            <td>{{ item.startDate ?? '-' }}</td>
            <td>{{ item.endDate ?? '-' }}</td>
            <td>{{ formatCell(item.status, 'leaveStatus') }}</td>
            <td>{{ item.reason ?? '-' }}</td>
            <td class="actions">
              <button v-if="item.status === 0" class="btn btn-sm btn-info" @click="handleApprove(item)">审批</button>
              <button v-else class="btn btn-sm btn-info" @click="handleView(item)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isApprove ? '审批请假' : '请假详情' }}</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>学生</label>
            <input :value="form.studentName" type="text" readonly />
          </div>
          <div class="form-item">
            <label>申请人</label>
            <input :value="form.applicantName" type="text" readonly />
          </div>
          <div class="form-item">
            <label>类型</label>
            <input :value="formatCell(form.leaveType, 'leaveType')" type="text" readonly />
          </div>
          <div class="form-item">
            <label>开始日期</label>
            <input :value="form.startDate" type="text" readonly />
          </div>
          <div class="form-item">
            <label>结束日期</label>
            <input :value="form.endDate" type="text" readonly />
          </div>
          <div class="form-item">
            <label>原因</label>
            <textarea :value="form.reason" readonly rows="3"></textarea>
          </div>
          <div v-if="isApprove" class="form-item">
            <label>审批结果</label>
            <select v-model="form.status">
              <option :value="1">通过</option>
              <option :value="2">驳回</option>
            </select>
          </div>
          <div v-else class="form-item">
            <label>状态</label>
            <input :value="formatCell(form.status, 'leaveStatus')" type="text" readonly />
          </div>
          <div class="form-item">
            <label>审批备注</label>
            <textarea v-if="isApprove" v-model="form.remark" placeholder="请输入审批备注" rows="2"></textarea>
            <input v-else :value="form.remark || '-'" type="text" readonly />
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="dialogVisible = false">{{ isApprove ? '取消' : '关闭' }}</button>
          <button v-if="isApprove" class="btn btn-primary" @click="handleSubmit" :disabled="loading">{{ loading ? '提交中...' : '确定' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLeaveListApi, updateLeaveApi } from '@/api/leave'

const list = ref([])
const dialogVisible = ref(false)
const isApprove = ref(false)
const loading = ref(false)

const form = reactive({
  id: null,
  studentName: '',
  applicantName: '',
  leaveType: 1,
  startDate: '',
  endDate: '',
  reason: '',
  status: 1,
  remark: ''
})

const formatCell = (v, type) => {
  const m = {
    leaveType: v => ['','事假','病假','其他'][v] || '-',
    leaveStatus: v => ['待审批','已通过','已驳回','已撤回'][v] || '-',
  }
  return (m[type] ? m[type](v) : v) ?? '-'
}

const getUser = () => {
  try {
    return JSON.parse(localStorage.getItem('loginUser'))
  } catch {
    return null
  }
}

const loadList = async () => {
  try {
    const res = await getLeaveListApi()
    list.value = res.data || []
  } catch (e) { alert(e.message) }
}

const handleApprove = (item) => {
  isApprove.value = true
  Object.assign(form, { ...item, status: 1, remark: '' })
  dialogVisible.value = true
}

const handleView = (item) => {
  isApprove.value = false
  Object.assign(form, { ...item })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    loading.value = true
    const user = getUser()
    const now = new Date()
    const approveTime = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
    await updateLeaveApi({
      ...form,
      approverId: user?.id,
      approveTime
    })
    alert('审批成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

onMounted(loadList)
</script>

<style scoped>
@import '@/assets/manage.css';
input[readonly], textarea[readonly] {
  background: #f9fafb;
  color: #6b7280;
}
</style>
