<template>
  <v-app>
    <v-main class="login-bg">
      <v-container class="fill-height d-flex justify-center align-center" fluid>
        <v-hover v-slot="{ isHovering, props }">
          <v-card
            v-bind="props"
            width="100%"
            max-width="420"
            :elevation="isHovering ? 12 : 4"
            class="pa-8 rounded-lg transition-swing"
          >
            <v-card-item class="text-center">
              <v-icon
                icon="mdi-library-shelves"
                size="x-large"
                color="brown-darken-1"
                class="mb-4"
              ></v-icon>
              <v-card-title class="text-h5 font-weight-bold text-brown-darken-3">
                網路書店後台系統
              </v-card-title>
              <v-card-subtitle class="text-subtitle-1 mt-1">管理員登入</v-card-subtitle>
            </v-card-item>

            <v-divider class="my-6"></v-divider>

            <v-form @submit.prevent="handleLogin">
              <v-text-field
                v-model="loginForm.email"
                label="帳號 (Email)"
                prepend-inner-icon="mdi-email-outline"
                variant="outlined"
                color="brown"
                class="mb-3"
                :rules="[(v) => !!v || '請輸入信箱']"
              ></v-text-field>

              <v-text-field
                v-model="loginForm.password"
                label="密碼"
                prepend-inner-icon="mdi-lock-outline"
                type="password"
                variant="outlined"
                color="brown"
                class="mb-6"
                :rules="[(v) => !!v || '請輸入密碼']"
              ></v-text-field>

              <v-btn
                type="submit"
                block
                height="50"
                color="brown-darken-1"
                class="text-h6 font-weight-bold elevation-2"
                rounded="md"
              >
                立即登入
              </v-btn>
            </v-form>

            <v-divider class="my-8"></v-divider>

            <div class="text-center text-caption text-grey">© 2026 網路書店系統管理後台</div>
          </v-card>
        </v-hover>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const router = useRouter()
const loginForm = reactive({
  email: '',
  password: '',
})

const handleLogin = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/login', {
      email: loginForm.email,
      password: loginForm.password,
    })

    if (response.data.success) {
      localStorage.setItem('userToken', response.data.token)
      localStorage.setItem('userRole', response.data.role)
      localStorage.setItem('userName', response.data.userName || '')
      localStorage.setItem('userId', response.data.userId)

      Swal.fire({
        icon: 'success',
        title: '登入成功',
        text: '歡迎進入系統',
        confirmButtonColor: '#7B5E47',
        timer: 1500,
        timerProgressBar: true,
      }).then(() => {
        router.push('/home')
      })
    } else {
      // 💡 這裡會正確接收到你後端寫的「權限不足」、「停權」等資訊
      Swal.fire({
        icon: 'error',
        title: '登入失敗',
        text: response.data.message,
        confirmButtonColor: '#B05252',
      })
    }
  } catch (error) {
    Swal.fire({
      icon: 'error',
      title: '連線失敗',
      text: '請檢查伺服器連線狀態',
      confirmButtonColor: '#B05252',
    })
  }
}

onMounted(() => {
  const token = localStorage.getItem('userToken')
  const role = localStorage.getItem('userRole')

  // 若已登入則直接跳轉
  if (token && (role === 'SUPER_ADMIN' || role === 'ADMIN')) {
    router.push('/home')
    return
  }

  // 處理登入頁面的登出訊息
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.has('logout')) {
    localStorage.clear() // 清空所有登入資訊
    Swal.fire({
      icon: 'info',
      title: '您已登出',
      confirmButtonColor: '#7B5E47',
    })
    window.history.replaceState({}, document.title, window.location.pathname)
  }
})
</script>

<style scoped>
.login-bg {
  /* 使用溫暖的紙張/木質調漸層 */
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}

.text-brown-darken-3 {
  color: #3e2723 !important;
  letter-spacing: 2px;
}

/* 微調卡片過渡動畫 */
.transition-swing {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}

.v-btn {
  letter-spacing: 2px;
}
</style>
