<template>
  <v-app>
    <v-main class="forest-home-bg">
      <v-container class="fill-height d-flex flex-column justify-center align-center" fluid>
        <div class="text-center mb-10">
          <h1 class="forest-main-title mb-2">網路書店後台系統</h1>
          <p class="forest-subtitle">核心管理選單</p>
        </div>

        <v-row class="menu-grid" justify="center">
          <v-col
            v-for="menu in menuItems"
            :key="menu.title"
            cols="12"
            sm="6"
            md="3"
            lg="3"
            class="pa-3"
          >
            <v-hover v-slot="{ isHovering, props }">
              <v-card
                v-bind="props"
                :elevation="isHovering ? 12 : 2"
                class="menu-tile d-flex flex-column align-center justify-center transition-swing rounded-xl"
                @click="handleMenuClick(menu)"
              >
                <v-icon :icon="menu.icon" size="42" color="primary" class="mb-3"></v-icon>
                <div class="menu-text font-weight-bold">
                  {{ menu.title }}
                </div>
                <div class="tile-indicator"></div>
              </v-card>
            </v-hover>
          </v-col>
        </v-row>

        <v-btn
          variant="tonal"
          color="primary"
          prepend-icon="mdi-logout"
          size="large"
          class="mt-12 px-10 rounded-lg font-weight-bold"
          @click="handleLogout"
        >
          登出系統
        </v-btn>

        <div class="text-caption text-grey-darken-1 mt-6">© 2026 BookStore Management System</div>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()

const menuItems = [
  { title: '會員管理', icon: 'mdi-account-group', path: '/dev/admin/users' },
  { title: '書籍管理', icon: 'mdi-book-open-page-variant', path: '/dev/admin/books' },
  { title: '訂單管理', icon: 'mdi-clipboard-list-outline', path: '/orders' },
  { title: '評價管理', icon: 'mdi-star-half-full', path: '/dev/admin/reviews' },
  { title: '進退貨管理', icon: 'mdi-swap-horizontal-bold', path: '/returns' },
  { title: '數據報表分析', icon: 'mdi-chart-bar', path: '/reports' },
  { title: '讀書會管理', icon: 'mdi-book-multiple', path: '/bookclubs' },
  { title: '網路書店前台', icon: 'mdi-storefront-outline', path: 'dev/user/home' },
]

const handleLogout = () => {
  Swal.fire({
    title: '確定要登出嗎？',
    text: '登出後將返回登入介面',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#2e5c43',
    cancelButtonColor: '#aaa',
    confirmButtonText: '登出',
    cancelButtonText: '取消',
  }).then(async (result) => {
    if (result.isConfirmed) {
      localStorage.clear()
      router.push('/login?logout=true')
    }
  })
}

const handleMenuClick = (menu) => {
  if (menu.title === '網路書店前台') {
    const url = menu.path.startsWith('http') ? menu.path : '/' + menu.path
    window.open(url, '_blank')
  } else {
    router.push(menu.path)
  }
}
</script>

<style scoped lang="scss">
.forest-home-bg {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}

.forest-main-title {
  color: #2e5c43;
  font-size: 2.8rem;
  font-weight: 900;
  letter-spacing: 4px;
}

.forest-subtitle {
  color: #5d7a66;
  font-size: 1.2rem;
  letter-spacing: 2px;
}

.menu-grid {
  width: 100%;
  max-width: 1200px;
}

.menu-tile {
  background-color: white !important;
  height: 160px;
  cursor: pointer;
  border: 1px solid rgba(46, 92, 67, 0.1) !important;
  position: relative;
  overflow: hidden;

  .menu-text {
    font-size: 1.1rem;
    color: #2e5c43;
  }

  .tile-indicator {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 6px;
    background-color: #2e5c43;
    opacity: 0.8;
    transform: translateY(6px);
    transition: transform 0.3s ease;
  }

  &:hover {
    .tile-indicator {
      transform: translateY(0);
    }
    .v-icon {
      transform: scale(1.1);
    }
  }
}

.transition-swing {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
</style>
