<template>
  <div class="center-body">
    <div class="form-container">
      <h1>新增會員資料</h1>

      <div v-if="message" :class="['message-box', isSuccess ? 'success-style' : 'error-style']">
        <strong>{{ message }}</strong>
      </div>

      <form @submit.prevent="handleSubmit">
        <table>
          <tr>
            <td><label for="email">帳號（Email）:</label></td>
            <td>
              <input
                type="email"
                id="email"
                v-model="formData.email"
                :disabled="isSuccess"
                required
                placeholder="必填"
              />
            </td>
          </tr>
          <tr>
            <td><label for="password">密碼:</label></td>
            <td>
              <input
                type="password"
                id="password"
                v-model="formData.userPwd"
                :disabled="isSuccess"
                required
              />
            </td>
          </tr>
          <tr>
            <td><label for="name">姓名:</label></td>
            <td>
              <input
                type="text"
                id="name"
                v-model="formData.userName"
                :disabled="isSuccess"
                required
                placeholder="必填"
              />
            </td>
          </tr>
          <tr>
            <td><label for="gender">性別:</label></td>
            <td>
              <select id="gender" v-model="formData.gender" :disabled="isSuccess">
                <option value="">請選擇</option>
                <option value="M">男</option>
                <option value="F">女</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><label for="birth">生日:</label></td>
            <td><input type="date" id="birth" v-model="formData.birth" :disabled="isSuccess" /></td>
          </tr>
          <tr>
            <td><label for="phone">聯絡電話:</label></td>
            <td>
              <input
                type="text"
                id="phone"
                v-model="formData.phoneNum"
                :disabled="isSuccess"
                required
                maxlength="10"
                placeholder="09開頭手機號碼"
              />
            </td>
          </tr>
          <tr>
            <td><label for="address">地址:</label></td>
            <td>
              <input type="text" id="address" v-model="formData.address" :disabled="isSuccess" />
            </td>
          </tr>
          <tr>
            <td><label for="userType">權限等級:</label></td>
            <td>
              <select id="userType" v-model="formData.userType" :disabled="isSuccess">
                <option value="">請選擇</option>
                <option value="0">超級管理員</option>
                <option value="1">一般管理員</option>
              </select>
            </td>
          </tr>
        </table>

        <div class="form-action-group">
          <input v-if="!isSuccess" type="submit" value="新增會員資料" />
          <button type="button" class="form-back-button" @click="router.push('/users/list')">
            {{ isSuccess ? '確定並返回列表' : '返回會員列表' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()
const isSuccess = ref(false)
const message = ref('')

const formData = reactive({
  email: '',
  userPwd: '',
  userName: '',
  gender: '',
  birth: '',
  phoneNum: '',
  address: '',
  userType: '',
})

const handleSubmit = async () => {
  if (!/^09\d{8}$/.test(formData.phoneNum)) {
    message.value = '新增失敗！電話號碼格式不正確！'
    return
  }

  if (formData.userType === '') {
    message.value = '新增失敗！請選擇權限等級！'
    return
  }

  try {
    const response = await axios.post('http://localhost:8080/api/users/insert', formData)

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '新增成功！',
        text: `會員 ${formData.userName} 已成功加入`,
        confirmButtonColor: '#a07d58',
        confirmButtonText: '確定',
      }).then(() => {
        router.push('/users/list')
      })
    } else {
      Swal.fire('失敗', response.data.message, 'error')
    }
  } catch (error) {
    Swal.fire('系統錯誤', '請檢查後端伺服器是否啟動', 'error')
  }
}
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
.message-box {
  text-align: center;
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 4px;
}
.success-style {
  color: #2d5a27;
  background-color: #e8f5e9;
  border: 1px dashed #a5d6a7;
}
.error-style {
  color: #b05252;
  background-color: #ffeaea;
  border: 1px dashed #e7c0c0;
}
table {
  width: 100%;
  margin-bottom: 20px;
}
td {
  padding: 8px 0;
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
input:disabled,
select:disabled {
  background-color: #f7f3e8;
  color: #4a4a4a;
  border: 1px solid #dcd5c7;
}
input[type='submit'] {
  width: auto;
  min-width: 120px;
  height: 40px;
  padding: 10px 20px;
  background-color: #a07d58;
  color: white;
  font-weight: bold;
  cursor: pointer;
  border: none;
  border-radius: 4px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  transition: all 0.2s;
  display: inline-block;
  margin: 5px;
}

input[type='submit']:hover {
  background-color: #926f4e;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.25);
}
.form-back-button {
  height: 40px;
  padding: 10px 20px;
  background-color: #e8e4dc;
  color: #4a4a4a;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 5px;
  transition: all 0.2s ease-in-out;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.form-back-button:hover {
  background-color: #dcd5c7;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.form-back-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.form-action-group {
  text-align: center;
  margin-top: 25px;
  display: block;
  width: 100%;
}
</style>
