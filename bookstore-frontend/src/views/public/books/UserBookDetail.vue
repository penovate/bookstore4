<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import bookService from '@/api/bookService.js'
import Swal from 'sweetalert2'
import axios from 'axios'
import { useCartStore } from '@/stores/cartStore'
import orderService from '@/api/orderService.js'
import BookReviews from '@/views/public/reviews/BookReviews.vue'

const route = useRoute()
const router = useRouter()
const book = ref({})
const loading = ref(false)
const cartStore = useCartStore()
const quantity = ref(1)
const isFavorited = ref(false)
const userId = localStorage.getItem('userId')

const bookId = route.params.id

// 載入書籍詳情
const loadBookMsg = async () => {
  loading.value = true
  try {
    const response = await bookService.getBook(bookId)
    book.value = response.data
  } catch (error) {
    console.error('載入書籍詳情失敗:', error)
    Swal.fire('錯誤', '無法載入書籍資料', 'error')
  } finally {
    loading.value = false
  }
}

// 圖片路徑
const imageUrl = computed(() => {
  if (book.value.bookImageBean && book.value.bookImageBean.imageUrl) {
    return `http://localhost:8080/upload-images/${book.value.bookImageBean.imageUrl}`
  }
  return ''
})

// 格式化金額
const formattedPrice = computed(() => {
  return book.value.price ? `$${Number(book.value.price).toLocaleString()}` : '$0'
})

const addToCart = async () => {
  try {
    const response = await orderService.addToCart(book.value.bookId, quantity.value)

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '加入成功',
        text: `${book.value.bookName} 已加入購物車`,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 1500,
      })

      // 更新全域狀態
      if (response.data.cartCount !== undefined) {
        cartStore.setCartCount(response.data.cartCount)
      } else {
        cartStore.fetchCartCount()
      }
    } else {
      if (response.data.message === '請先登入') {
        router.push('/login')
      }
      Swal.fire('加入失敗', response.data.message, 'error')
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      Swal.fire('驗證失效', '請重新登入', 'error').then(() => {
        router.push('/login')
      })
    } else {
      console.error(error)
      Swal.fire('錯誤', '加入購物車失敗', 'error')
    }
  }
}

// 立即購買
const buyNow = async () => {
  try {
    const response = await orderService.addToCart(book.value.bookId, quantity.value)

    if (response.data.success) {
      // 更新全域狀態
      if (response.data.cartCount !== undefined) {
        cartStore.setCartCount(response.data.cartCount)
      } else {
        cartStore.fetchCartCount()
      }
      router.push({ name: 'cart' })
    } else {
      if (response.data.message === '請先登入') {
        router.push('/login')
      }
      Swal.fire('購買失敗', response.data.message, 'error')
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      Swal.fire('驗證失效', '請重新登入', 'error').then(() => {
        router.push('/login')
      })
    } else {
      console.error(error)
      Swal.fire('錯誤', '無法加入購物車', 'error')
    }
  }
}

const checkFavoriteStatus = async () => {
  if (!userId || !bookId) return
  try {
    const response = await axios.get(`http://localhost:8080/api/wishlist/check`, {
      params: { userId: parseInt(userId), bookId: parseInt(bookId) }
    })
    isFavorited.value = response.data
  } catch (error) {
    console.error('檢查收藏狀態失敗:', error)
  }
}

// 收藏
const addToCollection = async () => {
  if (!userId) {
    Swal.fire('請先登入', '登入後即可收藏書籍', 'warning').then(() => {
      router.push('/login')
    })
    return
  }

  try {
    const response = await axios.post('http://localhost:8080/api/wishlist/toggle', {
      userId: parseInt(userId),
      bookId: parseInt(bookId)
    })

    if (response.data.success) {
      isFavorited.value = response.data.isFavorited 
      
      Swal.fire({
        icon: 'success',
        title: response.data.message, 
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 1500,
      })
    }
  } catch (error) {
    console.error('收藏操作失敗:', error)
    Swal.fire('錯誤', '操作失敗，請稍後再試', 'error')
  }
}

const goBack = () => {
  router.go(-1)
}

const recordView = async () => {
  const userId = localStorage.getItem('userId')
  
  if (userId && bookId) {
    try {
      await axios.post('http://localhost:8080/api/history/record', {
        userId: parseInt(userId),
        bookId: parseInt(bookId)
      })
      console.log('瀏覽紀錄已更新')
    } catch (error) {
      console.error('更新瀏覽紀錄失敗:', error)
    }
  }
}

onMounted(() => {
  if (bookId) {
    loadBookMsg()
    recordView()
    checkFavoriteStatus()
  }
})
</script>

<template>
  <v-container class="book-detail-container py-8">
    <!-- 麵包屑/返回 -->
    <v-btn variant="text" prepend-icon="mdi-arrow-left" @click="goBack" class="mb-4">
      返回列表
    </v-btn>

    <v-card v-if="!loading && book.bookId" class="pa-6 rounded-lg elevation-2">
      <v-row>
        <!-- 左側：圖片區 -->
        <v-col cols="12" md="4" class="d-flex justify-center align-start">
          <div class="image-wrapper elevation-3 rounded">
            <v-img
              v-if="imageUrl"
              :src="imageUrl"
              cover
              max-width="100%"
              aspect-ratio="0.7"
            ></v-img>
            <v-sheet
              v-else
              color="grey-lighten-3"
              class="d-flex align-center justify-center"
              height="400"
              width="300"
            >
              <v-icon icon="mdi-book-open-page-variant" size="80" color="grey"></v-icon>
            </v-sheet>
          </div>
        </v-col>

        <!-- 右側：資訊區 -->
        <v-col cols="12" md="8">
          <div class="d-flex flex-column h-100">
            <!-- 書名 -->
            <h1 class="text-h4 font-weight-bold text-primary mb-2">{{ book.bookName }}</h1>

            <!-- 其他資訊 -->
            <div class="text-subtitle-1 mb-4 text-grey-darken-2">
              <div class="mb-1"><strong class="me-2">作者:</strong> {{ book.author }}</div>
              <div class="mb-1">
                <strong class="me-2">譯者:</strong> {{ book.translator || '無' }}
              </div>
              <div class="mb-1"><strong class="me-2">出版社:</strong> {{ book.press }}</div>
              <div class="mb-1">
                <strong class="me-2">庫存:</strong>
                <span :class="book.stock > 0 ? 'text-success' : 'text-error'">
                  {{ book.stock > 0 ? `現貨 ${book.stock} 本` : '暫無庫存' }}
                </span>
              </div>
            </div>

            <v-divider class="mb-4"></v-divider>

            <!-- 價格 -->
            <div class="d-flex align-baseline mb-6">
              <span class="text-h6 text-grey me-2">售價:</span>
              <span class="text-h3 font-weight-bold price-text">{{ formattedPrice }}</span>
            </div>

            <v-spacer></v-spacer>

            <!-- 按鈕區 -->
            <div class="d-flex gap-3 mt-4">
              <v-btn
                variant="outlined"
                :color="isFavorited ? 'error' : 'primary'" 
                size="large"
                :prepend-icon="isFavorited ? 'mdi-heart' : 'mdi-heart-outline'"
                @click="addToCollection"
              >
                {{ isFavorited ? '已收藏' : '收藏' }}
              </v-btn>
              <v-btn
                variant="tonal"
                color="secondary"
                size="large"
                prepend-icon="mdi-cart-plus"
                class="ms-3"
                @click="addToCart"
                :disabled="book.stock <= 0"
              >
                加入購物車
              </v-btn>
              <v-btn
                variant="flat"
                color="primary"
                size="large"
                class="ms-3 flex-grow-1"
                @click="buyNow"
                :disabled="book.stock <= 0"
              >
                立即購買
              </v-btn>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-divider class="my-6"></v-divider>

      <!-- 下方：簡介區 -->
      <div class="description-section">
        <h3 class="text-h5 font-weight-bold text-primary mb-4">內容簡介</h3>
        <div class="text-body-1 text-grey-darken-3 lh-loose">
          {{ book.shortDesc || '暫無簡介' }}
        </div>
      </div>

      <div class="review-section-wrapper">
        <BookReviews v-if="bookId" :book-id="bookId" />
      </div>
    </v-card>

    <!-- 載入中 -->
    <div v-else-if="loading" class="text-center pa-10">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>
  </v-container>
</template>

<style scoped>
.book-detail-container {
  max-width: 1000px;
}

.image-wrapper {
  width: 100%;
  max-width: 350px;
  overflow: hidden;
}

.price-text {
  color: #800020;
}

.lh-loose {
  line-height: 1.8;
  white-space: pre-wrap;
  /* 保留換行 */
}

.review-section-wrapper {
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid #e0d9c9;
}
</style>
