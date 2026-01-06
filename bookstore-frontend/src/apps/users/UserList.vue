<template>
  <div class="center-body">
    <div class="list-container">
      <h2>所有會員資料</h2>

      <div v-if="alertMessage" class="custom-alert">
        <span class="alert-text">{{ alertMessage }}</span>
        <button @click="alertMessage = ''" class="close-btn">X</button>
      </div>

      <div class="filter-form">
        <input type="text" v-model="filters.searchName" placeholder="輸入「姓氏」進行查詢" />
        <select v-model="filters.userTypeFilter">
          <option value="">顯示所有使用者</option>
          <option value="0">僅顯示「管理員」</option>
          <option value="1">僅顯示「一般會員」</option>
        </select>
        <button class="submit-btn" @click="fetchUsers">查詢</button>
        <button class="system-button back-to-center-button" @click="resetFilters">取消篩選</button>
      </div>

      <table>
        <thead>
          <tr>
            <th>會員編號</th>
            <th>會員名稱</th>
            <th>密碼</th>
            <th>Email</th>
            <th>性別</th>
            <th>生日</th>
            <th>電話號碼</th>
            <th>地址</th>
            <th>權限等級</th>
            <th>修改資料</th>
            <th>刪除資料</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td>
              <a href="#" @click.prevent="goToDetail(user.userId)">{{ user.userName }}</a>
            </td>
            <td>{{ user.userPwd }}</td>
            <td>{{ user.email }}</td>
            <td>{{ formatGender(user.gender) }}</td>
            <td>{{ formatDate(user.birth) }}</td>
            <td>{{ user.phoneNum }}</td>
            <td>{{ user.address }}</td>
            <td>{{ user.userType === 0 ? '管理員' : '一般會員' }}</td>
            <td>
              <button class="update-button" @click="goToUpdate(user.userId)">修改</button>
            </td>
            <td>
              <button class="delete-button" @click="handleDelete(user)">刪除</button>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="11" style="text-align: center; color: #999">查無會員資料</td>
          </tr>
        </tbody>
      </table>

      <h3>共 {{ users.length }} 筆會員資料</h3>

      <div class="action-footer">
        <button class="system-button add-button" @click="router.push('/users/insert')">
          新增會員資料
        </button>
        <button class="system-button back-to-center-button" @click="router.push('/users')">
          回到會員中心首頁
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const users = ref([])
const alertMessage = ref('')
const alertType = ref('success')

const filters = reactive({
  searchName: '',
  userTypeFilter: '',
})

const checkUrlMessage = () => {
  if (route.query.status === 'success' && route.query.msg) {
    alertType.value = 'success'
    alertMessage.value = route.query.msg
    router.replace({ query: {} })
  }
}

const formatGender = (code) => {
  if (!code) return ''
  const c = code.toString().toUpperCase()
  if (c === 'M') return '男'
  if (c === 'F') return '女'
  return code
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const fetchUsers = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/data/list', {
      params: {
        searchName: filters.searchName || null,
        userTypeFilter: filters.userTypeFilter === '' ? null : filters.userTypeFilter,
      },
      withCredentials: true,
    })
    users.value = response.data
  } catch (error) {
    console.error('抓取資料失敗', error)
  }
}

const resetFilters = () => {
  filters.searchName = ''
  filters.userTypeFilter = ''
  fetchUsers()
}

const handleDelete = async (user) => {
  if (confirm(`確定要刪除會員 「${user.userName}」 的資料嗎？`)) {
    try {
      const response = await axios.delete(`http://localhost:8080/api/users/delete/${user.userId}`, {
        withCredentials: true,
      })

      if (response.data.success) {
        alertMessage.value = response.data.message
        fetchUsers()
      } else {
        alert(response.data.message)
      }
    } catch (error) {
      console.error('刪除請求出錯', error)
      alert('系統錯誤，無法執行刪除。')
    }
  }
}

const goToUpdate = (id) => router.push(`/users/update/${id}`)
const goToDetail = (id) => router.push(`/users/get/${id}`)

onMounted(() => {
  fetchUsers()
  checkUrlMessage()
})
</script>

<style scoped>
/* --- 完全移植 JSP 的 CSS --- */

.center-body {
  font-family: '微軟正黑體', 'Arial', sans-serif;
  background-color: #fcf8f0;
  color: #4a4a4a;
  margin: 0;
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
}

.list-container {
  width: 95%;
  max-width: 1200px;
  padding: 30px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
}

h2 {
  color: #7b5e47;
  font-size: 24px;
  margin-top: 0;
  margin-bottom: 25px;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 10px;
}

h3 {
  color: #9c8470;
  font-weight: normal;
  margin-top: 30px;
  margin-bottom: 20px;
}

/* 查詢表單樣式 */
.filter-form {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

input[type='text'],
select {
  padding: 10px 12px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  background-color: #fefcf9;
  color: #4a4a4a;
  font-size: 15px;
  height: 40px;
  box-sizing: border-box;
  transition:
    border-color 0.3s,
    box-shadow 0.3s;
}

input[type='text']:focus,
select:focus {
  border-color: #9fb89e;
  outline: none;
  box-shadow: 0 0 5px rgba(159, 184, 158, 0.4);
}

/* 查詢按鈕 (原本的 input[type="submit"]) */
.submit-btn {
  height: 40px;
  padding: 10px 20px;
  background-color: #a07d58;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition:
    background-color 0.3s,
    transform 0.1s;
}

.submit-btn:hover {
  background-color: #926f4e;
  transform: translateY(-1px);
}

/* 表格樣式 */
table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  font-size: 15px;
}

th,
td {
  border: 1px solid #e0d9c9;
  padding: 12px 10px;
  text-align: left;
}

th {
  background-color: #e8e4dc;
  color: #5d5d5d;
  font-weight: bold;
  letter-spacing: 0.5px;
}

tr:nth-child(even) {
  background-color: #f7f3f0;
}

tr:hover {
  background-color: #fffaf0;
  transition: background-color 0.3s;
}

td a {
  color: #a07d58;
  text-decoration: none;
  transition: color 0.3s;
}

td a:hover {
  color: #7b5e47;
  text-decoration: underline;
}

/* 按鈕共用樣式 */
button {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition:
    background-color 0.3s,
    transform 0.1s;
  margin: 5px;
}

.update-button {
  background-color: #9fb89e;
  color: #4a4a4a;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.update-button:hover {
  background-color: #8da68c;
  transform: translateY(-1px);
}

.delete-button {
  background-color: #d89696;
  color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.delete-button:hover {
  background-color: #c48383;
  transform: translateY(-1px);
}

.system-button {
  height: 40px;
  font-size: 16px;
  padding: 10px 20px;
  font-weight: bold;
}

.add-button {
  background-color: #a07d58;
  color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.add-button:hover {
  background-color: #926f4e;
  transform: translateY(-2px);
}

.back-to-center-button {
  background-color: #e8e4dc;
  color: #4a4a4a;
  font-weight: normal;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-to-center-button:hover {
  background-color: #dcd5c7;
}

.custom-alert {
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 5px;
  background-color: #f0f7f0;
  border: 1px solid #9fb89e;
  text-align: left;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.alert-text {
  color: #4a4a4a;
  font-weight: bold;
  text-align: center;
}

.close-btn {
  background: none;
  border: 1px solid #ccc;
  color: #4a4a4a;
  padding: 2px 8px;
  margin: 0;
  position: absolute;
  right: 15px;
  cursor: pointer;
}
</style>
