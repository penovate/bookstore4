<template>
  <div class="center-body">
    <div class="checkout-container">
      <h2>結帳確認</h2>

      <div v-if="loading" class="loading-state">載入中...</div>

      <div v-else class="checkout-content">
        <!-- 左側：填寫資料 -->
        <div class="form-section">
          <!-- 1. 訂購人資料 -->
          <div class="section-block">
            <h3>訂購人資料</h3>
            <div class="form-group">
              <label>姓名</label>
              <input type="text" :value="user.userName" disabled class="readonly-input" />
            </div>
            <div class="form-group">
              <label>電話</label>
              <input type="text" :value="user.userPhone" disabled class="readonly-input" />
            </div>
          </div>

          <!-- 2. 收件人資料 -->
          <div class="section-block">
            <div class="section-header">
              <h3>收件人資訊</h3>
              <button class="btn-copy" @click="copySubscriberInfo">同訂購人</button>
            </div>

            <div class="form-group">
              <label>收件人姓名 <span class="required">*</span></label>
              <input type="text" v-model="form.recipientName" placeholder="請輸入收件人姓名" />
            </div>
            <div class="form-group">
              <label>收件人電話 <span class="required">*</span></label>
              <input type="text" v-model="form.recipientPhone" placeholder="請輸入收件人電話" />
            </div>
          </div>

          <!-- 3. 配送方式 -->
          <div class="section-block">
            <h3>配送方式 <span class="required">*</span></h3>
            <div class="radio-group">
              <label class="radio-label" :class="{ active: form.deliveryMethod === '宅配到府' }">
                <input type="radio" v-model="form.deliveryMethod" value="宅配到府" />
                宅配到府
              </label>
              <label class="radio-label" :class="{ active: form.deliveryMethod === '超商取貨' }">
                <input type="radio" v-model="form.deliveryMethod" value="超商取貨" />
                超商取貨
              </label>
            </div>

            <!-- 動態顯示地址/門市 -->
            <div class="delivery-detail" v-if="form.deliveryMethod === '宅配到府'">
              <div class="form-group">
                <label>收件地址 <span class="required">*</span></label>
                <input type="text" v-model="form.address" placeholder="請輸入完整收件地址" />
              </div>
              <p class="shipping-note">宅配到府未滿 1,000 元，將酌收訂單處理費 50 元</p>
            </div>

            <div class="delivery-detail" v-if="form.deliveryMethod === '超商取貨'">
              <div class="form-group">
                <label>超商門市 <span class="required">*</span></label>
                <div style="margin-bottom: 10px">
                  <label><input type="radio" v-model="cvsType" value="UNIMART" /> 7-11</label>
                  <label style="margin-left: 10px"
                    ><input type="radio" v-model="cvsType" value="FAMI" /> 全家</label
                  >
                  <label style="margin-left: 10px"
                    ><input type="radio" v-model="cvsType" value="HILIFE" /> 萊爾富</label
                  >
                </div>
                <div class="store-selector">
                  <input
                    type="text"
                    v-model="form.address"
                    placeholder="請選擇您的配送門市"
                    readonly
                  />
                  <button class="btn-select-store" @click="selectStore">請選擇門市</button>
                </div>
              </div>
              <p class="shipping-note">超商取貨未滿 350 元，將酌收訂單處理費 50 元</p>
            </div>
          </div>

          <!-- 4. 付款方式 -->
          <div class="section-block">
            <h3>付款方式 <span class="required">*</span></h3>
            <div class="payment-options">
              <label v-if="canUseCreditCard">
                <input type="radio" v-model="form.paymentMethod" value="信用卡" />
                信用卡付款
              </label>
              <!-- 超商取貨才能使用貨到付款 -->
              <label v-if="canUseCOD">
                <input type="radio" v-model="form.paymentMethod" value="貨到付款" />
                貨到付款
              </label>
            </div>
          </div>

          <!-- 5. 優惠券 -->
          <div class="section-block">
            <h3>優惠券</h3>
            <div v-if="coupons.length > 0">
              <div class="radio-group" style="flex-direction: column; gap: 10px">
                <label class="radio-label" :class="{ active: selectedCouponId === null }">
                  <input type="radio" :value="null" v-model="selectedCouponId" />
                  不使用優惠券
                </label>
                <label
                  v-for="coupon in coupons"
                  :key="coupon.couponId"
                  class="radio-label"
                  :class="{
                    active: selectedCouponId === coupon.couponId,
                    'disabled-coupon': cartTotal < coupon.minSpend,
                  }"
                >
                  <input
                    type="radio"
                    :value="coupon.couponId"
                    v-model="selectedCouponId"
                    :disabled="cartTotal < coupon.minSpend"
                  />
                  <div class="d-flex justify-space-between w-100 align-center">
                    <span>
                      <strong class="text-primary">${{ coupon.discountAmount }} 折價券</strong>
                      <span class="text-caption text-grey ml-2"
                        >(滿 ${{ coupon.minSpend }} 可用)</span
                      >
                    </span>
                    <span v-if="cartTotal < coupon.minSpend" class="text-caption text-error"
                      >未達門檻</span
                    >
                  </div>
                </label>
              </div>
            </div>
            <div v-else class="text-grey pa-2">無可用優惠券</div>
          </div>

          <!-- 6. 訂單明細 -->
          <div class="section-block">
            <h3>訂單明細</h3>
            <div class="order-items-list">
              <div v-for="item in cartItems" :key="item.cartId" class="order-item">
                <div class="item-image">
                  <img
                    :src="
                      item.booksBean && item.booksBean.bookImageBean
                        ? `http://localhost:8080/upload-images/${item.booksBean.bookImageBean.imageUrl}`
                        : '/no-image.png'
                    "
                    alt="Book Cover"
                  />
                </div>
                <div class="item-info">
                  <div class="item-title">
                    {{ item.booksBean ? item.booksBean.bookName : '未知書籍' }}
                  </div>
                  <div class="item-meta">
                    <span
                      >${{ item.booksBean ? item.booksBean.price : 0 }} x {{ item.quantity }}</span
                    >
                    <span class="item-subtotal"
                      >小計: ${{
                        (item.booksBean ? item.booksBean.price : 0) * item.quantity
                      }}</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右側：訂單摘要 -->
        <div class="summary-section">
          <div class="summary-card">
            <h3>訂單摘要</h3>
            <div class="summary-row">
              <span>商品小計</span>
              <span>$ {{ cartTotal }}</span>
            </div>
            <div class="summary-row">
              <span>運費</span>
              <span>$ {{ shippingFee }}</span>
            </div>
            <div class="summary-row" v-if="discountAmount > 0">
              <span>折價券</span>
              <span class="text-error">- $ {{ discountAmount }}</span>
            </div>
            <div class="divider"></div>
            <div class="summary-row total-row">
              <span>應付金額</span>
              <span>$ {{ finalAmount }}</span>
            </div>

            <button class="btn-checkout" @click="submitOrder" :disabled="isSubmitting">
              {{ form.paymentMethod === '信用卡' ? '前往付款' : '提交訂單' }}
            </button>
            <button class="btn-back" @click="router.push({ name: 'cart' })">返回購物車</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- Hidden Form Container -->
  <div id="ecpay-form-container" style="display: none"></div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import orderService from '@/api/orderService.js'
import couponService from '@/api/couponService.js'

const router = useRouter()
const loading = ref(true)
const isSubmitting = ref(false)

const user = ref({ userName: '', userPhone: '' })
const cartTotal = ref(0) // 假設從後端取得購物車總額
const cartItemsCount = ref(0)
const cartItems = ref([])
const coupons = ref([])
const selectedCouponId = ref(null)

const form = ref({
  recipientName: '',
  recipientPhone: '',
  deliveryMethod: '宅配到府', // 預設值
  address: '',
  paymentMethod: '',
})

// === 獲取資料 ===
const initData = async () => {
  try {
    const cartRes = await orderService.getCartItems()

    if (!cartRes.data.success) {
      Swal.fire('錯誤', cartRes.data.message || '請先登入', 'error').then(() => {
        router.push('/login')
      })
      return
    }

    cartTotal.value = cartRes.data.totalAmount
    cartItems.value = cartRes.data.cartItems || []
    cartItemsCount.value = (cartRes.data.cartItems || []).length

    if (cartItemsCount.value === 0) {
      Swal.fire('提示', '購物車是空的', 'info').then(() => router.push({ name: 'bookStore' }))
      return
    }

    // 2. 獲取使用者資料
    const token = localStorage.getItem('userToken')
    if (token) {
      try {
        const base64Url = token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(
          atob(base64)
            .split('')
            .map(function (c) {
              return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
            })
            .join(''),
        )
        const payload = JSON.parse(jsonPayload)

        if (payload.sub) {
          const userRes = await axios.get(`http://localhost:8080/api/data/get/${payload.sub}`, {
            withCredentials: true,
            headers: { Authorization: `Bearer ${token}` },
          })
          if (userRes.data) {
            user.value = {
              userName: userRes.data.userName,
              userPhone: userRes.data.phoneNum || '無電話資料',
            }
          }
        }
      } catch (e) {
        console.error('Token decode error', e)
      }
    }

    // 3. 獲取優惠券
    try {
      const couponRes = await couponService.getMyCoupons()
      if (couponRes.data.success) {
        // 只顯示未使用且符合低消的優惠券
        coupons.value = couponRes.data.coupons.filter((c) => c.status === 0)
      }
    } catch (e) {
      console.error('Failed to fetch coupons', e)
    }

    loading.value = false
  } catch (error) {
    console.error(error)
    Swal.fire('錯誤', '系統發生錯誤', 'error')
  }
}

// === 計算邏輯 ===

const shippingFee = computed(() => {
  if (form.value.deliveryMethod === '宅配到府') {
    return cartTotal.value < 1000 ? 50 : 0
  } else {
    // 超商取貨
    return cartTotal.value < 350 ? 50 : 0
  }
})

const finalAmount = computed(() => {
  let total = cartTotal.value + shippingFee.value
  if (selectedCouponId.value) {
    const coupon = coupons.value.find((c) => c.couponId === selectedCouponId.value)
    if (coupon) {
      total -= coupon.discountAmount
    }
  }
  return total > 0 ? total : 0
})

const discountAmount = computed(() => {
  if (selectedCouponId.value) {
    const coupon = coupons.value.find((c) => c.couponId === selectedCouponId.value)
    return coupon ? coupon.discountAmount : 0
  }
  return 0
})

const canUseCreditCard = computed(() => true) // 總是可用
const canUseCOD = computed(() => form.value.deliveryMethod === '超商取貨') // 僅限超商取貨

// === 監聽器 ===
watch(
  () => form.value.deliveryMethod,
  (newVal) => {
    // 切換配送方式時清除地址
    form.value.address = ''
  },
)

// === ECPay Map Listeners ===
onMounted(() => {
  initData()
  window.addEventListener('message', handleMapMessage)
})

import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('message', handleMapMessage)
})

const handleMapMessage = (event) => {
  if (event.data && event.data.type === 'STORE_SELECTED') {
    const { storeId, storeName, address } = event.data
    // Format: 7-11(123456) - City... or just address
    // 儲存門市名稱和地址，這裡簡單串接
    form.value.address = `${storeName} (${storeId}) - ${address}`
  }
}

// === 快速填入方法 ===
const cvsType = ref('UNIMART')

const selectStore = () => {
  // 使用 Form Post 方式開啟新視窗 (避免被瀏覽器擋下或跨網域問題)
  // 目標: POST /orders/ecpay/map params={ logisticsSubType: cvsType.value }

  const width = 800
  const height = 600
  const left = (window.screen.width - width) / 2
  const top = (window.screen.height - height) / 2
  const windowName = 'SelectStoreWindow'

  // 1. 先開啟空白視窗
  window.open('', windowName, `width=${width},height=${height},top=${top},left=${left}`)

  // 2. 建立動態 Form 並送出
  const form = document.createElement('form')
  form.method = 'POST'
  form.action = 'http://localhost:8080/orders/ecpay/map'
  form.target = windowName // 指定送出到剛開的視窗

  const input = document.createElement('input')
  input.type = 'hidden'
  input.name = 'logisticsSubType'
  input.value = cvsType.value
  form.appendChild(input)

  document.body.appendChild(form)
  form.submit()
  document.body.removeChild(form)
}

const copySubscriberInfo = () => {
  form.value.recipientName = user.value.userName
  form.value.recipientPhone = user.value.userPhone
}

const submitOrder = async () => {
  // 驗證
  if (!form.value.recipientName || !form.value.recipientPhone) {
    Swal.fire('提示', '請填寫完整收件人資訊', 'warning')
    return
  }
  if (!form.value.address) {
    Swal.fire(
      '提示',
      form.value.deliveryMethod === '宅配到府' ? '請填寫地址' : '請選擇門市',
      'warning',
    )
    return
  }
  if (!form.value.paymentMethod) {
    Swal.fire('提示', '請選擇付款方式', 'warning')
    return
  }

  isSubmitting.value = true
  try {
    const payload = {
      recipientName: form.value.recipientName,
      recipientPhone: form.value.recipientPhone,
      deliveryMethod: form.value.deliveryMethod,
      address: form.value.address,
      paymentMethod: form.value.paymentMethod,
      couponId: selectedCouponId.value,
    }

    // 將前端訂單資料傳入後端執行新增
    const response = await orderService.checkout(payload)

    if (response.data.success) {
      if (response.data.ecpayParams) {
        // ECPay 信用卡付款，建立 Form 並送出
        const container = document.getElementById('ecpay-form-container')
        container.innerHTML = ''

        const formEl = document.createElement('form')
        formEl.method = 'POST'
        formEl.action = response.data.paymentUrl // https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5

        const params = response.data.ecpayParams
        for (const key in params) {
          const input = document.createElement('input')
          input.type = 'hidden'
          input.name = key
          input.value = params[key]
          formEl.appendChild(input)
        }

        container.appendChild(formEl)
        formEl.submit()
      } else {
        // 一般訂單 (貨到付款)
        Swal.fire({
          icon: 'success',
          title: '訂購成功',
          text: '您的訂單已建立！',
          showConfirmButton: false,
          timer: 1500,
        }).then(() => {
          router.push({ name: 'orderSuccess' }) // 導向成功頁面
        })
      }
    } else {
      Swal.fire('訂購失敗', response.data.message, 'error')
    }
  } catch (error) {
    console.error(error)
    Swal.fire('錯誤', '訂購失敗 (Server Error)', 'error')
  } finally {
    isSubmitting.value = false
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

.checkout-container {
  width: 95%;
  max-width: 1000px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 6px; /* 圓角效果 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

h2 {
  color: #7b5e47;
  margin-bottom: 20px;
  border-bottom: 1px solid #e0d9c9;
  padding-bottom: 10px;
}

.checkout-content {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.form-section {
  flex: 2;
  min-width: 300px;
}

.summary-section {
  flex: 1;
  min-width: 250px;
}

.section-block {
  margin-bottom: 25px;
  border: 1px solid #eee;
  padding: 20px;
  border-radius: 8px;
}

.section-block h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #555;
  font-size: 18px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 8px;
  margin-bottom: 15px;
}
.section-header h3 {
  border-bottom: none;
  margin: 0;
  padding: 0;
}

.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #666;
}

.required {
  color: #d33;
}

input[type='text'] {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 15px;
  width: 100%;
  box-sizing: border-box;
}

.readonly-input {
  background-color: #f9f9f9;
  color: #888;
}

.btn-copy {
  background-color: #e8e4dc;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 13px;
  color: #555;
}
.btn-copy:hover {
  background-color: #dcd5c7;
}

/* 單選按鈕群組 */
.radio-group {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.radio-label {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.radio-label.active {
  border-color: #a07d58;
  background-color: #fff9f0;
  color: #a07d58;
  font-weight: bold;
}

.shipping-note {
  font-size: 13px;
  color: #d94;
  margin-top: 5px;
}

.store-selector {
  display: flex;
  gap: 10px;
}

.btn-select-store {
  background-color: #7b5e47;
  color: white;
  border: none;
  padding: 0 15px;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

/* 付款選項 */
.payment-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.payment-options label {
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.payment-options input:checked + span {
  font-weight: bold;
  color: #a07d58;
}

/* 訂單摘要 */
.summary-card {
  background-color: #fcfcfc;
  border: 1px solid #e0d9c9;
  padding: 20px;
  border-radius: 8px;
  position: sticky;
  top: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 15px;
  color: #555;
}

.divider {
  border-top: 1px dashed #ccc;
  margin: 15px 0;
}

.total-row {
  font-size: 18px;
  font-weight: bold;
  color: #d33;
}

.btn-checkout {
  width: 100%;
  padding: 12px;
  background-color: #2e5a44;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 20px;
}
.btn-checkout:disabled {
  background-color: #a9b7b0;
  cursor: not-allowed;
}
.btn-checkout:hover:not(:disabled) {
  background-color: #3e795b;
}

.btn-back {
  width: 100%;
  padding: 10px;
  background-color: transparent;
  color: #666;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 10px;
  cursor: pointer;
}
.disabled-coupon {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #f5f5f5;
}
.text-error {
  color: #d33;
}

/* 訂單明細列表 */
.order-items-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  display: flex;
  gap: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.item-image {
  width: 60px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ddd;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
  font-size: 15px;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}

.item-subtotal {
  color: #d33;
  font-weight: bold;
}
</style>
