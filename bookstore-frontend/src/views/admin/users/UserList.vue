<template>
  <v-app>
    <div class="center-body forest-gradient-bg">
      <div class="list-container forest-card-border elevation-2">
        <h2 class="forest-title-text">所有會員資料</h2>

        <v-row class="mb-4" justify="center" align="center">
          <v-col cols="12" md="4">
            <v-text-field
              v-model="filters.keyword"
              label="搜尋姓名/電話/Email"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              hide-details
              clearable
              color="primary"
              @keydown.enter="fetchUsers"
              @click:clear="resetFilters"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="filters.userTypeFilter"
              label="權限篩選"
              :items="roleOptions"
              variant="outlined"
              density="compact"
              hide-details
              color="primary"
            ></v-select>
          </v-col>
          <v-col cols="auto">
            <v-btn color="primary" @click="fetchUsers" class="mr-2 px-6">查詢</v-btn>
            <v-btn variant="outlined" color="primary" @click="resetFilters">取消篩選</v-btn>
          </v-col>
        </v-row>

        <v-data-table
          :headers="headers"
          :items="users"
          :items-per-page="10"
          class="mt-5 forest-table-style"
          hover
        >
          <template v-slot:item.userName="{ item }">
            <a href="#" @click.prevent="goToDetail(item.userId)" class="forest-link">
              {{ item.userName }}
            </a>
          </template>

          <template v-slot:item.userType="{ item }">
            <v-chip
              :color="getRoleColor(item.userType)"
              size="small"
              variant="flat"
              class="text-white"
            >
              {{ formatUserType(item.userType) }}
            </v-chip>
          </template>

          <template v-slot:item.action="{ item }">
            <v-btn
              v-if="canEdit(item)"
              icon="mdi-pencil"
              variant="text"
              color="primary"
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
                class="ml-2 font-weight-bold text-caption"
              >
                {{ item.status === 1 ? '啟用中' : '停權' }}
              </span>
            </div>
          </template>
        </v-data-table>

        <div class="action-footer mt-10">
          <v-btn
            v-if="currentUserRole === 'SUPER_ADMIN'"
            color="primary"
            prepend-icon="mdi-account-plus"
            class="mr-4"
            @click="router.push('/dev/admin/users/insert')"
          >
            新增會員資料
          </v-btn>
          <v-btn
            variant="outlined"
            color="primary"
            prepend-icon="mdi-home"
            @click="router.push('/users')"
          >
            回到會員中心首頁
          </v-btn>
        </div>
      </div>
    </div>
  </v-app>
</template>

<script setup>
// ... (script 部分保持不變，邏輯是一樣的)
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
  { title: '編號', key: 'userId', sortable: true, width: '100px', align: 'center' },
  { title: '姓名', key: 'userName', sortable: true, align: 'center' },
  { title: 'Email', key: 'email', align: 'center' },
  { title: '電話', key: 'phoneNum', align: 'center' },
  { title: '權限等級', key: 'userType', align: 'center' },
  { title: '修改', key: 'action', sortable: false, align: 'center' },
  { title: '帳號狀態', key: 'status', sortable: false, align: 'center' },
]

const formatUserType = (type) =>
  ({ 0: '超級管理員', 1: '一般管理員', 2: '一般會員' })[type] || '未知'

// 💡 修改角色顏色以配合森林系 (綠、深綠、灰)
const getRoleColor = (type) => ({ 0: 'primary', 1: 'secondary' })[type] || 'grey'

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
    confirmButtonColor: newStatus === 2 ? '#d33' : '#4CAF50', // 改為綠色
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

const goToUpdate = (id) => router.push(`/dev/admin/users/update/${id}`)
const goToDetail = (id) => router.push(`/dev/admin/users/get/${id}`)

onMounted(fetchUsers)
</script>

<style lang="scss" scoped>
/* 💡 風格完全同步組員，但結構不動你的 */

// 1. 背景漸層同步
.forest-gradient-bg {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
  display: flex;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 0;
}

// 2. 容器邊框同步 (組員的 border-t-4 效果)
.list-container {
  width: 95%;
  max-width: 1200px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 8px;
  border-top: 4px solid #2e5c43 !important; // 加入組員的頂部粗邊框
  border-left: 1px solid #d7ccc8;
  border-right: 1px solid #d7ccc8;
  border-bottom: 1px solid #d7ccc8;
}

// 3. 標題顏色同步 (深綠色)
.forest-title-text {
  color: #2e5c43;
  margin-bottom: 25px;
  font-weight: bold;
  font-size: 2rem;
}

// 4. 表格內部風格同步
.forest-table-style {
  :deep(.v-data-table-header) {
    background-color: #f9fbe7 !important; // 米綠色表頭
  }

  :deep(.v-data-table-header__content) {
    font-weight: bold;
    color: #2e5c43;
  }

  :deep(.v-data-table__tr:hover) {
    background-color: #f1f8e9 !important; // 滑過時的淺綠色
  }
}

// 5. 連結顏色同步
.forest-link {
  color: #2e5c43;
  text-decoration: none;
  font-weight: bold;
  &:hover {
    text-decoration: underline;
  }
}
</style>
