<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex justify-center" fluid>
        <v-card width="100%" max-width="600" class="pa-6 elevation-8" rounded="lg">
          <v-card-item class="text-center">
            <v-icon icon="mdi-account-details" size="large" color="brown" class="mb-2"></v-icon>
            <v-card-title class="text-h5 font-weight-bold text-brown-darken-2">
              會員詳細資料
            </v-card-title>
          </v-card-item>

          <v-divider class="mb-6"></v-divider>

          <v-card-text v-if="user">
            <v-row v-for="(field, index) in displayFields" :key="index" class="mb-2" align="center">
              <v-col
                cols="4"
                class="text-right text-subtitle-1 font-weight-bold text-grey-darken-2"
              >
                {{ field.label }}
              </v-col>
              <v-col cols="8">
                <v-text-field
                  :model-value="field.value"
                  readonly
                  variant="filled"
                  density="compact"
                  hide-details
                  bg-color="grey-lighten-4"
                  color="brown"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-card-text>

          <v-card-text v-else class="text-center pa-10">
            <v-progress-circular indeterminate color="brown"></v-progress-circular>
            <div class="mt-4 text-grey">正在取得會員資料...</div>
          </v-card-text>

          <v-divider class="my-6"></v-divider>

          <v-card-actions class="justify-center pb-4">
            <v-btn
              v-if="user && canEdit(user)"
              color="brown-darken-1"
              variant="elevated"
              prepend-icon="mdi-pencil"
              size="large"
              class="px-6 mr-4"
              @click="router.push(`/users/update/${user.userId}`)"
            >
              修改資料
            </v-btn>

            <v-btn
              variant="outlined"
              color="grey-darken-1"
              prepend-icon="mdi-arrow-left"
              size="large"
              class="px-6"
              @click="router.push('/users/list')"
            >
              返回列表
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()
const user = ref(null)

const currentUserRole = localStorage.getItem('userRole')
const currentUserId = localStorage.getItem('userId')

const displayFields = computed(() => {
  if (!user.value) return []
  return [
    { label: '會員編號', value: user.value.userId },
    { label: 'Email 帳號', value: user.value.email },
    { label: '會員姓名', value: user.value.userName },
    { label: '性別', value: formatGender(user.value.gender) },
    { label: '生日', value: formatDate(user.value.birth) },
    { label: '聯絡電話', value: user.value.phoneNum },
    { label: '地址', value: user.value.address },
    { label: '權限等級', value: formatUserType(user.value.userType) },
  ]
})

const canEdit = (u) => {
  return (
    currentUserRole === 'SUPER_ADMIN' ||
    (currentUserRole === 'ADMIN' && (u.userType === 2 || String(u.userId) === currentUserId))
  )
}

const formatGender = (code) => (code === 'M' ? '男' : code === 'F' ? '女' : '未設定')
const formatDate = (val) => (val ? new Date(val).toISOString().split('T')[0] : '未設定')
const formatUserType = (type) =>
  ({ 0: '超級管理員', 1: '一般管理員', 2: '一般會員' })[type] || '未知'

const fetchUserDetail = async () => {
  try {
    const userId = route.params.id
    const response = await axios.get(`http://localhost:8080/api/data/get/${userId}`)
    user.value = response.data
  } catch (error) {
    Swal.fire({ icon: 'error', title: '讀取失敗', text: '無法取得會員詳細資料' })
  }
}

onMounted(fetchUserDetail)
</script>

<style scoped>
.fill-height {
  background-image: linear-gradient(135deg, #fcf8f0 0%, #f3e9dc 100%);
}

:deep(.v-field--disabled),
:deep(.v-field--readonly) {
  opacity: 1 !important;
  color: rgba(0, 0, 0, 0.87) !important;
}

.text-brown-darken-2 {
  color: #5d4037 !important;
}
</style>
