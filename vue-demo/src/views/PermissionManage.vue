<template>
  <div class="manage-page">
    <div class="page-desc">
      <p>管理系统用户账号及角色权限，支持新增、编辑、删除用户，并分配管理员、教师（任课教师 / 班主任）、家长等角色。</p>
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增用户</button>
    </div>
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>姓名</th>
            <th>角色</th>
            <th>教师级别</th>
            <th>电话</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.username }}</td>
            <td>{{ item.name }}</td>
            <td>{{ formatRole(item.role) }}</td>
            <td>{{ formatTeacherLevel(item) }}</td>
            <td>{{ item.phone || '-' }}</td>
            <td>
              <span :class="['badge', item.status === 1 ? 'badge-success' : 'badge-danger']">
                {{ item.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ item.createTime || '-' }}</td>
            <td class="actions">
              <button class="btn btn-sm btn-info" @click="handleEdit(item)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(item)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑' : '新增' }}用户</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>用户名 *</label>
            <input v-model="form.username" type="text" placeholder="登录用户名" :disabled="isEdit" />
          </div>
          <div class="form-item">
            <label>{{ isEdit ? '新密码' : '密码 *' }}</label>
            <input v-model="form.password" type="password" :placeholder="isEdit ? '留空则不修改密码' : '默认 123456'" />
          </div>
          <div class="form-item">
            <label>姓名 *</label>
            <input v-model="form.name" type="text" placeholder="请输入姓名" />
          </div>
          <div class="form-item">
            <label>角色 *</label>
            <select v-model="form.role" @change="onRoleChange">
              <option value="admin">管理员</option>
              <option value="teacher">教师</option>
              <option value="parent">家长</option>
            </select>
          </div>
          <div v-if="form.role === 'teacher'" class="form-item">
            <label>教师级别</label>
            <select v-model="form.teacherLevel">
              <option :value="1">任课教师</option>
              <option :value="2">班主任</option>
            </select>
          </div>
          <div class="form-item">
            <label>电话</label>
            <input v-model="form.phone" type="text" placeholder="请输入电话" />
          </div>
          <div class="form-item">
            <label>邮箱</label>
            <input v-model="form.email" type="text" placeholder="请输入邮箱" />
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="loading">
            {{ loading ? '提交中...' : '确定' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserListApi, addUserApi, updateUserApi, deleteUserApi } from '@/api/user'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const form = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  role: 'teacher',
  teacherLevel: 1,
  phone: '',
  email: '',
  status: 1
})

const getLoginUser = () => {
  try {
    return JSON.parse(localStorage.getItem('loginUser'))
  } catch {
    return null
  }
}

const formatRole = (role) => {
  const map = { admin: '管理员', teacher: '教师', parent: '家长' }
  return map[role] || role
}

const formatTeacherLevel = (item) => {
  if (item.role !== 'teacher') return '-'
  return item.teacherLevel === 2 ? '班主任' : '任课教师'
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    name: '',
    role: 'teacher',
    teacherLevel: 1,
    phone: '',
    email: '',
    status: 1
  })
}

const onRoleChange = () => {
  if (form.role === 'teacher') {
    form.teacherLevel = 1
  } else {
    form.teacherLevel = null
  }
}

const loadList = async () => {
  try {
    const res = await getUserListApi()
    list.value = res.data || []
  } catch (e) {
    alert(e.message)
  }
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (item) => {
  isEdit.value = true
  Object.assign(form, {
    id: item.id,
    username: item.username,
    password: '',
    name: item.name,
    role: item.role,
    teacherLevel: item.role === 'teacher' ? (item.teacherLevel || 1) : null,
    phone: item.phone || '',
    email: item.email || '',
    status: item.status ?? 1
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.username) {
    alert('请填写用户名')
    return
  }
  if (!isEdit.value && !form.password) {
    form.password = '123456'
  }
  if (!form.name) {
    alert('请填写姓名')
    return
  }
  try {
    loading.value = true
    const payload = { ...form }
    if (isEdit.value && !payload.password) {
      delete payload.password
    }
    if (payload.role !== 'teacher') {
      payload.teacherLevel = null
    }
    if (isEdit.value) {
      await updateUserApi(payload)
    } else {
      await addUserApi(payload)
    }
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) {
    alert(e.message)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (item) => {
  const loginUser = getLoginUser()
  if (loginUser && loginUser.id === item.id) {
    alert('不能删除当前登录账号')
    return
  }
  if (!confirm(`确定删除用户「${item.name}（${item.username}）」吗？`)) return
  try {
    await deleteUserApi(item.id)
    alert('删除成功')
    loadList()
  } catch (e) {
    alert(e.message)
  }
}

onMounted(loadList)
</script>

<style scoped>
@import '@/assets/manage.css';

.page-desc {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f5f3ff;
  border-radius: 8px;
  border-left: 4px solid #7c3aed;
}

.page-desc p {
  margin: 0;
  color: #4b5563;
  font-size: 14px;
}
</style>
