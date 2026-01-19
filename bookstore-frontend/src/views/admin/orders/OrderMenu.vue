<template>
  <v-container class="fill-height" fluid>
    <div class="menu-container w-100">
      <div class="text-center mb-10">
        <h2 class="forest-main-title mb-2">後台訂單與優惠券管理系統</h2>
        <p class="forest-subtitle">訂單管理與優惠券管理核心選單</p>
      </div>

      <v-row justify="center" class="gap-4">
        <v-col cols="12" md="4" lg="3">
          <v-hover v-slot="{ isHovering, props }">
            <v-card
              v-bind="props"
              :elevation="isHovering ? 12 : 2"
              class="menu-card d-flex flex-column align-center justify-center pa-6 rounded-xl transition-swing"
              @click="router.push({ name: 'orderList' })"
            >
              <div class="icon-wrapper mb-4">
                <span class="emoji-icon">📋</span>
              </div>
              <h3 class="text-h5 font-weight-bold text-primary mb-2">訂單管理</h3>
              <p class="text-grey-darken-1 text-center">查詢、修改、新增與取消訂單</p>
              <div class="card-indicator"></div>
            </v-card>
          </v-hover>
        </v-col>

        <v-col cols="12" md="4" lg="3">
          <v-hover v-slot="{ isHovering, props }">
            <v-card
              v-bind="props"
              :elevation="isHovering ? 12 : 2"
              class="menu-card d-flex flex-column align-center justify-center pa-6 rounded-xl transition-swing"
              @click="router.push({ name: 'admin-coupons' })"
            >
              <div class="icon-wrapper mb-4">
                <span class="emoji-icon">🎫</span>
              </div>
              <h3 class="text-h5 font-weight-bold text-primary mb-2">優惠券管理</h3>
              <p class="text-grey-darken-1 text-center">新增、查詢與管理優惠券</p>
              <div class="card-indicator"></div>
            </v-card>
          </v-hover>
        </v-col>
      </v-row>

      <div class="d-flex justify-center mt-12">
        <v-btn
          color="secondary"
          variant="tonal"
          size="large"
          prepend-icon="mdi-arrow-left"
          class="rounded-lg font-weight-bold px-8"
          @click="router.push({ name: 'home' })"
        >
          回後台首頁
        </v-btn>
      </div>
    </div>
  </v-container>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.forest-main-title {
  color: #2e5c43;
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: 2px;
}

.forest-subtitle {
  color: #5d7a66;
  font-size: 1.1rem;
  letter-spacing: 1px;
}

.menu-card {
  height: 280px;
  cursor: pointer;
  border: 1px solid rgba(46, 92, 67, 0.1);
  position: relative;
  overflow: hidden;
  background-color: white;
}

.icon-wrapper {
  background-color: #f1f8e9;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.emoji-icon {
  font-size: 40px;
}

.card-indicator {
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

.menu-card:hover .card-indicator {
  transform: translateY(0);
}

.menu-card:hover .icon-wrapper {
  transform: scale(1.1) rotate(5deg);
  background-color: #dcedc8;
}

.menu-card:hover {
  border-color: #2e5c43;
}

.transition-swing {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}
</style>
