<template>
  <div class="center-body">
    <div class="form-container">
      <h2>新增訂單明細</h2>

      <div class="form-group">
          <label>訂單編號: {{ orderId }}</label>
      </div>

      <form @submit.prevent="handleSubmit">
        <h3>商品資訊</h3>
        <div class="items-container">
            <div v-for="(item, index) in items" :key="index" class="item-row">
                <div class="item-header">
                    <h4>商品 {{ index + 1 }}</h4>
                    <button type="button" class="remove-btn" @click="removeItem(index)" v-if="items.length > 1">移除</button>
                </div>
                
                <div class="form-group">
                    <label>書籍ID:</label>
                    <input type="text" v-model="item.bookId" @change="fetchBookInfo(item)" placeholder="輸入書籍 ID" required>
                </div>

                <div class="form-group">
                    <label>書籍名稱:</label>
                    <input type="text" v-model="item.bookName" readonly class="readonly-input">
                </div>

                <div class="form-group">
                    <label>單價:</label>
                    <input type="number" v-model="item.price" readonly class="readonly-input">
                </div>

                <div class="form-group">
                    <label>數量:</label>
                    <input type="number" v-model="item.quantity" min="1" required>
                </div>
            </div>
        </div>

        <button type="button" class="add-btn" @click="addItem">+ 增加一筆商品</button>
        
        <div class="button-group">
            <button type="submit" class="submit-btn">確認新增</button>
            <button type="button" class="cancel-btn" @click="router.back()">取消</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Swal from 'sweetalert2'
import orderService from '@/api/orderService.js'

const router = useRouter()
const route = useRoute()
const orderId = ref(route.params.id)

const items = ref([
    { bookId: '', bookName: '', price: '', quantity: 1 }
])

const addItem = () => {
    items.value.push({ bookId: '', bookName: '', price: '', quantity: 1 })
}

const removeItem = (index) => {
    items.value.splice(index, 1)
}

const fetchBookInfo = async (item) => {
    if (!item.bookId) return
    
    try {
        const response = await orderService.getBookInfo(item.bookId)
        const book = response.data
        if (book && book.bookId) {
            item.bookName = book.bookName
            item.price = book.price
        } else {
            Swal.fire('錯誤', '找不到此書籍ID', 'error')
            item.bookName = ''
            item.price = ''
            item.bookId = ''
        }
    } catch (error) {
        console.error(error)
        Swal.fire('錯誤', '查詢書籍失敗', 'error')
    }
}

const handleSubmit = async () => {
    try {
        const response = await orderService.addOrderItems(orderId.value, items.value)
        
        if (response.data === 'success') {
             Swal.fire({
                icon: 'success',
                title: '新增成功',
                timer: 1500
            }).then(() => {
                router.push({ name: 'orderDetail-admin', params: { id: orderId.value } })
            })
        } else {
            Swal.fire('錯誤', '新增失敗: ' + response.data, 'error')
        }
    } catch (error) {
        Swal.fire('錯誤', '系統錯誤', 'error')
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
  width: 600px;
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
input[type="number"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #d0c8b9;
  border-radius: 4px;
  background-color: #fefcf9;
  box-sizing: border-box;
}

.readonly-input {
    background-color: #eee !important;
    color: #555;
    cursor: not-allowed;
}

.item-row {
    border: 1px solid #dcd5c7;
    background-color: #fffaf0;
    padding: 15px;
    margin-bottom: 15px;
    border-radius: 4px;
    position: relative;
}

.item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    border-bottom: 1px dashed #ccc;
    padding-bottom: 5px;
}

h4 { margin: 0; color: #4a4a4a; }

.remove-btn {
    background-color: #d89696;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}

.add-btn {
    width: 100%;
    padding: 10px;
    background-color: #9fb89e;
    color: #4a4a4a;
    border: none;
    border-radius: 4px;
    font-weight: bold;
    cursor: pointer;
    margin-bottom: 20px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 25px;
}

button.submit-btn, button.cancel-btn {
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
