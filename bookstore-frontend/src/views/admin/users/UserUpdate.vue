<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex justify-center" fluid>
        <v-card width="100%" max-width="700" class="pa-8 elevation-10" rounded="lg">
          <v-card-item class="text-center">
            <v-card-title class="text-h5 font-weight-bold text-brown-darken-2">
              <v-icon icon="mdi-account-edit" class="mr-2"></v-icon> 會員資料修改
            </v-card-title>
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
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.email"
                    label="帳號 (Email)"
                    type="email"
                    variant="outlined"
                    density="compact"
                    :rules="[(v) => !!v || 'Email 必填']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.userPwd"
                    label="密碼"
                    type="password"
                    placeholder="若不修改請留空"
                    variant="outlined"
                    density="compact"
                    hint="不修改請保持空白"
                    persistent-hint
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.userName"
                    label="姓名"
                    variant="outlined"
                    density="compact"
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
                  ></v-select>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.birth"
                    label="生日"
                    type="date"
                    variant="outlined"
                    density="compact"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.phoneNum"
                    label="聯絡電話"
                    variant="outlined"
                    density="compact"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.address"
                    label="地址"
                    variant="outlined"
                    density="compact"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" v-if="currentUserRole === 'SUPER_ADMIN'">
                  <v-select
                    v-model="formData.userType"
                    label="權限等級"
                    :items="roleSelectOptions"
                    variant="outlined"
                    density="compact"
                    color="brown"
                  ></v-select>
                </v-col>
              </v-row>
            </v-container>

            <v-divider class="my-4"></v-divider>

            <v-card-actions class="justify-center">
              <v-btn type="submit" color="brown" variant="elevated" size="large" class="px-8 mr-4">
                確認修改
              </v-btn>
              <v-btn
                color="grey-darken-1"
                variant="outlined"
                size="large"
                class="px-8"
                @click="router.push('/dev/admin/users')"
              >
                取消返回
              </v-btn>
            </v-card-actions>
          </v-form>

          <v-card-text v-else class="text-center pa-10">
            <v-progress-circular indeterminate color="brown"></v-progress-circular>
          </v-card-text>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()
const updateForm = ref(null)

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

  try {
    const response = await axios.put('http://localhost:8080/api/data/update', formData.value)
    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '更新成功！',
        confirmButtonColor: '#a07d58',
      }).then(() => {
        router.push('/dev/admin/users')
      })
    } else {
      Swal.fire('更新失敗', response.data.message, 'error')
    }
  } catch (error) {
    Swal.fire('錯誤', '連線異常', 'error')
  }
}

onMounted(fetchUser)
</script>

<style scoped>
.fill-height {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}
:deep(.v-field--disabled) {
  background-color: #f5f5f5 !important;
}
</style>
