<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue' 
import { useCartStore } from '@/stores/cartStore'
import { useRouter, useRoute } from 'vue-router' 
import Swal from 'sweetalert2'
import { useUserStore } from '@/stores/userStore'

const cartStore = useCartStore()
const drawer = ref(false)
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const unreadCount = computed(() => userStore.unreadCount)

const menuItems = ref([
  { title: '書籍專區', to: '/dev/user/books', icon: 'mdi-book-open-page-variant' },
  { title: '會員中心', to: '/dev/user/user-menu', icon: 'mdi-account-circle' },
  { title: '讀者書評', to: '/dev/user/reviews', icon: 'mdi-comment-quote-outline' },
  { title: '歷史訂單', to: '/dev/user/orders', icon: 'mdi-history' },
  { title: '我的優惠券', to: '/dev/user/coupons', icon: 'mdi-ticket-percent' },
  { title: '關於我們', to: '/dev/user/about-us', icon: 'mdi-information' },
  { title: '後台系統', to: '/home', icon: 'mdi-information' },
])

const filteredMenuItems = computed(() => {
  return menuItems.value.filter((item) => {
    if (item.title === '後台系統') {
      return userStore.role === 'ADMIN' || userStore.role === 'SUPER_ADMIN'
    }
    return true
  })
})

const socialLinks = [
  { icon: 'mdi-facebook' },
  { icon: 'mdi-twitter' },
  { icon: 'mdi-instagram', link: 'https://www.instagram.com/penbrary.616/' },
]

watch(
  () => userStore.isLoggedIn,
  (isLoggedIn) => {
    if (isLoggedIn) {
      cartStore.fetchCartCount(); 
      cartStore.setCartCount(0);
    }
  }
)

const handleLogout = () => {
  Swal.fire({
    title: '確定要登出嗎？',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2e5c43',
    cancelButtonColor: '#aaa',
    confirmButtonText: '登出',
    cancelButtonText: '取消',
  }).then((result) => {
    if (result.isConfirmed) {
      userStore.logout()
      Swal.fire({
        icon: 'success',
        title: '登出成功',
        confirmButtonColor: '#2e5c43',
        timer: 1500,
        showConfirmButton: false,
      })
    }
  })
}

let notifySocket = null

const connectNotifySocket = () => {
  if (!userStore.userId || !userStore.isLoggedIn) return
  
  notifySocket = new WebSocket(`ws://localhost:8080/chat/${userStore.userId}`)
  
  notifySocket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      
      if (data && data.content){
          if (route.path !== '/dev/user/user-chat') {
            userStore.incrementUnreadCount() 
          }
        }
    } catch (e) {
    }
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUnreadCount();
    
    cartStore.fetchCartCount(); 
    
    connectNotifySocket();
    
    setInterval(() => {
       if (route.path !== '/dev/user/user-chat') {
         userStore.fetchUnreadCount();
       }
    }, 10000);
  }
});

onUnmounted(() => {
  if (notifySocket) notifySocket.close()
})
</script>

<template>
  <v-app theme="forestTheme">
    <v-app-bar color="primary" elevation="2" class="px-md-4">
      <div class="d-flex align-center" style="min-width: 200px">
        <v-app-bar-nav-icon
          variant="text"
          @click.stop="drawer = !drawer"
          class="d-md-none"
        ></v-app-bar-nav-icon>

        <v-toolbar-title
          class="font-weight-bold text-h5 cursor-pointer"
          @click="$router.push('/dev/user/home')"
        >
          <v-icon icon="mdi-book-open-variant" class="me-2"></v-icon>
          森林書屋
        </v-toolbar-title>
      </div>

      <v-spacer class="d-none d-md-block"></v-spacer>

      <div class="d-none d-md-flex justify-center flex-grow-1">
        <v-btn
          v-for="item in filteredMenuItems"
          :key="item.title"
          variant="text"
          class="mx-1 text-subtitle-1 font-weight-medium"
          :href="item.title === '後台系統' ? item.to : undefined"
          :target="item.title === '後台系統' ? '_blank' : undefined"
          :to="item.title === '後台系統' ? undefined : item.to"
        >
          {{ item.title }}
        </v-btn>
      </div>

      <v-spacer class="d-none d-md-block"></v-spacer>

      <div class="d-flex align-center justify-end" style="min-width: 200px">
        <v-btn icon="mdi-magnify" variant="text"></v-btn>

        <!-- 導覽列的購物車icon，點擊後跳轉到購物車頁面 -->
        <v-btn icon class="me-2" @click="$router.push({ name: 'cart' })">
          <v-badge
            :content="cartStore.cartCount"
            :model-value="cartStore.cartCount > 0"
            color="accent"
          >
            <v-icon icon="mdi-cart-outline"></v-icon>
          </v-badge>
        </v-btn>

          <div v-if="userStore.isLoggedIn">
            <v-menu min-width="200px" rounded>
              <template v-slot:activator="{ props }">
                <v-badge
                  :content="unreadCount"
                  :model-value="unreadCount > 0"
                  color="error"
                  dot
                  overlap
                  offset-x="3"
                  offset-y="3"
                >
                  <v-avatar 
  color="surface" 
  size="36" 
  class="cursor-pointer border-sm elevation-1" 
  v-bind="props"
>
  <v-img 
    v-if="userStore.img && userStore.img.trim() !== ''" 
    :src="userStore.img.startsWith('data:') || userStore.img.startsWith('http') 
          ? userStore.img 
          : `http://localhost:8080${userStore.img}`" 
    cover
  >
    <template v-slot:placeholder>
      <div class="d-flex align-center justify-center fill-height bg-primary text-white">
        {{ userStore.name?.charAt(0).toUpperCase() }}
      </div>
    </template>
  </v-img>

  <span v-else class="text-primary font-weight-bold">
    {{ userStore.name?.charAt(0).toUpperCase() }}
  </span>
</v-avatar>
                </v-badge>
              </template>

              <v-list>
                <v-list-item
                  prepend-icon="mdi-account-circle"
                  title="會員中心"
                  to="/dev/user/user-menu"
                >
                  <template v-slot:append v-if="unreadCount > 0">
                    <v-badge color="error" :content="unreadCount" inline></v-badge>
                  </template>
                </v-list-item>

                <v-divider></v-divider>

                <v-list-item
                  prepend-icon="mdi-logout"
                  title="登出"
                  @click="handleLogout"
                  class="text-error"
                ></v-list-item>
              </v-list>
            </v-menu>
          </div>

        <v-btn 
          v-else 
          color="white" 
          class="login-btn-cute px-6 font-weight-bold" 
          rounded="pill" 
          elevation="2"
          to="/dev/user/login"
        >
          <v-icon start icon="mdi-hand-wave-outline" class="jump-icon"></v-icon>
          登入
        </v-btn>
      </div>
    </v-app-bar>

    <!-- Mobile Navigation Drawer -->
    <v-navigation-drawer v-model="drawer" temporary location="left">
      <v-list>
        <v-list-item title="導覽選單" subtitle="BookStore"></v-list-item>
        <v-divider></v-divider>
        <v-list-item
          v-for="item in filteredMenuItems"
          :key="item.title"
          :prepend-icon="item.icon"
          :title="item.title"
          :href="item.title === '後台系統' ? item.to : undefined"
          :target="item.title === '後台系統' ? '_blank' : undefined"
          :to="item.title === '後台系統' ? undefined : item.to"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- Main Content (有 padding-top 避免被 App Bar 遮擋) -->
    <v-main :class="{ 'bg-transparent': $route.name === 'user-login' }">
      <v-container
        :class="{ 'py-6': $route.name !== 'user-login' }"
        :fluid="$route.name === 'user-login'"
        style="min-height: 80vh"
      >
        <router-view></router-view>
      </v-container>
    </v-main>

    <!-- Footer -->
    <v-footer color="primary" class="d-flex flex-column py-6">
      <div class="d-flex w-100 align-center px-4">
        <strong class="text-h6">與我們保持聯繫，獲取最新好書資訊！</strong>
        <v-spacer></v-spacer>
        <v-btn
          v-for="item in socialLinks"
          :key="item.icon"
          :icon="item.icon"
          class="mx-2"
          variant="text"
          :href="item.link"
          :target="item.link ? '_blank' : undefined"
          rel="noopener noreferrer"
        ></v-btn>
      </div>
      <v-divider class="w-100 my-4 border-opacity-25"></v-divider>
      <div class="text-center w-100 text-body-2">
        {{ new Date().getFullYear() }} — <strong>BookStore Inc.</strong>
      </div>
    </v-footer>
  </v-app>
</template>

<style scoped>
:deep(.v-main) {
  background-color: transparent !important;
}

:deep(.v-application--wrap) {
  background-color: #f2f2e9 !important;
}

:deep(.v-toolbar__content) {
  overflow: visible !important;
}

.v-btn {
  text-transform: none;
}

.login-btn-cute {
  background: linear-gradient(45deg, #ffffff 0%, #f9f9f0 100%) !important;
  color: #2e5c43 !important;
  border: 2px solid #2e5c43 !important;
  
  margin: 8px !important; 
  
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) !important;
  
  overflow: visible !important; 
}

.login-btn-cute:hover {
  transform: scale(1.05) rotate(-3deg); 
  box-shadow: 0 6px 15px rgba(46, 92, 67, 0.3) !important;
  background-color: #fdfdfd !important;
}

.jump-icon {
  transition: transform 0.3s ease;
}

.login-btn-cute:hover .jump-icon {
  animation: wave 0.5s infinite alternate;
}

@keyframes wave {
  from { transform: rotate(-10deg); }
  to { transform: rotate(20deg); }
}
</style>
