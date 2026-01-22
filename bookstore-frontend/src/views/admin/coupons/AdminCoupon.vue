<template>
  <v-container>
    <div class="d-flex align-center mb-4">
      <h2 class="text-h4 font-weight-bold">優惠券管理</h2>
      <!-- v-spacer類似彈簧框，將前後的組件推開占滿剩餘空間，即把h2文字推到左邊、搜尋框推到右邊，然後站滿中間 -->
      <v-spacer></v-spacer>
      <!-- 搜尋框的v-model雙向綁定、設定放大鏡圖案、label文字、強制輸入單行顯示、隱藏細節文字、設計緊湊密度、最大寬度限制 -->
      <!-- v-text-field 類似input輸入框，但多了更多視覺效果與實用功能 -->
      <v-text-field
        v-model="search"
        prepend-inner-icon="mdi-magnify" 
        label="搜尋優惠碼或使用者ID"
        single-line
        hide-details
        density="compact"
        style="max-width: 300px"
      ></v-text-field>
    </div>

    <v-card class="elevation-2 rounded-lg">
      <!-- 冒號為v-bind，動態綁定屬性 -->
      <!-- headers是<v-data-table>內建的接收「欄位定義」的屬性名稱，不能換 -->
      <!-- items是<v-data-table>內建的接收「資料陣列」的屬性名稱，不能換 -->
      <!-- headers是<v-data-table>內建的接收「過濾關鍵字」的屬性名稱，不能換 -->
      <!-- loading是<v-data-table>內建的接收「載入狀態」的屬性名稱(true、false)，不能換 -->
      <!-- 一定要用這四個特定屬性寫，亂命名跑不起來 -->
      <v-data-table
        :headers="headers"
        :items="coupons"
        :search="search"
        :loading="loading"
        hover
      >
      <!-- 使用者 -->
        <template v-slot:item.userBean="{ item }">
          <div class="d-flex align-center">
            <!-- v-avatar大頭貼的圓形容器元件 -->
            <v-avatar size="24" color="secondary" class="mr-2">
              <span class="text-white text-caption">{{ (item.userBean?.userName || 'U').charAt(0) }}</span>
            </v-avatar>
            {{ item.userBean?.userName || 'Unknown' }}
          </div>
        </template>

       <!-- 狀態 -->
        <template v-slot:item.status="{ item }">
          <v-chip
            :color="item.status === 1 ? 'grey' : 'success'"
            class="font-weight-bold"
          >
            {{ item.status === 1 ? '已使用' : '未使用' }}
          </v-chip>
        </template>

        <!-- 折扣金額 -->
        <template v-slot:item.discountAmount="{ item }">
          <span class="text-primary font-weight-bold">${{ item.discountAmount }}</span>
        </template>
        
        <!-- 低消門檻 -->
        <template v-slot:item.minSpend="{ item }">
          <span>${{ item.minSpend }}</span>
        </template>

        <!-- 領取時間 -->
        <template v-slot:item.createdAt="{ item }">
          {{ formatDate(item.createdAt) }}
        </template>

        <!-- 使用時間 -->
        <template v-slot:item.usedAt="{ item }">
          {{ item.usedAt ? formatDate(item.usedAt) : '-' }}
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import couponService from '@/api/couponService.js'

const search = ref('')
const loading = ref(true)
const coupons = ref([])

// sortable:是否可以進行排序
const headers = [
  { title: 'ID', key: 'couponId', sortable: true },
  { title: '使用者', key: 'userBean', sortable: false },
  { title: '優惠碼', key: 'couponCode', sortable: true },
  { title: '折扣金額', key: 'discountAmount', sortable: true },
  { title: '低消門檻', key: 'minSpend', sortable: true },
  { title: '狀態', key: 'status', sortable: true },
  { title: '領取時間', key: 'createdAt', sortable: true },
  { title: '使用時間', key: 'usedAt', sortable: true },
]

const fetchAllCoupons = async () => {
  try {
    const response = await couponService.getAllCouponsAdmin()
    if (response.data.success) {
      coupons.value = response.data.coupons || []
    }
  } catch (error) {
    console.error('Failed to fetch coupons', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

// 掛載，請執行裡面的動作
onMounted(() => {
  fetchAllCoupons()
})
</script>
