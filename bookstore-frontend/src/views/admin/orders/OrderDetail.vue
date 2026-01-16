<template>
  <div class="page-wrapper">
    <div class="header-section mb-6 text-left">
      <h2 class="forest-main-title">訂單明細</h2>
    </div>

    <v-btn
      color="secondary"
      variant="tonal"
      prepend-icon="mdi-arrow-left"
      class="mb-6 font-weight-bold"
      @click="router.push({ name: 'orderList' })"
    >
      返回列表
    </v-btn>

    <div v-if="loading" class="d-flex justify-center my-10">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <v-card v-else class="forest-card pa-6 rounded-xl">
      <!-- 訂單基本資訊 -->
      <v-row>
        <v-col cols="12" md="6">
          <h3 class="text-h5 font-weight-bold text-primary mb-4">
            <v-icon icon="mdi-information-outline" class="mr-2"></v-icon>
            基本資訊
          </h3>
          <v-list density="compact" class="info-list">
            <v-list-item>
              <template v-slot:prepend><span class="label">訂單編號：</span></template>
              <span class="value">#{{ order.orderId }}</span>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">訂購日期：</span></template>
              <span class="value">{{ formatDate(order.createdAt) }}</span>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">目前狀態：</span></template>
              <v-chip :color="getStatusColor(order.orderStatus)" size="small" variant="flat" class="text-white font-weight-bold">
                {{ order.orderStatus }}
              </v-chip>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">付款方式：</span></template>
              <span class="value">
                {{ order.paymentMethod === 'CREDIT_CARD' ? '信用卡' : order.paymentMethod === 'COD' ? '貨到付款' : order.paymentMethod }}
              </span>
            </v-list-item>
             <v-list-item>
              <template v-slot:prepend><span class="label">配送方式：</span></template>
              <span class="value">
                 {{ order.deliveryMethod === 'HOME' ? '宅配' : '超商取貨' }}
              </span>
            </v-list-item>
          </v-list>
        </v-col>

        <v-col cols="12" md="6">
          <h3 class="text-h5 font-weight-bold text-primary mb-4">
            <v-icon icon="mdi-account-outline" class="mr-2"></v-icon>
            收件資訊
          </h3>
           <v-list density="compact" class="info-list">
            <v-list-item>
              <template v-slot:prepend><span class="label">訂購人：</span></template>
              <div class="d-flex align-center">
                 <v-avatar size="24" color="secondary" class="mr-2">
                   <span class="text-white text-caption">{{ (order.userBean?.userName || 'U').charAt(0) }}</span>
                 </v-avatar>
                <span class="value">{{ order.userBean ? order.userBean.userName : 'Unknown' }}</span>
              </div>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">收件人：</span></template>
              <span class="value">{{ order.recipientName || order.recipientAt }}</span> <!-- Support both fields if schema varies -->
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">聯絡電話：</span></template>
              <span class="value">{{ order.phone }}</span>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend><span class="label">收件地址：</span></template>
              <span class="value">{{ order.address }}</span>
            </v-list-item>
          </v-list>
        </v-col>
      </v-row>

      <v-divider class="my-6 border-opacity-50" color="primary"></v-divider>

      <!-- 訂單品項 -->
      <div class="d-flex justify-space-between align-center mb-4">
        <h3 class="text-h5 font-weight-bold text-primary mb-0">
          <v-icon icon="mdi-book-open-variant" class="mr-2"></v-icon>
          訂購商品
        </h3>
        <v-btn
           v-if="order.orderStatus !== '已取消' && order.orderStatus !== '已退款'"
           color="primary"
           prepend-icon="mdi-plus"
           variant="elevated"
           size="small"
           @click="goToAddItem"
        >
           新增書籍
        </v-btn>
      </div>
      
      <v-table class="forest-table mb-6">
        <thead>
          <tr>
            <th class="text-left font-weight-bold text-primary">書籍名稱</th>
            <th class="text-center font-weight-bold text-primary">單價</th>
            <th class="text-center font-weight-bold text-primary">數量</th>
            <th class="text-right font-weight-bold text-primary">小計</th>
            <th class="text-center font-weight-bold text-primary">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in orderItems" :key="item.itemId">
            <td>{{ item.booksBean ? item.booksBean.bookName : '未知書籍' }}</td>
            <td class="text-center">${{ item.price }}</td>
            <td class="text-center">{{ item.quantity }}</td>
            <td class="text-right font-weight-bold">${{ item.price * item.quantity }}</td>
            <td class="text-center">
               <v-btn
                 icon="mdi-pencil"
                 size="x-small"
                 color="info"
                 variant="text"
                 class="mr-2"
                 @click="goToEditItem(item)"
                 :disabled="order.orderStatus === '已取消'"
               ></v-btn>
               <v-btn
                 icon="mdi-delete"
                 size="x-small"
                 color="error"
                 variant="text"
                 @click="deleteItem(item)"
                 :disabled="order.orderStatus === '已取消'"
               ></v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

      <!-- 總計區域 -->
      <v-row justify="end">
        <v-col cols="12" md="4" lg="3">
          <v-card variant="tonal" color="primary" class="pa-4 rounded-lg">
            <div class="d-flex justify-space-between mb-2">
               <span class="text-grey-darken-2">商品小計</span>
               <span class="font-weight-bold">${{ order.totalAmount }}</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
               <span class="text-grey-darken-2">運費</span>
               <span class="font-weight-bold">${{ order.shippingFee }}</span>
            </div>
            <v-divider class="my-2"></v-divider>
            <div class="d-flex justify-space-between text-h6 text-primary font-weight-bold">
               <span>總金額</span>
               <span>${{ order.finalAmount }}</span>
            </div>
          </v-card>
        </v-col>
      </v-row>
      
      <!-- 底部按鈕 -->
      <div class="d-flex justify-end gap-3 mt-6">
         <v-btn
           v-if="order.orderStatus !== '已取消'"
           color="error"
           variant="outlined"
           prepend-icon="mdi-close"
           @click="handleCancel"
         >
           取消訂單
         </v-btn>
      </div>

    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import orderService from '@/api/orderService.js'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id

const order = ref({})
const orderItems = ref([])
const loading = ref(true)



const formatDate = (dateArr) => {
  if (!dateArr) return ''
  if (Array.isArray(dateArr)) {
    const [y, m, d, h, min] = dateArr
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}`
  }
  return dateArr
}

const getStatusColor = (status) => {
    if (status === '已付款' || status === '完成') return 'success'
    if (status === '待付款' || status === '處理中') return 'warning'
    if (status === '已取消') return 'error'
    return 'grey'
}

const fetchOrderDetail = async () => {
  try {
    const response = await orderService.getOrderDetail(orderId)
    order.value = response.data.order
    orderItems.value = response.data.items || []
  } catch (error) {
    console.error('Fetch detail failed', error)
    Swal.fire('錯誤', '無法讀取訂單明細', 'error')
    router.push({ name: 'orderList' })
  } finally {
    loading.value = false
  }
}

// --- Item Management Logic ---

const goToAddItem = () => {
    router.push({ name: 'orderAddItem', params: { id: orderId } })
}

const goToEditItem = (item) => {
    router.push({
        name: 'orderItemUpdate',
        params: { itemId: item.orderItemId },
        query: {
            orderId: orderId,
            bookId: item.booksBean?.bookId,
            price: item.price,
            quantity: item.quantity
        }
    })
}

const deleteItem = async (item) => {
    Swal.fire({
        title: '確定刪除此明細？',
        text: item.booksBean?.bookName,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        confirmButtonText: '刪除'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await orderService.deleteOrderItem(orderId, item.orderItemId)
                 Swal.fire('成功', '已刪除', 'success')
                 fetchOrderDetail()
            } catch (e) {
                 Swal.fire('錯誤', '刪除失敗', 'error')
            }
        }
    })
}

const handleCancel = () => {
    Swal.fire({
        title: '確定要取消訂單嗎？',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#aaa',
        confirmButtonText: '確認取消',
        cancelButtonText: '返回'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                await orderService.cancelOrder(orderId)
                Swal.fire('成功', '訂單已取消', 'success')
                fetchOrderDetail() // Refresh
            } catch (error) {
                Swal.fire('錯誤', '取消失敗', 'error')
            }
        }
    })
}

onMounted(() => {
  if (orderId) {
    fetchOrderDetail()
  } else {
    router.push({ name: 'orderList' })
  }
})
</script>

<style scoped lang="scss">


.forest-main-title {
  color: #2e5c43;
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 0;
}

.forest-card {
   background-color: white !important;
   border: 1px solid #e0e0e0;
   box-shadow: 0 4px 20px rgba(0,0,0,0.05) !important;
}

.info-list {
  background-color: transparent;
  
  .v-list-item {
    padding-left: 0;
  }
  
  .label {
    min-width: 80px;
    display: inline-block;
    color: #666;
    font-weight: bold;
  }
  
  .value {
    color: #333;
    font-size: 1rem;
  }
}

.forest-table {
   background-color: #fafafa;
   border-radius: 8px;
   overflow: hidden;
   
   th {
     background-color: #f1f8e9 !important;
     font-size: 0.95rem;
   }
}
</style>
