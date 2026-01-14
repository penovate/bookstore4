<template>
  <v-app>
    <div class="center-body">
      <div class="list-container">
        <h2>所有會員資料</h2>

        <v-row class="mb-4" justify="center" align="center">
          <v-col cols="12" md="4">
            <v-text-field
              v-model="filters.keyword"
              label="輸入「姓名」、「電話」或「Email」查詢"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              hide-details
              clearable
              @keydown.enter="fetchUsers"
              @click:clear="resetFilters"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="filters.userTypeFilter"
              label="權限篩選"
              :items="[
                { title: '顯示所有使用者', value: '' },
                { title: '超級管理員', value: '0' },
                { title: '一般管理員', value: '1' },
                { title: '一般會員', value: '2' },
              ]"
              variant="outlined"
              density="compact"
              hide-details
            ></v-select>
          </v-col>
          <v-col cols="auto">
            <v-btn color="brown" @click="fetchUsers" class="mr-2">查詢</v-btn>
            <v-btn variant="outlined" @click="resetFilters">取消篩選</v-btn>
          </v-col>
        </v-row>

        <v-data-table
          :headers="headers"
          :items="users"
          :items-per-page="10"
          :items-per-page-options="[10, 20, 50, 100]"
          items-per-page-text="每頁顯示筆數："
          class="elevation-1 mt-5"
          hover
        >
          <template v-slot:item.userName="{ item }">
            <a href="#" @click.prevent="goToDetail(item.userId)" class="user-link">
              {{ item.userName }}
            </a>
          </template>

          <template v-slot:item.userType="{ item }">
            <v-chip :color="getRoleColor(item.userType)" size="small" variant="flat">
              {{ formatUserType(item.userType) }}
            </v-chip>
          </template>

          <template v-slot:item.action="{ item }">
            <v-btn
              v-if="canEdit(item)"
              icon="mdi-pencil-box"
              variant="text"
              color="brown-lighten-1"
              @click="goToUpdate(item.userId)"
            ></v-btn>
          </template>

          <template v-slot:item.status="{ item }">
            <div class="d-flex align-center justify-center">
              <v-switch
                :model-value="item.status === 1"
                color="success"
                hide-details
                density="compact"
                @click.prevent="handleToggleStatus(item)"
              ></v-switch>
              <span
                :class="item.status === 1 ? 'text-success' : 'text-error'"
                class="ml-2 font-weight-bold"
              >
                {{ item.status === 1 ? '啟用' : '停權' }}
              </span>
            </div>
          </template>
        </v-data-table>

        <div class="action-footer mt-10">
          <v-btn
            v-if="currentUserRole === 'SUPER_ADMIN'"
            color="brown"
            prepend-icon="mdi-account-plus"
            class="mr-4"
            @click="router.push('/users/insert')"
          >
            新增會員資料
          </v-btn>
          <v-btn variant="outlined" prepend-icon="mdi-home" @click="router.push('/users')">
            回到會員中心首頁
          </v-btn>
        </div>
      </div>
    </div>
  </v-app>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import Swal from 'sweetalert2'

const router = useRouter()
const route = useRoute()
const users = ref([])
const currentUserRole = localStorage.getItem('userRole')
const currentUserId = localStorage.getItem('userId')

const filters = reactive({ keyword: '', userTypeFilter: '' })
const roleOptions = [
  { title: '顯示所有使用者', value: '' },
  { title: '超級管理員', value: '0' },
  { title: '一般管理員', value: '1' },
  { title: '一般會員', value: '2' },
]

const headers = [
  {
    title: '編號',
    key: 'userId',
    sortable: true,
    width: '100px',
    align: 'center',
  },
  { title: '姓名', key: 'userName', sortable: true, align: 'center' },
  { title: 'Email', key: 'email', align: 'center' },
  { title: '電話', key: 'phoneNum', align: 'center' },
  { title: '權限等級', key: 'userType', align: 'center' },
  { title: '修改', key: 'action', sortable: false, align: 'center' },
  { title: '帳號狀態', key: 'status', sortable: false, align: 'center' },
]

const formatUserType = (type) =>
  ({ 0: '超級管理員', 1: '一般管理員', 2: '一般會員' })[type] || '未知'
const getRoleColor = (type) =>
  ({ 0: 'deep-purple-accent-2', 1: 'teal-darken-1' })[type] || 'grey-darken-1'
const canEdit = (u) =>
  currentUserRole === 'SUPER_ADMIN' ||
  (currentUserRole === 'ADMIN' && (u.userType === 2 || String(u.userId) === currentUserId))

const fetchUsers = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/data/list', {
      params: {
        keyword: filters.keyword || null,
        userTypeFilter: filters.userTypeFilter || null,
      },
    })
    users.value = res.data
  } catch (error) {
    console.error('查詢失敗', error)
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.userTypeFilter = ''
  fetchUsers()
}

const handleToggleStatus = (user) => {
  const newStatus = user.status === 1 ? 2 : 1
  const actionText = newStatus === 2 ? '停權' : '恢復啟用'

  if (String(user.userId) === currentUserId && newStatus === 2) {
    return Swal.fire({ icon: 'error', title: '操作禁止', text: '您不能停權自己的帳號！' })
  }
  if (
    currentUserRole === 'ADMIN' &&
    (user.userType === 0 || user.userType === 1) &&
    String(user.userId) !== currentUserId
  ) {
    return Swal.fire({ icon: 'error', title: '權限不足', text: '您無權管理其他管理員的權限！' })
  }

  Swal.fire({
    title: `確定要${actionText}會員「${user.userName}」嗎？`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: actionText,
    cancelButtonText: '取消',
    confirmButtonColor: newStatus === 2 ? '#d33' : '#9fb89e',
    cancelButtonColor: '#aaa',
  }).then(async (result) => {
    if (result.isConfirmed) {
      const res = await axios.put(`http://localhost:8080/api/data/status/${user.userId}`, {
        status: newStatus,
      })
      if (res.data.success) {
        user.status = newStatus
        Swal.fire({ icon: 'success', title: '更新成功', timer: 1000, showConfirmButton: false })
      }
    }
  })
}

const goToUpdate = (id) => router.push(`/users/update/${id}`)
const goToDetail = (id) => router.push(`/users/get/${id}`)

onMounted(fetchUsers)
</script>

<style scoped>
.center-body {
  background-color: #fcf8f0;
  display: flex;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 0;
}
.list-container {
  width: 95%;
  max-width: 1200px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}
h2 {
  color: #7b5e47;
  margin-bottom: 25px;
  border-bottom: 2px solid #e8e4dc;
  padding-bottom: 10px;
}
.user-link {
  color: #a07d58;
  text-decoration: none;
  font-weight: bold;
}
.user-link:hover {
  text-decoration: underline;
}
</style>
