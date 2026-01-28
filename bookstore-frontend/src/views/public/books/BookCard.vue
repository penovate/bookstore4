<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { useCartStore } from '@/stores/cartStore'
import orderService from '@/api/orderService.js'

const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
})

const router = useRouter()
const cartStore = useCartStore()

// 格式化金額
const formattedPrice = computed(() => {
  return props.book.price ? `$${Number(props.book.price).toLocaleString()}` : '$0'
})

// 圖片路徑處理
const imageUrl = computed(() => {
  // 1. 優先檢查完整 Entity 結構 (BooksBean -> BookImageBean -> imageUrl)
  if (props.book.bookImageBean && props.book.bookImageBean.imageUrl) {
    return `http://localhost:8080/upload-images/${props.book.bookImageBean.imageUrl}`
  }
  // 2. 檢查 DTO(來自 BookSalesDTO 的 coverImage 或手動 mapping 的 imagePath)
  if (props.book.coverImage) {
    return `http://localhost:8080/upload-images/${props.book.coverImage}`
  }
  if (props.book.imagePath) {
    return `http://localhost:8080/upload-images/${props.book.imagePath}`
  }
  return ''
})

const goToDetail = () => {
  router.push({ name: 'user-book-detail', params: { id: props.book.bookId } })
}

const addToCart = async () => {
  try {
    const response = await orderService.addToCart(props.book.bookId, 1)

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '加入成功',
        text: `${props.book.bookName} 已加入購物車`,
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
const addAndGoToCart = async () => {
  try {
    const response = await orderService.addToCart(props.book.bookId, 1)

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
</script>

<template>
  <v-card class="mx-auto rounded-lg elevation-3 card-hover" max-width="300" @click="goToDetail">
    <!-- 圖片區 -->
    <div class="image-container bg-grey-lighten-4 d-flex align-center justify-center">
      <v-img v-if="imageUrl" :src="imageUrl" cover aspect-ratio="0.7"></v-img>
      <v-icon v-else icon="mdi-book-open-page-variant" size="64" color="grey-lighten-1"></v-icon>
    </div>

    <v-card-item>
      <!-- 書名 -->
      <v-card-title class="text-h6 font-weight-bold text-truncate mb-1">
        {{ book.bookName }}
      </v-card-title>

      <!-- 作者 -->
      <v-card-subtitle class="mb-2">
        <v-icon icon="mdi-account-edit" size="small" class="me-1"></v-icon>
        {{ book.author || '未知作者' }}
      </v-card-subtitle>
    </v-card-item>

    <v-card-text>
      <!-- 價格 (酒紅色) -->
      <div class="text-h5 font-weight-bold price-text">
        {{ formattedPrice }}
      </div>
    </v-card-text>

    <v-card-actions>
      <v-btn block variant="flat" color="primary" class="text-white" @click.stop="addToCart">
        <v-icon start>mdi-cart-plus</v-icon>
        加入購物車
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.image-container {
  height: 300px;
  overflow: hidden;
}

/* 酒紅色文字 */
.price-text {
  color: #800020;
  /* Wine Red */
}

/* 卡片懸停效果 */
.card-hover {
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 25px 0 rgba(0, 0, 0, 0.1) !important;
}
</style>
