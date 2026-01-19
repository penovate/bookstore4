<template>
  <v-container>
    <div class="d-flex align-center mb-4">
      <h2 class="text-h4 font-weight-bold">優惠券管理</h2>
      <v-spacer></v-spacer>
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
      <v-data-table
        :headers="headers"
        :items="coupons"
        :search="search"
        :loading="loading"
        hover
      >
        <template v-slot:item.userBean="{ item }">
          <div class="d-flex align-center">
            <v-avatar size="24" color="secondary" class="mr-2">
              <span class="text-white text-caption">{{ (item.userBean?.userName || 'U').charAt(0) }}</span>
            </v-avatar>
            {{ item.userBean?.userName || 'Unknown' }}
          </div>
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip
            :color="item.status === 1 ? 'grey' : 'success'"
            class="font-weight-bold"
          >
            {{ item.status === 1 ? '已使用' : '未使用' }}
          </v-chip>
        </template>

        <template v-slot:item.discountAmount="{ item }">
          <span class="text-primary font-weight-bold">${{ item.discountAmount }}</span>
        </template>
        
        <template v-slot:item.minSpend="{ item }">
          <span>${{ item.minSpend }}</span>
        </template>

        <template v-slot:item.createdAt="{ item }">
          {{ formatDate(item.createdAt) }}
        </template>

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

onMounted(() => {
  fetchAllCoupons()
})
</script>
