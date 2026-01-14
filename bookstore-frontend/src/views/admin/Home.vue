<template>
  <div class="dashboard-body">
    <div class="container dashboard-container">
      <h1 class="main-title">網路書店後台系統</h1>
      <h2 class="sub-title">功能選單</h2>
      <div class="button-group">
        <button class="menu-button" @click="goTo('/users')">會員中心</button>
        <button class="menu-button" @click="goTo('/dev/admin/books')">書籍資料處理</button>
        <button class="menu-button" @click="goTo('/orders')">訂單與購物車系統</button>
        <button class="menu-button" @click="goTo('/reviews')">評價管理</button>
      </div>
      <hr class="divider" />
      <div>
        <button class="logout-button" @click="handleLogout">登出系統</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()

const goTo = (path) => {
  router.push(path)
}

const handleLogout = async () => {
  Swal.fire({
    title: '確定要登出嗎？',
    text: '登出後將返回登入介面',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#b0957b',
    cancelButtonColor: '#e8e4dc',
    confirmButtonText: '登出',
    cancelButtonText: '取消',
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        await axios.get('http://localhost:8080/api/logout', { withCredentials: true })

        router.push('/login?logout=true')
      } catch (error) {
        console.error('登出失敗', error)
        router.push('/login?logout=true')
      }
    }
  })
}
</script>

<style scoped>
.dashboard-body {
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

.dashboard-container {
  width: 600px; /* 這裡確保容器寬度夠大 */
  max-width: 90%;
  padding: 35px 45px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
  box-sizing: border-box;
}

.main-title {
  color: #7b5e47;
  font-size: 26px;
  margin-bottom: 5px;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 15px;
  letter-spacing: 1px;
}

.sub-title {
  color: #9c8470;
  font-size: 19px;
  margin-top: 0;
  margin-bottom: 30px;
  font-weight: normal;
}

.button-group {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 15px;
  margin-top: 20px;
}

.menu-button {
  flex: 0 0 calc(50% - 8px);
  height: 90px;
  padding: 10px 15px;
  background-color: #b0957b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 1px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.menu-button:hover {
  background-color: #a5886d;
  transform: translateY(-3px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
}

.logout-button {
  height: 45px;
  padding: 0 30px;
  background-color: #e8e4dc;
  color: #6d6d6d;
  border: 1px solid #dcd5c7;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}

.divider {
  border: 0;
  height: 1px;
  background-color: #e0d9c9;
  margin-top: 35px;
}

.logout-button {
  height: 45px;
  padding: 0 30px;
  background-color: #e8e4dc;
  color: #6d6d6d;
  border: 1px solid #dcd5c7;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: normal;
  letter-spacing: 1px;
  transition: all 0.2s ease-in-out;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.logout-button:hover {
  background-color: #dcd5c7;
  color: #4a4a4a;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.logout-button:active {
  background-color: #cec7b9;
  transform: translateY(0);
}
</style>
