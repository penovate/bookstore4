<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import bookService from '@/api/bookService'
import orderService from '@/api/orderService.js'
import BookCard from './books/BookCard.vue'

const router = useRouter()
const featuredBooks = ref([])
const categories = ref([])

// 圖示與顏色庫
const iconPool = [
  { icon: 'mdi-book-open-page-variant', color: '#C8E6C9' },
  { icon: 'mdi-chart-line', color: '#F1F8E9' },
  { icon: 'mdi-account-group', color: '#DCEDC8' },
  { icon: 'mdi-palette', color: '#E8F5E9' },
  { icon: 'mdi-school', color: '#FFF9C4' },
  { icon: 'mdi-briefcase', color: '#D1C4E9' },
  { icon: 'mdi-earth', color: '#B3E5FC' },
  { icon: 'mdi-flask', color: '#FFCCBC' },
  { icon: 'mdi-music', color: '#F8BBD0' },
  { icon: 'mdi-camera', color: '#CFD8DC' },
]

const goToBooks = () => {
  router.push({ name: 'user-books' })
}

const goToCategory = (genreId) => {
  router.push({ name: 'user-books', query: { genreId: genreId } })
}

const getFeaturedBooks = async () => {
  try {
    const response = await bookService.getAllBooks()
    let allBooks = response.data

    // 1. 只選擇上架中的書籍
    allBooks = allBooks.filter((book) => book.onShelf === 1)

    // 2. 隨機打亂陣列 (Fisher-Yates Shuffle)
    for (let i = allBooks.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1))
      ;[allBooks[i], allBooks[j]] = [allBooks[j], allBooks[i]]
    }

    // 3. 取前 4 筆
    featuredBooks.value = allBooks.slice(0, 4)
  } catch (error) {
    console.error('Fetching books failed:', error)
  }
}

const topSellers = ref([])
const getTopSellers = async () => {
  try {
    const res = await orderService.getHomepageTopSellers()
    // 對應 BookCard 需要的格式，BookSalesDTO 回傳的欄位: bookId, bookName, author, price, coverImage, totalQuantity
    // BookCard 可能需要: bookId, bookName, author, price, imagePath (通常後端回傳 coverImage 或 imagePath)
    // 確保格式一致
    topSellers.value = res.data.map((item) => ({
      ...item,
      imagePath: item.coverImage, // Mapping coverImage to imagePath if needed by BookCard
    }))
  } catch (e) {
    console.error('Fetch top sellers failed', e)
  }
}

const getRandomCategories = async () => {
  try {
    const response = await bookService.getGenres()
    let allGenres = response.data

    // 隨機打亂
    for (let i = allGenres.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1))
      ;[allGenres[i], allGenres[j]] = [allGenres[j], allGenres[i]]
    }

    // 取前 4 個，並分配圖示與顏色
    categories.value = allGenres.slice(0, 4).map((genre, index) => {
      const style = iconPool[index % iconPool.length] // 循環使用樣式
      return {
        title: genre.genreName,
        genreId: genre.genreId,
        icon: style.icon,
        color: style.color,
      }
    })
  } catch (error) {
    console.error('Fetching genres failed:', error)
  }
}

onMounted(() => {
  getFeaturedBooks()
  getRandomCategories()
  getTopSellers()
})
</script>
<template>
  <div class="home-wrapper">
    <v-parallax
      src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=2070"
      height="500"
    >
      <div
        class="d-flex flex-column fill-height justify-center align-center text-white hero-overlay"
      >
        <h1 class="text-h2 font-weight-black mb-4">網路書籍商城</h1>
        <h4 class="text-h5 font-weight-light mb-6">在喧囂的城市中，為您找尋一隅靜謐的書香。</h4>
        <v-btn
          color="primary"
          size="x-large"
          class="rounded-pill px-8"
          elevation="4"
          @click="goToBooks"
        >
          立即探索
        </v-btn>
      </div>
    </v-parallax>

    <v-container class="mt-12">
      <div class="d-flex align-center mb-8">
        <v-divider></v-divider>
        <h2 class="mx-4 text-h4 font-weight-bold" style="color: #2e5c43">熱門分類</h2>
        <v-divider></v-divider>
      </div>
      <v-row>
        <v-col v-for="cat in categories" :key="cat.title" cols="12" sm="6" md="3">
          <v-hover v-slot="{ isHovering, props }">
            <v-card
              v-bind="props"
              class="category-card rounded-xl text-center pa-6"
              :elevation="isHovering ? 8 : 2"
              :color="isHovering ? '#E8F5E9' : 'white'"
              @click="goToCategory(cat.genreId)"
            >
              <v-icon :icon="cat.icon" size="48" color="primary" class="mb-4"></v-icon>
              <h3 class="text-h6 font-weight-bold">{{ cat.title }}</h3>
            </v-card>
          </v-hover>
        </v-col>
      </v-row>
    </v-container>

    <v-container class="my-12">
      <div class="d-flex justify-space-between align-center mb-6">
        <h2 class="text-h4 font-weight-bold" style="color: #2e5c43">本月熱銷排行</h2>
        <v-btn variant="text" color="primary" @click="goToBooks"
          >查看全部 <v-icon icon="mdi-chevron-right"></v-icon
        ></v-btn>
      </div>
      <v-row>
        <v-col v-for="(book, index) in topSellers" :key="book.bookId" cols="12" sm="6" md="3">
          <div class="position-relative h-100">
            <!-- 排名標籤 -->
            <v-chip
              class="position-absolute z-index-10 ma-2"
              color="red"
              elevation="2"
              label
              style="top: 0; left: 0; z-index: 5"
            >
              No.{{ index + 1 }} | 熱銷 {{ book.totalQuantity }} 本
            </v-chip>
            <BookCard :book="book" />
          </div>
        </v-col>
      </v-row>
    </v-container>

    <v-container class="my-12">
      <div class="d-flex justify-space-between align-center mb-6">
        <h2 class="text-h4 font-weight-bold" style="color: #2e5c43">本月精選</h2>
        <v-btn variant="text" color="primary" @click="goToBooks"
          >查看更多 <v-icon icon="mdi-chevron-right"></v-icon
        ></v-btn>
      </div>

      <v-row>
        <v-col v-for="book in featuredBooks" :key="book.bookId" cols="12" sm="6" md="3">
          <BookCard :book="book" />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.hero-overlay {
  background: rgba(0, 0, 0, 0.4);
}

.category-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.book-card {
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}
</style>
