<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-section">
            <div class="logo-icon">🌸</div>
            <div class="logo-text">
              <h1>心田花开</h1>
              <p>培训机构综合管理平台</p>
            </div>
          </div>
        </div>

        <div class="login-form">
          <h2>欢迎登录</h2>
          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label for="username">
                <span class="label-icon">👤</span>
                <span>用户名</span>
              </label>
              <input
                id="username"
                v-model.trim="form.username"
                type="text"
                placeholder="请输入用户名"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label for="password">
                <span class="label-icon">🔑</span>
                <span>密码</span>
              </label>
              <input
                id="password"
                v-model.trim="form.password"
                type="password"
                placeholder="请输入密码"
                class="form-input"
              />
            </div>

            <div class="form-options">
              <label class="checkbox-label">
                <input type="checkbox" v-model="rememberMe" />
                <span>记住我</span>
              </label>
            </div>

            <button type="submit" class="login-btn" :disabled="loading">
              <span v-if="loading" class="loading-spinner"></span>
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </form>

          <p v-if="errorMsg" class="error-message">{{ errorMsg }}</p>
        </div>

        <div class="login-footer">
          <p>© 2026 心田花开培训机构 | PC管理端</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { loginApi } from '@/api/user'
import { getDefaultRoute } from '@/router'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  errorMsg.value = ''
  if (!form.username || !form.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  try {
    loading.value = true
    const res = await loginApi({
      username: form.username,
      password: form.password
    })
    const user = res.data || {}
    if (user.role === 'parent') {
      errorMsg.value = '家长账号请使用小程序端登录'
      return
    }
    localStorage.setItem('loginUser', JSON.stringify(user))
    if (rememberMe.value) {
      localStorage.setItem('rememberUser', form.username)
    } else {
      localStorage.removeItem('rememberUser')
    }
    router.push(getDefaultRoute(user))
  } catch (error) {
    errorMsg.value = error?.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const rememberUser = localStorage.getItem('rememberUser')
  if (rememberUser) {
    form.username = rememberUser
    rememberMe.value = true
  }
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #ede9fe 0%, #fae8ff 50%, #fce7f3 100%);
}

.login-bg {
  position: absolute;
  inset: 0;
  opacity: 0.3;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%237c3aed' fill-opacity='0.08'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.login-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 440px;
  padding: 20px;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(124, 58, 237, 0.15);
  overflow: hidden;
}

.login-header {
  background: linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%);
  padding: 32px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.logo-text h1 {
  margin: 0;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
}

.logo-text p {
  margin: 4px 0 0;
  color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
}

.login-form {
  padding: 32px;
}

.login-form h2 {
  margin: 0 0 28px;
  color: #1f2937;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 48px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  padding: 0 16px;
  font-size: 15px;
  box-sizing: border-box;
  background: #f9fafb;
}

.form-input:focus {
  outline: none;
  border-color: #7c3aed;
  background: #fff;
}

.form-options {
  margin-bottom: 24px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #6b7280;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  height: 52px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  margin: 16px 0 0;
  padding: 12px 16px;
  background: #fee2e2;
  border-radius: 8px;
  color: #dc2626;
  font-size: 14px;
  text-align: center;
}

.login-footer {
  padding: 16px 32px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  text-align: center;
}

.login-footer p {
  margin: 0;
  color: #9ca3af;
  font-size: 13px;
}
</style>
