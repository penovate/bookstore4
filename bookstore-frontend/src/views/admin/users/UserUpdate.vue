<template>
  <div class="update-page-wrapper">
    <div class="header-section mb-6">
      <h2 class="forest-main-title">修改會員資料</h2>
    </div>

    <v-card
      width="100%"
      max-width="850"
      class="pa-8 forest-card-border elevation-2 mx-auto rounded-xl"
    >
      <v-card-item class="text-center mb-4">
        <v-icon icon="mdi-account-edit-outline" size="large" color="primary" class="mb-2"></v-icon>
        <v-card-title class="text-h5 font-weight-bold text-primary"> 編輯帳號資訊 </v-card-title>
      </v-card-item>

      <v-divider class="my-4"></v-divider>

      <v-form ref="updateForm" @submit.prevent="handleUpdate" v-if="formData.userId">
        <v-container>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                label="會員編號 (ID)"
                :model-value="formData.userId"
                readonly
                disabled
                variant="filled"
                density="compact"
                bg-color="grey-lighten-4"
                color="primary"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.email"
                label="帳號 (Email)"
                type="email"
                variant="outlined"
                density="compact"
                color="primary"
                :rules="[(v) => !!v || 'Email 必填']"
                required
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.userPwd"
                label="修改新密碼"
                type="password"
                placeholder="若不修改請留空"
                variant="outlined"
                density="compact"
                color="primary"
                hint="不修改請保持空白"
                persistent-hint
                @input="confirmPwd = ''"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="confirmPwd"
                label="確認新密碼"
                type="password"
                placeholder="請再次輸入新密碼"
                variant="outlined"
                density="compact"
                color="primary"
                :disabled="!formData.userPwd || formData.userPwd.trim() === ''"
                :rules="[
                  (v) =>
                    !formData.userPwd || formData.userPwd.trim() === '' || !!v || '確認密碼必填',
                  (v) =>
                    !formData.userPwd ||
                    formData.userPwd.trim() === '' ||
                    v === formData.userPwd ||
                    '兩次輸入的密碼不一致',
                ]"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.userName"
                label="姓名"
                variant="outlined"
                density="compact"
                color="primary"
                :rules="[(v) => !!v || '姓名必填']"
                required
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="formData.gender"
                label="性別"
                :items="[
                  { title: '男', value: 'M' },
                  { title: '女', value: 'F' },
                ]"
                variant="outlined"
                density="compact"
                color="primary"
              ></v-select>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.birth"
                label="生日"
                type="date"
                variant="outlined"
                density="compact"
                color="primary"
              ></v-text-field>
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.phoneNum"
                label="聯絡電話"
                variant="outlined"
                density="compact"
                color="primary"
              ></v-text-field>
            </v-col>

            <v-col cols="12">
              <v-text-field
                v-model="formData.address"
                label="地址"
                variant="outlined"
                density="compact"
                color="primary"
              ></v-text-field>
            </v-col>

            <v-col cols="12" v-if="currentUserRole === 'SUPER_ADMIN'">
              <v-select
                v-model="formData.userType"
                label="權限等級"
                :items="roleSelectOptions"
                variant="outlined"
                density="compact"
                color="primary"
              ></v-select>
            </v-col>
          </v-row>
        </v-container>

        <v-divider class="my-6"></v-divider>

        <v-card-actions class="justify-center pb-4">
          <v-btn
            type="submit"
            color="primary"
            variant="elevated"
            size="large"
            class="px-10 mr-4 rounded-lg font-weight-bold"
          >
            確認修改資料
          </v-btn>
          <v-btn
            color="primary"
            variant="outlined"
            size="large"
            class="px-10 rounded-lg font-weight-bold"
            @click="router.push('/dev/admin/users')"
          >
            取消返回
          </v-btn>
        </v-card-actions>
      </v-form>

      <v-card-text v-else class="text-center pa-10">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
        <div class="mt-4 text-grey">正在讀取會員資料...</div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()
const updateForm = ref(null)
const confirmPwd = ref('')

const currentUserRole = localStorage.getItem('userRole')
const currentUserId = localStorage.getItem('userId')

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

const roleSelectOptions = computed(() => {
  const options = [
    { title: '超級管理員', value: 0 },
    { title: '一般管理員', value: 1 },
  ]
  if (formData.value.userType === 2) {
    options.push({ title: '一般會員 (禁止修改權限)', value: 2, props: { disabled: true } })
  }
  return options
})

const fetchUser = async () => {
  try {
    const userId = route.params.id
    const response = await axios.get(`http://localhost:8080/api/data/get/${userId}`)
    if (response.data) {
      const userData = response.data

      if (
        currentUserRole === 'ADMIN' &&
        (userData.userType === 0 || userData.userType === 1) &&
        String(userData.userId) !== currentUserId
      ) {
        Swal.fire({ icon: 'error', title: '權限不足', text: '您無權修改管理員等級的資料！' })
        router.push('/dev/admin/users')
        return
      }

      formData.value = userData
      if (formData.value.birth) {
        formData.value.birth = new Date(formData.value.birth).toISOString().split('T')[0]
      }
    }
  } catch (error) {
    Swal.fire('錯誤', '讀取會員資料失敗', 'error')
  }
}

const handleUpdate = async () => {
  const { valid } = await updateForm.value.validate()
  if (!valid) return

  if (formData.value.userPwd && formData.value.userPwd.trim() !== '') {
    if (formData.value.userPwd !== confirmPwd.value) {
      Swal.fire({
        icon: 'error',
        title: '密碼不一致',
        text: '請確認兩次輸入的新密碼是否相同！',
        confirmButtonColor: '#2E5C43',
      })
      return
    }
  }

  try {
    const checkRes = await axios.get('http://localhost:8080/api/users/check-unique', {
      params: {
        userId: formData.value.userId,
        email: formData.value.email,
        phoneNum: formData.value.phoneNum,
      },
    })

    if (!checkRes.data.success) {
      Swal.fire({
        icon: 'warning',
        title: '更新失敗',
        text: checkRes.data.message,
        confirmButtonColor: '#2E5C43',
      })
      return
    }

    const response = await axios.put('http://localhost:8080/api/data/update', formData.value)
    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '更新成功！',
        confirmButtonColor: '#2E5C43',
      }).then(() => {
        router.push('/dev/admin/users')
      })
    } else {
      Swal.fire({
        icon: 'error',
        title: '更新失敗',
        text: response.data.message,
        confirmButtonColor: '#2E5C43',
      })
    }
  } catch (error) {
    Swal.fire({ icon: 'error', title: '錯誤', text: '連線異常', confirmButtonColor: '#2E5C43' })
  }
}

onMounted(fetchUser)
</script>

<style scoped lang="scss">
.update-page-wrapper {
  width: 100%;
}

.forest-main-title {
  color: #2e5c43;
  font-size: 2.25rem;
  font-weight: 800;
  letter-spacing: 1px;
}

.forest-card-border {
  border-top: 6px solid #2e5c43 !important;
  background-color: white !important;
}

:deep(.v-field--disabled) {
  background-color: #f8f9f0 !important;
  opacity: 0.8;
}

:deep(.v-label.v-field-label--floating) {
  color: #2e5c43 !important;
  opacity: 1;
}
</style>
