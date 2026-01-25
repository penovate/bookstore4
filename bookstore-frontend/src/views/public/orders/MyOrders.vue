<template>
  <div class="my-orders-page">
    <v-container class="py-10">
      <div class="text-center mb-8">
        <h2 class="forest-main-title">我的歷史訂單</h2>
        <v-divider class="mx-auto mt-4" length="60" thickness="4" color="primary"></v-divider>
      </div>

      <div v-if="loading" class="text-center py-10">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        <div class="mt-4 text-grey">載入訂單中...</div>
      </div>

      <div v-else-if="orders.length === 0" class="text-center py-10">
        <v-icon icon="mdi-file-document-outline" size="64" color="grey-lighten-2" class="mb-4"></v-icon>
        <div class="text-h6 text-grey">目前沒有任何訂單紀錄</div>
        <v-btn color="primary" class="mt-4" to="/dev/user/store">去逛逛</v-btn>
      </div>

      <div v-else>
        <!-- 搜尋欄 -->
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          label="搜尋訂單..."
          single-line
          hide-details
          variant="outlined"
          class="mb-6"
          density="compact"
        ></v-text-field>

        <v-data-iterator
          :items="orders"
          :items-per-page="itemsPerPage"
          :search="search"
        >
          <template v-slot:default="{ items }">
            <template v-for="item in items" :key="item.raw.orderId">
              <v-card 
                class="mb-6 forest-card rounded-lg cursor-pointer" 
                elevation="2"
                @click="openDetails(item.raw.orderId)"
                v-ripple
              >
                <v-card-title class="d-flex justify-space-between align-center bg-grey-lighten-4 py-3 px-4">
                  <div class="text-subtitle-1 font-weight-bold text-grey-darken-2">
                    訂單編號 #{{ item.raw.orderId }}
                  </div>
                  <v-chip :color="getStatusColor(item.raw.orderStatus)" size="small" label class="font-weight-bold">
                    {{ item.raw.orderStatus }}
                  </v-chip>
                </v-card-title>
                
                <v-card-text class="pt-4">
                  <v-row align="center">
                    <v-col cols="12" sm="6">
                      <div class="text-caption text-grey">下單日期</div>
                      <div class="text-body-1">{{ formatDate(item.raw.createdAt) }}</div>
                      
                      <div class="text-caption text-grey mt-2">配送方式</div>
                      <div class="text-body-1">{{ item.raw.deliveryMethod }}</div>
                    </v-col>
                    
                    <v-col cols="12" sm="6" class="text-right">
                      <div class="text-caption text-grey">實付金額</div>
                      <div class="text-h5 text-error font-weight-bold">${{ item.raw.finalAmount }}</div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </template>
          </template>

          <template v-slot:footer="{ page, pageCount, prevPage, nextPage }">
            <div class="d-flex align-center justify-center pa-4">
              <v-btn
                :disabled="page === 1"
                icon="mdi-arrow-left"
                density="comfortable"
                variant="text"
                rounded
                @click="prevPage"
              ></v-btn>

              <div class="mx-2 text-caption">
                第 {{ page }} 頁 / 共 {{ pageCount }} 頁
              </div>

              <v-btn
                :disabled="page === pageCount"
                icon="mdi-arrow-right"
                density="comfortable"
                variant="text"
                rounded
                @click="nextPage"
              ></v-btn>
            </div>
          </template>
        </v-data-iterator>

        <!-- 訂單明細 Dialog -->
        <v-dialog v-model="detailsDialog" max-width="700px">
          <v-card class="rounded-lg">
            <v-card-title class="bg-primary text-white d-flex justify-space-between align-center">
              <span>訂單明細 #{{ selectedOrderId }}</span>
              <v-btn icon="mdi-close" variant="text" color="white" @click="detailsDialog = false"></v-btn>
            </v-card-title>
            <v-card-text class="pa-4">
              <div v-if="detailsLoading" class="text-center py-4">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>
              <div v-else>
                <v-table density="compact">
                  <thead>
                    <tr>
                      <th class="text-left">書籍名稱</th>
                      <th class="text-center">單價</th>
                      <th class="text-center">數量</th>
                      <th class="text-right">小計</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in currentOrderItems" :key="item.orderItemId">
                      <td>{{ item.booksBean ? item.booksBean.bookName : '未知書籍' }}</td>
                      <td class="text-center">${{ item.price }}</td>
                      <td class="text-center">{{ item.quantity }}</td>
                      <td class="text-right font-weight-bold">${{ item.subtotal }}</td>
                    </tr>
                  </tbody>
                </v-table>
                <v-divider class="my-4"></v-divider>
                <div class="d-flex justify-end">
                   <div class="text-right">
                     <div class="text-subtitle-2 text-grey">運費: ${{ currentOrder?.shippingFee || 0 }}</div>
                     <div v-if="currentOrder?.discount > 0" class="text-subtitle-2 text-error">折扣: -${{ currentOrder?.discount }}</div>
                     <div class="text-h6 text-error font-weight-bold">總計: ${{ currentOrder?.finalAmount || 0 }}</div>
                   </div>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-dialog>
      </div>

    </v-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'
import orderService from '@/api/orderService.js'

const router = useRouter()
const orders = ref([])
const loading = ref(true)
const search = ref('')
const itemsPerPage = ref(5)
const detailsDialog = ref(false)
const selectedOrderId = ref(null)
const detailsLoading = ref(false)
const currentOrderItems = ref([])
const currentOrder = ref(null)
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit', 
    minute: '2-digit'
  })
}

const getStatusColor = (status) => {
  switch (status) {
    case '待出貨': return 'warning'
    case '已出貨': return 'info'
    case '已完成': return 'success'
    case '已取消': return 'error'
    default: return 'grey'
  }
}

const openDetails = async (orderId) => {
  selectedOrderId.value = orderId
  detailsDialog.value = true
  detailsLoading.value = true
  currentOrderItems.value = []
  currentOrder.value = null

  try {
    const response = await orderService.getOrderDetail(orderId)
    // 後端 OrderController 回傳的資料格式為: { order: {...}, items: [...] } 
    if (response.data) {
        currentOrder.value = response.data.order
        currentOrderItems.value = response.data.items || []
    }
  } catch (error) {
    console.error('Fetch detail error:', error)
    Swal.fire('錯誤', '無法讀取訂單明細', 'error')
  } finally {
    detailsLoading.value = false
  }
}

const fetchUserOrders = async () => {
    loading.value = true
    const token = localStorage.getItem('userToken')
    
    if (!token) {
        Swal.fire('提示', '請先登入', 'warning').then(() => {
            router.push('/login')
        })
        return
    }

    try {
        // 1. 解碼 Token 以取得用戶 ID 或獲取個人資料
        const base64Url = token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2) // 修正 Base64 解碼時可能出現的編碼問題
        }).join(''))
        const payload = JSON.parse(jsonPayload)

        if (!payload.sub) {
            throw new Error('Invalid Token')
        }

        // 2. 根據解碼出的帳號資訊，向後端獲取詳細的用戶資料（以取得真正的 userId）
        const userRes = await axios.get(`http://localhost:8080/api/data/get/${payload.sub}`, {
            withCredentials: true,
            headers: { Authorization: `Bearer ${token}` }
        })

        if (userRes.data && userRes.data.userId) {
            const userId = userRes.data.userId
            
            // 3. 取得真正的 userId 後，向後端請求該用戶的所有訂單
            const response = await orderService.getUserOrders(userId)
            orders.value = response.data || []
            
            // 將訂單依照日期降冪排序（最新的訂單排在最前面）
            orders.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

        } else {
            throw new Error('User Data Not Found')
        }

    } catch (error) {
        console.error('Fetch orders error:', error)
        Swal.fire('錯誤', '無法獲取訂單資料', 'error')
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchUserOrders()
})

</script>

<style scoped>
.my-orders-page {
  min-height: 100vh;
  background-color: #fcf8f0;
}

.forest-main-title {
  color: #2e5c43;
  font-size: 2.2rem;
  font-weight: 800;
  letter-spacing: 2px;
}

.forest-card {
  border-left: 5px solid #2e5c43 !important;
  transition: transform 0.2s;
}

.forest-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 92, 67, 0.15) !important;
}
</style>
