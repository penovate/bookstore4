<template>
  <div class="center-body">
    <div class="form-container">
      <h2>修改訂單明細</h2>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>訂單明細編號</label>
          <input type="text" :value="itemId" disabled class="readonly-input"/>
        </div>

        <div class="form-group">
          <label>書籍 ID</label>
          <input type="text" :value="bookId" disabled class="readonly-input" />
        </div>

        <div class="form-group">
          <label>單價</label>
          <input type="number" v-model="price" required />
        </div>
        
         <div class="form-group">
          <label>數量</label>
          <input type="number" v-model="quantity" min="1" required />
        </div>

        <div class="button-group">
          <button type="submit" class="submit-btn">確認修改</button>
          <button type="button" class="cancel-btn" @click="router.back()">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Swal from 'sweetalert2'
import orderService from '@/api/orderService.js'

const router = useRouter()
const route = useRoute()

const itemId = route.params.itemId
// Ideally fetch fresh data, but JSP example passed params via URL. 
// We will use query params as initial state for simplicity as per JSP logic.
const orderId = route.query.orderId
const bookId = route.query.bookId
const price = ref(route.query.price)
const quantity = ref(route.query.quantity)

const handleSubmit = async () => {
  try {
    const response = await orderService.updateOrderItem(
        orderId, 
        itemId, 
        price.value, 
        quantity.value, 
        bookId // Pass bookId to ensure it's not lost
    )

    if (response.data === 'success') {
       Swal.fire({
          icon: 'success',
          title: '更新成功',
          timer: 1500
        }).then(() => {
          router.push({ name: 'orderDetail-admin', params: { id: orderId } })
        })
    } else {
        Swal.fire('錯誤', '更新失敗: ' + response.data, 'error')
    }

  } catch (error) {
    Swal.fire('錯誤', '更新失敗', 'error')
  }
}
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

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  background-color: #fefcf9;
  box-sizing: border-box;
}

.readonly-input {
    background-color: #eee !important;
    cursor: not-allowed;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 25px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  min-width: 100px;
}

.submit-btn { background-color: #a07d58; color: white; }
.cancel-btn { background-color: #e8e4dc; color: #4a4a4a; }
</style>
