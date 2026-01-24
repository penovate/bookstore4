<template>
  <v-container class="fill-height d-flex justify-center align-center py-12" fluid>
    <v-card width="100%" max-width="500" class="mx-auto pa-10 rounded-xl forest-card">
      <v-card-item class="text-center mb-6">
        <v-icon icon="mdi-lock-reset" size="48" color="primary" class="mb-2"></v-icon>
        <v-card-title class="text-h4 font-weight-bold text-primary">重設密碼</v-card-title>
        <p class="text-subtitle-1 text-grey mt-2">請選擇驗證方式以重核身分</p>
      </v-card-item>

      <v-tabs v-model="verifyMethod" color="primary" grow class="mb-6">
        <v-tab value="email"><v-icon start>mdi-email</v-icon>電子郵件</v-tab>
        <v-tab value="phone"><v-icon start>mdi-phone</v-icon>手機號碼</v-tab>
      </v-tabs>

      <v-form ref="forgotFormRef" @submit.prevent="handleVerify">
        <v-window v-model="verifyMethod">
          <v-window-item value="email">
            <v-text-field
              v-model="formData.email"
              label="註冊電子信箱"
              prepend-inner-icon="mdi-email-outline"
              variant="outlined"
              :rules="verifyMethod === 'email' ? [(v) => !!v || 'Email 為必填'] : []"
            ></v-text-field>
          </v-window-item>

          <v-window-item value="phone">
            <v-text-field
              v-model="formData.phoneNum"
              label="註冊手機號碼"
              prepend-inner-icon="mdi-phone-outline"
              variant="outlined"
              maxlength="10"
              :rules="verifyMethod === 'phone' ? [(v) => !!v || '手機號碼為必填'] : []"
            ></v-text-field>
          </v-window-item>
        </v-window>

        <v-text-field
          v-model="formData.birth"
          label="西元生日"
          type="date"
          prepend-inner-icon="mdi-calendar-range"
          variant="outlined"
          class="mt-2"
          :rules="[(v) => !!v || '生日為必填']"
        ></v-text-field>

        <v-row class="mt-6">
          <v-col cols="6">
            <v-btn variant="outlined" block height="50" color="grey" @click="resetForm">
              清除重填
            </v-btn>
          </v-col>
          <v-col cols="6">
            <v-btn type="submit" block height="50" color="primary" class="font-weight-bold">
              下一步
            </v-btn>
          </v-col>
        </v-row>

        <v-btn variant="text" block class="mt-4" @click="router.push('/dev/user/login')">
          返回登入
        </v-btn>
      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()
const forgotFormRef = ref(null)
const verifyMethod = ref('email')

const formData = reactive({
  email: '',
  phoneNum: '',
  birth: '',
})

const resetForm = () => {
  formData.email = ''
  formData.phoneNum = ''
  formData.birth = ''
  forgotFormRef.value.resetValidation()
}

const handleVerify = async () => {
  const { valid } = await forgotFormRef.value.validate()
  if (!valid) return

  const payload = {
    birth: formData.birth,
  }

  if (verifyMethod.value === 'email') {
    payload.email = formData.email
  } else {
    Swal.fire('提醒', '手機驗證功能開發中，請先使用 Email 驗證', 'info')
    return
  }

  try {
    const response = await axios.post('http://localhost:8080/api/user/forget-password', payload)

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '驗證成功',
        text: '請設定您的新密碼',
        confirmButtonColor: '#2E5C43',
        }).then(() => {
        router.push({
          path: '/dev/user/reset-password-by-email',
          query: {
            userId: response.data.userId,
            email: formData.email,
          },
        })
      })
    } else {
      Swal.fire('驗證失敗', response.data.message || '資料不匹配，請重新檢查', 'error')
    }
  } catch (error) {
    console.error(error)
    Swal.fire('錯誤', '伺服器連線異常，請稍後再試', 'error')
  }
}
</script>
