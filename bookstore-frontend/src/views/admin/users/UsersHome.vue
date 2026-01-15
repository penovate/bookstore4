<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex justify-center" fluid>
        <v-card width="100%" max-width="500" class="pa-8 elevation-10" rounded="lg">
          <v-card-item class="text-center">
            <v-card-title class="text-h4 font-weight-bold text-brown-darken-2 mb-2">
              網路書店後台系統
            </v-card-title>
            <v-card-subtitle class="text-h6 text-brown-lighten-1"> 會員管理中心 </v-card-subtitle>
          </v-card-item>

          <v-divider class="my-6"></v-divider>

          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-hover v-slot="{ isHovering, props }">
                  <v-btn
                    v-bind="props"
                    block
                    height="70"
                    color="brown"
                    :elevation="isHovering ? 8 : 2"
                    class="text-h6 font-weight-bold rounded-lg mb-4"
                    prepend-icon="mdi-account-search"
                    @click="router.push('/dev/admin/users')"
                  >
                    查詢所有會員資料
                  </v-btn>
                </v-hover>
              </v-col>

              <v-col cols="12">
                <v-hover v-slot="{ isHovering, props }">
                  <v-btn
                    v-bind="props"
                    block
                    height="60"
                    variant="outlined"
                    color="brown-darken-1"
                    :elevation="isHovering ? 4 : 0"
                    class="text-subtitle-1 rounded-lg"
                    prepend-icon="mdi-home"
                    @click="router.push('/home')"
                  >
                    返回系統首頁
                  </v-btn>
                </v-hover>
              </v-col>
            </v-row>
          </v-card-text>

          <v-divider class="mt-6 mb-2"></v-divider>

          <v-card-actions class="justify-center">
            <span class="text-caption text-grey">請點選上方按鈕開始操作系統</span>
          </v-card-actions>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.fill-height {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}

.text-brown-darken-2 {
  color: #5d4037 !important;
  letter-spacing: 2px !important;
}

.v-btn {
  letter-spacing: 1.5px;
  transition: transform 0.2s ease-in-out;
}

.v-btn:hover {
  transform: scale(1.02);
}
</style>
