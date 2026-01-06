<template>
  <div class="center-body">
    <div class="detail-container">
      <h2>會員詳細資料</h2>
      <table v-if="user">
        <tr>
          <td>會員編號 (ID)</td>
          <td><input type="text" disabled :value="user.userId" /></td>
        </tr>
        <tr>
          <td>Email 帳號</td>
          <td><input type="text" disabled :value="user.email" /></td>
        </tr>
        <tr>
          <td>會員姓名</td>
          <td><input type="text" disabled :value="user.userName" /></td>
        </tr>
        <tr>
          <td>性別</td>
          <td><input type="text" disabled :value="formatGender(user.gender)" /></td>
        </tr>
        <tr>
          <td>生日</td>
          <td><input type="text" disabled :value="formatDate(user.birth)" /></td>
        </tr>
        <tr>
          <td>聯絡電話</td>
          <td><input type="text" disabled :value="user.phoneNum" /></td>
        </tr>
        <tr>
          <td>地址</td>
          <td><input type="text" disabled :value="user.address" /></td>
        </tr>
        <tr>
          <td>權限等級</td>
          <td>
            <input type="text" disabled :value="user.userType === 0 ? '管理員' : '一般會員'" />
          </td>
        </tr>
      </table>
      <div v-else style="text-align: center; padding: 20px">載入中...</div>

      <div class="button-area">
        <button @click="router.push('/users/list')">回到所有會員資料</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const user = ref(null)

const formatGender = (code) => {
  if (code === 'M') return '男'
  if (code === 'F') return '女'
  return code || ''
}

const formatDate = (dateValue) => {
  if (!dateValue) return ''
  const date = new Date(dateValue)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const fetchUserDetail = async () => {
  try {
    const userId = route.params.id
    const url = `http://localhost:8080/api/data/get/${userId}`
    console.log('正在請求網址：', url)

    const response = await axios.get(url, {
      withCredentials: true,
    })
    console.log('獲取的資料：', response.data)
    user.value = response.data
  } catch (error) {
    console.error('取得詳細資料失敗', error.response || error)
    alert('無法取得會員資料，請檢查後端 Console')
  }
}

onMounted(fetchUserDetail)
</script>

<style scoped>
/* 這裡請保留剛才幫你寫的 JSP 移植樣式 */
.center-body {
  font-family: '微軟正黑體';
  background-color: #fcf8f0;
  display: flex;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 0;
}
.detail-container {
  width: 90%;
  max-width: 550px;
  padding: 35px 45px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
}
h2 {
  color: #7b5e47;
  text-align: center;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 10px;
  margin-top: 0;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}
tr {
  border-bottom: 1px dashed #e0d9c9;
}
td {
  padding: 12px 0;
  font-size: 15px;
}
td:first-child {
  width: 35%;
  text-align: right;
  padding-right: 20px;
  color: #5d5d5d;
  font-weight: 500;
}
input[type='text'][disabled] {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dcd5c7;
  border-radius: 4px;
  background-color: #f7f3e8;
  color: #4a4a4a;
  box-sizing: border-box;
}
.button-area {
  text-align: center;
}
button {
  height: 40px;
  padding: 10px 20px;
  background-color: #e8e4dc;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
button:hover {
  background-color: #dcd5c7;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}
</style>
