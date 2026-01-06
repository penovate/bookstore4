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

// 初始化變數與路由工具
const errorMessage = ref('')
const router = useRouter()

const loginForm = reactive({
  email: '',
  password: '',
})

// 處理登入邏輯
const handleLogin = async () => {
  errorMessage.value = '' // 每次按下登入先清空舊的錯誤訊息
  console.log('準備登入，資料為：', loginForm)

  try {
    // 發送 POST 請求到 Spring Boot API
    const response = await axios.post(
      'http://localhost:8080/api/login',
      {
        email: loginForm.email,
        password: loginForm.password,
      },
      {
        withCredentials: true, // 允許跨域攜帶 Cookie/Session
      },
    )

    console.log('伺服器回傳：', response.data)

    if (response.data.success) {
      // 1. 彈出成功視窗
      alert(response.data.message)

      // 2. 執行路由跳轉，導向到首頁 (功能選單)
      router.push('/home')
    } else {
      // 顯示後端驗證失敗的訊息 (如：帳密錯誤、停權)
      errorMessage.value = response.data.message
    }
  } catch (error) {
    console.error('連線失敗：', error)
    if (error.response) {
      // 後端有回應但狀態碼非 2xx (如 401, 403, 404, 500)
      errorMessage.value = error.response.data.message || '伺服器錯誤'
    } else {
      // 完全連不上伺服器
      errorMessage.value = '連線不到後端伺服器，請檢查 Spring Boot 是否啟動'
    }
  }
}

// 處理網址列是否有 logout 參數 (處理登出後的提示)
onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.has('logout')) {
    alert('您已成功登出系統！')
    // 清除網址列的 ?logout=true，避免重新整理後又跳彈窗
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
