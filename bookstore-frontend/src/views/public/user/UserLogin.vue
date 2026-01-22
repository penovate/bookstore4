<template>
  <v-container class="fill-height d-flex justify-center align-center py-12" fluid>
    <v-card width="100%" max-width="460" class="mx-auto pa-10 text-center login-card-box">
      <v-card-item class="mb-6">
        <v-icon icon="mdi-book-open-variant" size="48" color="primary" class="mb-2"></v-icon>
        <v-card-title class="text-h4 font-weight-bold text-primary">會員登入</v-card-title>
      </v-card-item>

      <v-form @submit.prevent="handleLogin" class="mt-4">
        <v-text-field
          v-model="loginForm.email"
          label="電子信箱"
          prepend-inner-icon="mdi-email-outline"
          variant="outlined"
          color="primary"
          class="mb-4 rounded-lg"
          density="comfortable"
        ></v-text-field>

        <v-text-field
          v-model="loginForm.password"
          label="密碼"
          prepend-inner-icon="mdi-lock-outline"
          type="password"
          variant="outlined"
          color="primary"
          class="mb-6 rounded-lg"
          density="comfortable"
        ></v-text-field>

        <v-btn
          type="submit"
          block
          height="54"
          color="primary"
          class="text-h6 font-weight-bold rounded-lg elevation-1 mt-2"
        >
          立即登入
        </v-btn>
      </v-form>

      <v-divider class="my-10"></v-divider>
      <div class="text-caption text-grey-darken-1 mb-4 font-weight-bold">測試帳號快選</div>

      <v-row dense justify="center">
        <v-col v-for="type in ['SUPER_ADMIN', 'ADMIN', 'USER', 'BANNED']" :key="type" cols="3">
          <v-btn
            variant="tonal"
            icon
            color="primary"
            @click="quickLogin(type)"
            class="quick-btn rounded-circle"
          >
            <v-icon :icon="getIcon(type)" size="20"></v-icon>
            <v-tooltip activator="parent" location="bottom">{{ getLabel(type) }}</v-tooltip>
          </v-btn>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const loginForm = reactive({ email: '', password: '' })

const getIcon = (type) => {
  const icons = {
    SUPER_ADMIN: 'mdi-shield-check',
    ADMIN: 'mdi-account-cog',
    USER: 'mdi-account',
    BANNED: 'mdi-account-off',
  }
  return icons[type]
}

const getLabel = (type) => {
  const labels = { SUPER_ADMIN: '超管', ADMIN: '管理', USER: '會員', BANNED: '停權' }
  return labels[type]
}

const quickLogin = (type) => {
  const accounts = {
    SUPER_ADMIN: { email: 'pen@bookstore.com', pass: '12345' },
    ADMIN: { email: 'alice.lee@mail.com', pass: '123456' },
    USER: { email: 'vip.reader@test.com', pass: '1234' },
    BANNED: { email: 'super@bookstore.com', pass: '123' },
  }
  loginForm.email = accounts[type].email
  loginForm.password = accounts[type].pass
}

const handleLogin = async () => {
  if (!loginForm.email || !loginForm.password) return

  try {
    const response = await axios.post('http://localhost:8080/api/login', {
      email: loginForm.email,
      password: loginForm.password,
    })

    if (response.data.success) {
      userStore.login({
        token: response.data.token,
        role: response.data.role,
        userName: response.data.userName,
        userId: response.data.userId,
      })

      Swal.fire({
        icon: 'success',
        title: '登入成功',
        text: `歡迎回來，${response.data.userName}`,
        confirmButtonColor: '#2E5C43',
        timer: 1500,
      }).then(() => {
        const redirectPath = router.currentRoute.value.query.redirect || '/dev/user/home'
        router.push(redirectPath)
      })
    } else {
      Swal.fire({ icon: 'error', title: '帳號停權', text: response.data.message })
    }
  } catch (error) {
    Swal.fire({ icon: 'error', title: '連線失敗', text: '請檢查網路連線' })
  }
}
</script>

<style scoped>
:deep(.v-application) {
  background-color: #f2f2e9 !important;
}

/* 💡 自定義登入卡片樣式 */
.login-card-box {
  background-color: #ffffff !important;
  border: 1.5px solid #2e5c43 !important;
  border-radius: 24px !important;
  box-shadow: 0 8px 30px rgba(46, 92, 67, 0.08) !important;
}

.text-primary {
  color: #2e5c43 !important;
}

.quick-btn {
  background-color: rgba(46, 92, 67, 0.05) !important;
  transition: all 0.3s ease;
}

.quick-btn:hover {
  background-color: #2e5c43 !important;
  color: white !important;
  transform: translateY(-2px);
}

:deep(.v-field--variant-outlined) {
  --v-field-border-opacity: 0.15;
}

:deep(.v-field--focused) {
  --v-field-border-opacity: 1;
}
</style>
