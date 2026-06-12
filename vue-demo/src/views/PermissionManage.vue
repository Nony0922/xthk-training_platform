<template>
  <div class="manage-page">
    <div class="page-desc">
      <p>管理系统用户账号及角色权限，包括管理员、教师（任课教师 / 班主任）等角色的分配与维护。</p>
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
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserListApi } from '@/api/user'

const list = ref([])

const formatRole = (role) => {
  const map = { admin: '管理员', teacher: '教师', parent: '家长' }
  return map[role] || role
}

const formatTeacherLevel = (item) => {
  if (item.role !== 'teacher') return '-'
  return item.teacherLevel === 2 ? '班主任' : '任课教师'
}

const loadList = async () => {
  try {
    const res = await getUserListApi()
    list.value = res.data || []
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
