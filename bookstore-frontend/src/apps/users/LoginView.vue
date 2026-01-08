<template>
  <div class="login-body">
    <div class="container login-container">
      <h1 class="main-title">網路書店後台系統</h1>
      <h2 class="sub-title">管理員登入</h2>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email" class="form-label">帳號（信箱）:</label>
          <input type="email" id="email" v-model="loginForm.email" required class="form-input" />
        </div>
        <div class="form-group">
          <label for="password" class="form-label">密碼:</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            class="form-input"
          />
        </div>
        <div class="form-action">
          <button type="submit" class="submit-button">登入</button>
        </div>
      </form>
      <hr class="divider" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'

const errorMessage = ref('')
const router = useRouter()

const loginForm = reactive({
  email: '',
  password: '',
})

const handleLogin = async () => {
  errorMessage.value = ''

  try {
    const response = await axios.post(
      'http://localhost:8080/api/login',
      {
        email: loginForm.email,
        password: loginForm.password,
      },
      {
        withCredentials: true,
      },
    )

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '登入成功',
        text: '歡迎進入網路書店後台系統',
        confirmButtonColor: '#a5886d',
        confirmButtonText: '進入系統',
        timer: 1500,
        timerProgressBar: true,
      }).then(() => {
        router.push('/home')
      })
    } else {
      errorMessage.value = response.data.message
    }
  } catch (error) {
    console.error('連線失敗：', error)
    Swal.fire({
      icon: 'error',
      title: '連線失敗',
      text: '請檢查 Spring Boot 後端伺服器是否啟動',
      confirmButtonColor: '#b05252',
    })
  }
}

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.has('logout')) {
    Swal.fire({
      icon: 'info',
      title: '您已登出',
      confirmButtonColor: '#a5886d',
      confirmButtonText: '確定',
    })

    window.history.replaceState({}, document.title, window.location.pathname)
  }
})
</script>

<style scoped>
.login-body {
  font-family: '微軟正黑體', 'Arial', sans-serif;
  background-color: #fcf8f0;
  color: #4a4a4a;
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.login-container {
  width: 380px;
  max-width: 90%;
  padding: 35px 45px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.main-title {
  color: #7b5e47;
  font-size: 26px;
  text-align: center;
  margin-bottom: 5px;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 15px;
  letter-spacing: 1px;
}

.sub-title {
  color: #9c8470;
  font-size: 19px;
  text-align: center;
  margin-top: 0;
  margin-bottom: 30px;
  font-weight: normal;
}

.error-message {
  color: #b05252;
  background-color: #ffeaea;
  padding: 12px;
  border: 1px dashed #e7c0c0;
  margin-bottom: 25px;
  text-align: center;
  border-radius: 4px;
  font-size: 14px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  font-weight: 500;
  color: #6d6d6d;
  display: block;
  margin-bottom: 8px;
  font-size: 15px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  box-sizing: border-box;
  background-color: #fefcf9;
  transition:
    border-color 0.3s,
    box-shadow 0.3s;
}

.form-input:focus {
  border-color: #9fb89e;
  outline: none;
  box-shadow: 0 0 8px rgba(159, 184, 158, 0.4);
}

.submit-button {
  width: 100%;
  padding: 14px;
  margin-top: 15px;
  background-color: #a5886d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 17px;
  transition:
    background-color 0.3s,
    transform 0.1s;
  letter-spacing: 1.5px;
}

.submit-button:hover {
  background-color: #92755e;
}

.submit-button:active {
  transform: translateY(1px);
}

.divider {
  border: 0;
  height: 1px;
  background-color: #e0d9c9;
  margin-top: 35px;
}
</style>
