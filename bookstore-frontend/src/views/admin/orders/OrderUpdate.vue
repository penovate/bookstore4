<template>
  <div class="center-body">
    <div class="form-container">
      <h2>修改訂單資料</h2>

      <form @submit.prevent="handleSubmit" v-if="order.orderId">
        <div class="form-group">
          <label>訂單編號</label>
          <input type="text" :value="order.orderId" disabled />
        </div>
        
        <div class="form-group">
          <label>收件人</label>
          <input type="text" v-model="order.recipientAt" required />
        </div>

        <div class="form-group">
          <label>聯絡電話</label>
          <input type="text" v-model="order.phone" required />
        </div>

        <div class="form-group">
          <label>收件地址</label>
          <input type="text" v-model="order.address" required />
        </div>
        
        <div class="form-group">
          <label>付款方式</label>
          <select v-model="order.paymentMethod">
              <option value="貨到付款">貨到付款</option>
              <option value="信用卡">信用卡</option>
              <option value="轉帳">轉帳</option>
          </select>
        </div>

        <div class="form-group">
          <label>付款狀態</label>
          <select v-model="order.paymentStatus">
              <option value="未付款">未付款</option>
              <option value="已付款">已付款</option>
          </select>
        </div>

        <div class="form-group">
          <label>訂單狀態</label>
          <select v-model="order.orderStatus">
              <option value="待出貨">待出貨</option>
              <option value="已出貨">已出貨</option>
              <option value="已到貨">已到貨</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
              <option value="退款中">退款中</option>
              <option value="已退款">已退款</option>
              <option value="待處理">待處理</option>
          </select>
        </div>

        <div class="button-group">
          <button type="submit" class="submit-btn" :disabled="isSubmitting">更新</button>
          <button type="button" class="back-btn" @click="router.back()">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'
import Swal from 'sweetalert2'

const router = useRouter()
const route = useRoute()
const isSubmitting = ref(false)

const order = reactive({
  orderId: '',
  userId: '',
  recipientAt: '',
  phone: '',
  address: '',
  paymentMethod: '',
  paymentStatus: '',
  orderStatus: '',
  totalAmount: '' // Needed for update call if controller checks it, though typically read-only
})

const fetchOrder = async () => {
  try {
    const id = route.params.id
    const response = await axios.get(`http://localhost:8080/order/api/get?id=${id}`, { withCredentials: true })
    const data = response.data.order
    
    order.orderId = data.orderId
    order.userId = data.userId
    order.recipientAt = data.recipientAt
    order.phone = data.phone
    order.address = data.address
    order.paymentMethod = data.paymentMethod
    order.paymentStatus = data.paymentStatus
    order.orderStatus = data.orderStatus
    order.totalAmount = data.totalAmount

  } catch (error) {
    console.error('Failed to fetch order', error)
    Swal.fire('錯誤', '無法讀取訂單資料', 'error')
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  
  try {
    const params = new URLSearchParams()
    params.append('orderId', order.orderId)
    params.append('recipientAt', order.recipientAt)
    params.append('phone', order.phone)
    params.append('address', order.address)
    params.append('paymentMethod', order.paymentMethod)
    params.append('paymentStatus', order.paymentStatus)
    params.append('orderStatus', order.orderStatus)
    params.append('totalAmount', order.totalAmount)

    // Controller: @PostMapping("/order/update") -> processUpdateOrder(@ModelAttribute Orders order, Model model)
    // It maps params to Orders object fields.

    await axios.post('http://localhost:8080/order/api/update', params, {
      withCredentials: true
    })
    // Note: The controller returns a JSP view name if successful.
    // My previous plan was to create `/order/api/update`.
    // I missed creating `/order/api/update` in OrderController execution step 179!
    // I created `insert`, `cancel`, `restore`, `get`... 
    // Wait, let me check OrderController.java content again.
    
    // Step 179 added:
    // /order/api/activeList, /order/api/cancelledList, /order/api/get, /order/api/insert, /order/api/cancel, /order/api/restore.
    // IT DID NOT ADD /order/api/update!
    
    // I need to add /order/api/update in OrderController or use the existing /order/update (which returns JSP view, might cause issue if axios expects JSON or if returned HTML is large).
    // Ideally I should add /order/api/update.
    
    // For now I will code this component to use /order/api/update and I MUST add it to controller in next step.
      
    Swal.fire({
      icon: 'success',
      title: '更新成功',
      timer: 1500
    }).then(() => {
      router.back()
    })

  } catch (error) {
     // If I use existing controller it might return HTML 200 OK.
    Swal.fire('錯誤', '更新失敗', 'error')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.center-body {
  font-family: '微軟正黑體', 'Arial', sans-serif;
  background-color: #fcf8f0;
  display: flex;
  justify-content: center;
  padding: 40px 0;
  min-height: 100vh;
}

.form-container {
  width: 500px;
  max-width: 90%;
  padding: 30px;
  border: 1px solid #dcd5c7;
  border-radius: 6px;
  background-color: #ffffff;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

h2 {
  color: #7b5e47;
  text-align: center;
  margin-bottom: 25px;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #5d5d5d;
}

input[type="text"],
select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  background-color: #fefcf9;
  box-sizing: border-box;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 25px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.submit-btn {
  background-color: #a07d58;
  color: white;
}

.submit-btn:hover {
  background-color: #926f4e;
}

.back-btn {
  background-color: #e8e4dc;
  color: #4a4a4a;
}
</style>
