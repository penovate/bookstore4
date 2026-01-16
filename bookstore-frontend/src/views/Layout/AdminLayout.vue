<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()
const drawer = ref(true)

// 合併後的選單：補齊了訂單管理路徑，並採用更直觀的圖示
const items = ref([
  {
    title: '會員管理',
    icon: 'mdi-account-group',
    to: '/dev/admin/users',
  },
  {
    title: '書籍管理',
    icon: 'mdi-book-open-page-variant',
    to: '/dev/admin/books',
  },
  {
    title: '訂單管理',
    icon: 'mdi-clipboard-list-outline',
    to: '/dev/admin/orders', // 補齊路徑
  },
  {
    title: '評價管理',
    icon: 'mdi-star-half-full',
    to: '/dev/admin/reviews',
  },
  {
    title: '進退貨管理',
    icon: 'mdi-truck-return', // 採用第二段更直觀的圖示
    // to: '/dev/admin/returns',
  },
  {
    title: '數據報表分析',
    icon: 'mdi-chart-bar',
    // to: '/dev/admin/reports',
  },
  {
    title: '讀書會管理',
    icon: 'mdi-book-multiple',
    // to: '/dev/admin/bookclubs',
  },
])

const handleLogout = () => {
  Swal.fire({
    title: '確定要登出嗎？',
    text: '登出後將返回登入介面',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2E5C43',
    cancelButtonColor: '#aaa',
    confirmButtonText: '登出',
    cancelButtonText: '取消',
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        // 呼叫後端登出 API
        await axios.get('http://localhost:8080/api/logout', { withCredentials: true })
      } catch (error) {
        console.error('後端登出失敗', error)
      } finally {
        // 無論後端是否成功，前端強制清除資料並跳轉
        localStorage.clear()
        router.push('/login')

        Swal.fire({
          icon: 'success',
          title: '已成功登出',
          timer: 1500,
          showConfirmButton: false,
        })
      }
    }
  })
}
</script>

<template>
  <v-layout class="rounded rounded-md">
    <v-navigation-drawer v-model="drawer" color="primary">
      <v-list-item title="BookStore" subtitle="後台管理系統" class="py-4" to="/home">
        <template v-slot:prepend>
          <v-icon icon="mdi-leaf" class="me-2"></v-icon>
        </template>
      </v-list-item>

      <v-divider></v-divider>

      <v-list density="compact" nav>
        <v-list-item
          v-for="(item, i) in items"
          :key="i"
          :value="item"
          :to="item.to"
          :prepend-icon="item.icon"
          color="accent"
        >
          <v-list-item-title v-text="item.title"></v-list-item-title>
        </v-list-item>
      </v-list>

      <template v-slot:append>
        <div class="pa-2">
          <v-btn
            block
            color="accent"
            variant="tonal"
            prepend-icon="mdi-storefront-outline"
            class="mb-2"
            style="
              color: #ffffff !important;
              font-weight: 500 !important;
              background-color: rgba(var(--v-theme-accent), 0.3) !important;
              filter: drop-shadow(0 0 1px rgba(255, 255, 255, 0.2));
            "
            href="/dev/user/home"
            target="_blank"
          >
            前台網頁
          </v-btn>

          <v-btn block color="secondary" @click="handleLogout" prepend-icon="mdi-logout">
            登出
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar elevation="0" class="bg-background" density="compact">
      <v-app-bar-nav-icon
        variant="text"
        color="primary"
        @click.stop="drawer = !drawer"
      ></v-app-bar-nav-icon>

      <v-spacer></v-spacer>

      <v-btn icon="mdi-bell-outline" color="secondary" variant="text"></v-btn>
      <v-avatar class="mx-2" size="32" color="secondary">
        <span class="text-white text-caption">Admin</span>
      </v-avatar>
    </v-app-bar>

    <v-main class="bg-background" style="min-height: 100vh">
      <v-container fluid class="pa-6">
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-layout>
</template>
