<template>
  <div class="center-body">
    <div class="form-container">
      <h1>會員資料修改</h1>

      <div v-if="message" class="message-box error-style">
        <strong>{{ message }}</strong>
      </div>

      <form @submit.prevent="handleUpdate" v-if="formData">
        <table>
          <tr>
            <td><label>會員編號 (ID):</label></td>
            <td><input type="text" disabled :value="formData.userId" /></td>
          </tr>
          <tr>
            <td><label for="email">帳號 (Email):</label></td>
            <td><input type="email" id="email" v-model="formData.email" required /></td>
          </tr>
          <tr>
            <td><label for="password">密碼:</label></td>
            <td><input type="password" id="password" v-model="formData.userPwd" /></td>
          </tr>
          <tr>
            <td><label for="name">姓名:</label></td>
            <td><input type="text" id="name" v-model="formData.userName" required /></td>
          </tr>
          <tr>
            <td><label for="gender">性別:</label></td>
            <td>
              <select id="gender" v-model="formData.gender">
                <option value="">請選擇</option>
                <option value="M">男</option>
                <option value="F">女</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><label for="birth">生日:</label></td>
            <td><input type="date" id="birth" v-model="formData.birth" /></td>
          </tr>
          <tr>
            <td><label for="phone">聯絡電話:</label></td>
            <td><input type="text" id="phone" v-model="formData.phoneNum" /></td>
          </tr>
          <tr>
            <td><label for="address">地址:</label></td>
            <td><input type="text" id="address" v-model="formData.address" /></td>
          </tr>
          <tr>
            <td><label for="userType">權限等級:</label></td>
            <td>
              <select id="userType" v-model="formData.userType">
                <option value="">請選擇</option>
                <option value="0">管理員</option>
                <option value="1">一般會員</option>
              </select>
            </td>
          </tr>
        </table>
        <br />
        <div class="action-area">
          <input type="submit" value="確認修改" />

          <button type="button" class="form-back-button" @click="router.push('/users/list')">
            取消並返回列表
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()
const message = ref('')

const formData = ref({
  userId: null,
  email: '',
  userPwd: '',
  userName: '',
  gender: '',
  birth: '',
  phoneNum: '',
  address: '',
  userType: null,
  status: null,
  points: null,
})

const fetchUser = async () => {
  try {
    const userId = route.params.id
    const response = await axios.get(`http://localhost:8080/api/data/get/${userId}`, {
      withCredentials: true,
    })
    if (response.data) {
      formData.value = response.data
      if (formData.value.birth) {
        formData.value.birth = new Date(formData.value.birth).toISOString().split('T')[0]
      }
    }
  } catch (error) {
    message.value = '讀取會員資料失敗'
  }
}

const handleUpdate = async () => {
  try {
    const response = await axios.put('http://localhost:8080/api/data/update', formData.value, {
      withCredentials: true,
    })

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '更新成功！',
        text: `會員資料已修改完成`,
        confirmButtonColor: '#a07d58',
        confirmButtonText: '確定',
      }).then(() => {
        router.push('/users/list')
      })
    } else {
      Swal.fire('更新失敗', response.data.message, 'error')
    }
  } catch (error) {
    Swal.fire('錯誤', '更新失敗，請檢查資料格式', 'error')
  }
}

onMounted(fetchUser)
</script>

<style scoped>
.center-body {
  font-family: '微軟正黑體';
  background-color: #fcf8f0;
  display: flex;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 0;
}
.form-container {
  width: 90%;
  max-width: 600px;
  padding: 35px 45px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}
h1 {
  color: #7b5e47;
  text-align: center;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 15px;
  margin-top: 0;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}
td {
  padding: 10px 0;
}
td:first-child {
  width: 30%;
  text-align: right;
  padding-right: 20px;
  color: #6d6d6d;
  font-weight: 500;
}
input,
select {
  width: 100%;
  height: 40px;
  padding: 10px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  box-sizing: border-box;
}
input[disabled] {
  background-color: #f7f3e8;
  color: #7b5e47;
}
input[type='submit'],
.form-back-button {
  width: auto;
  min-width: 120px;
  height: 40px;
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  transition: all 0.2s ease-in-out;
}
input[type='submit'] {
  background-color: #a07d58;
  color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}
input[type='submit']:hover {
  background-color: #926f4e;
  transform: translateY(-2px);
}
.form-back-button {
  background-color: #e8e4dc;
  color: #4a4a4a;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
input[type='submit']:hover,
.form-back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}
.form-back-button:hover {
  background-color: #dcd5c7;
}
input[type='submit']:active,
.form-back-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.action-area {
  text-align: center;
  margin-top: 25px;
  display: flex;
  justify-content: center;
  gap: 15px;
}
.message-box {
  text-align: center;
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 4px;
}
.error-style {
  color: #b05252;
  background-color: #ffeaea;
  border: 1px dashed #e7c0c0;
}
</style>
