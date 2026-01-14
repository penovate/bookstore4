<template>
  <v-app>
    <v-main class="home-bg">
      <v-container class="fill-height d-flex justify-center align-center" fluid>
        <v-card width="100%" max-width="700" class="pa-8 elevation-10 rounded-lg">
          <v-card-item class="text-center mb-4">
            <v-card-title class="text-h4 font-weight-bold text-brown-darken-3">
              網路書店後台系統
            </v-card-title>
            <v-card-subtitle class="text-h6 mt-2">功能主選單</v-card-subtitle>
          </v-card-item>

          <v-divider class="mb-6"></v-divider>

          <v-card-text>
            <v-row>
              <v-col v-for="menu in menuItems" :key="menu.title" cols="12" sm="6">
                <v-hover v-slot="{ isHovering, props }">
                  <v-card
                    v-bind="props"
                    :color="isHovering ? 'brown-lighten-5' : 'white'"
                    :elevation="isHovering ? 8 : 2"
                    height="120"
                    class="d-flex align-center justify-center cursor-pointer transition-swing rounded-lg border-brown"
                    @click="router.push(menu.path)"
                  >
                    <div class="text-center">
                      <v-icon
                        :icon="menu.icon"
                        size="36"
                        color="brown-darken-1"
                        class="mb-2"
                      ></v-icon>
                      <div class="text-h6 font-weight-bold text-brown-darken-2">
                        {{ menu.title }}
                      </div>
                    </div>
                  </v-card>
                </v-hover>
              </v-col>
            </v-row>
          </v-card-text>

          <v-divider class="my-6"></v-divider>

          <v-card-actions class="justify-center">
            <v-btn
              variant="outlined"
              color="grey-darken-1"
              prepend-icon="mdi-logout"
              size="large"
              class="px-10"
              @click="handleLogout"
            >
              登出系統
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()

// 💡 定義功能清單與對應圖示
const menuItems = [
  { title: '會員中心', icon: 'mdi-account-group', path: '/users' },
  { title: '書籍資料處理', icon: 'mdi-book-open-page-variant', path: '/books' },
  { title: '訂單購物系統', icon: 'mdi-cart-check', path: '/orders' },
  { title: '評價管理', icon: 'mdi-comment-quote', path: '/reviews' },
]

const handleLogout = () => {
  Swal.fire({
    title: '確定要登出嗎？',
    text: '登出後將返回登入介面',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#7B5E47',
    cancelButtonColor: '#E8E4DC',
    confirmButtonText: '登出',
    cancelButtonText: '取消',
  }).then(async (result) => {
    if (result.isConfirmed) {
      // 💡 清空前端身分證件
      localStorage.clear()
      try {
        await axios.get('http://localhost:8080/api/logout', { withCredentials: true })
        router.push('/login?logout=true')
      } catch (error) {
        router.push('/login?logout=true')
      }
    }
  })
}
</script>

<style scoped>
.home-bg {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}

.text-brown-darken-3 {
  color: #3e2723 !important;
  letter-spacing: 2px;
}

.border-brown {
  border: 1px solid #d7ccc8 !important;
}

.transition-swing {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
