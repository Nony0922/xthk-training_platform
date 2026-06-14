<template>
  <div class="manage-page">
    <PageSkeleton v-if="pageLoading" variant="grouped" />
    <template v-else>
    <div class="toolbar">
      <button class="btn btn-primary" @click="handleAdd">新增成绩</button>
    </div>
    <div v-if="!groups.length" class="empty-tip">暂无成绩数据</div>

    <div v-for="course in groups" :key="course.courseId" class="data-group">
      <div class="group-header">
        <span class="group-title">{{ course.courseName }}</span>
        <span class="group-meta">{{ countScoreRows(course) }} 条成绩</span>
      </div>
      <div v-for="group in course.exams" :key="group.examId" class="subgroup">
        <div class="subgroup-header exam-subgroup-header">
          <div class="exam-group-left">
            <span class="subgroup-title">{{ group.examName }}</span>
            <span class="exam-group-sub">{{ group.className }} · {{ group.examDate }}</span>
          </div>
          <span class="subgroup-meta">{{ group.rows.length }} 名学生</span>
        </div>
        <div class="table-container">
          <table class="data-table group-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>学生</th>
                <th>分数</th>
                <th>备注</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in group.rows" :key="item.id">
                <td>{{ item.rankNum ?? '-' }}</td>
                <td>{{ item.studentName ?? '-' }}</td>
                <td>{{ item.score ?? '-' }}</td>
                <td>{{ item.remark ?? '-' }}</td>
                <td class="actions">
                  <button class="btn btn-sm btn-info" @click="handleEdit(item)">编辑</button>
                  <button class="btn btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    </template>
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑' : '新增' }}成绩</h3>
          <button class="close-btn" @click="dialogVisible = false">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-item">
            <label>考试</label>
            <select v-model="form.examId"><option :value="null">请选择</option><option v-for="e in exams" :key="e.id" :value="e.id">{{ e.name }}</option></select>
          </div>
          <div class="form-item">
            <label>学生</label>
            <select v-model="form.studentId"><option :value="null">请选择</option><option v-for="s in students" :key="s.id" :value="s.id">{{ s.name }}</option></select>
          </div>
          <div class="form-item">
            <label>分数</label>
            <input v-model="form.score" type="number" placeholder="请输入分数" />
          </div>
          <div class="form-item">
            <label>排名</label>
            <input v-model="form.rankNum" type="number" placeholder="请输入排名" />
          </div>
          <div class="form-item">
            <label>备注</label>
            <input v-model="form.remark" type="text" placeholder="请输入备注" />
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
import { getScoreListApi, addScoreApi, updateScoreApi, deleteScoreApi } from '@/api/score'
import { getExamListApi } from '@/api/exam'
import { getStudentListApi } from '@/api/student'
import { getScopeModeFromRoute } from '@/composables/useTeacherScope'
import { groupScoresByCourseExam } from '@/utils/groupTeachingData'
import PageSkeleton from '@/components/PageSkeleton.vue'
import { usePageLoading } from '@/composables/usePageLoading'

const route = useRoute()
const scopeMode = () => getScopeModeFromRoute(route)
const { pageLoading, withLoading } = usePageLoading()

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const exams = ref([])
const students = ref([])

const groups = computed(() => groupScoresByCourseExam(list.value, exams.value))

const countScoreRows = (course) =>
  course.exams.reduce((sum, exam) => sum + exam.rows.length, 0)

const form = reactive({
  id: null,
  examId: null,
  studentId: null,
  score: '',
  rankNum: '',
  remark: ''
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
  Object.assign(form, { id: null, examId: null,
  studentId: null,
  score: '',
  rankNum: '',
  remark: '' })
}

const loadList = () => withLoading(async () => {
  const res = await getScoreListApi(scopeMode())
  list.value = res.data || []
})

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
    if (isEdit.value) await updateScoreApi(form)
    else await addScoreApi(form)
    alert('操作成功')
    dialogVisible.value = false
    loadList()
  } catch (e) { alert(e.message) }
  finally { loading.value = false }
}

const handleDelete = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    await deleteScoreApi(id)
    alert('删除成功')
    loadList()
  } catch (e) { alert(e.message) }
}

onMounted(async () => {
  loadList()
  exams.value = (await getExamListApi(scopeMode())).data || []
students.value = (await getStudentListApi(scopeMode())).data || []
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
