<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import bookService from '@/api/bookService.js'
import BookCard from './BookCard.vue'
import Swal from 'sweetalert2'
import AiAssistant from '@/views/public/books/AiAssistant.vue'

const route = useRoute()
const router = useRouter()
const books = ref([])
const genres = ref([])
const loading = ref(false)
const search = ref('')
const viewMode = ref('grid')
const selectedGenreId = ref('all')

// 載入書籍與分類
const loadData = async () => {
  loading.value = true
  try {
    const [booksRes, genresRes] = await Promise.all([
      bookService.getAllBooks(),
      bookService.getGenres(),
    ])

    // 只顯示上架中的書籍 (onShelf === 1)
    books.value = booksRes.data.filter((book) => book.onShelf === 1)
    genres.value = genresRes.data

    // 檢查 URL query 是否有指定 genreId
    if (route.query.genreId) {
      // 確保該 genreId 存在於分類列表中
      const targetGenre = genresRes.data.find(
        (g) => String(g.genreId) === String(route.query.genreId),
      )
      if (targetGenre) {
        selectedGenreId.value = targetGenre.genreId
      }
    }
  } catch (error) {
    console.error('載入資料失敗:', error)
    Swal.fire('錯誤', '無法載入書籍資料', 'error')
  } finally {
    loading.value = false
  }
}

// 篩選邏輯
const filteredBooks = computed(() => {
  let result = books.value

  // 1. 分類篩選
  if (selectedGenreId.value !== 'all') {
    result = result.filter(
      (book) => book.genres && book.genres.some((g) => g.genreId === selectedGenreId.value),
    )
  }

  // 2. 搜尋篩選
  if (search.value) {
    const lowerSearch = search.value.toLowerCase()
    result = result.filter(
      (book) =>
        book.bookName.toLowerCase().includes(lowerSearch) ||
        (book.author && book.author.toLowerCase().includes(lowerSearch)),
    )
  }
  return result
})

// 取得圖片 URL helper
const getImageUrl = (book) => {
  if (book.bookImageBean && book.bookImageBean.imageUrl) {
    return `http://localhost:8080/upload-images/${book.bookImageBean.imageUrl}`
  }
  return ''
}

// 格式化金額 helper
const formatPrice = (price) => {
  return price ? `$${Number(price).toLocaleString()}` : '$0'
}

const goToDetail = (bookId) => {
  router.push({ name: 'user-book-detail', params: { id: bookId } })
}

const selectGenre = (genreId) => {
  selectedGenreId.value = genreId
}

// 監聽路由參數變化 (支援同一頁面切換分類)
watch(
  () => route.query.genreId,
  (newGenreId) => {
    if (newGenreId && genres.value.length > 0) {
      const targetGenre = genres.value.find((g) => String(g.genreId) === String(newGenreId))
      if (targetGenre) {
        selectedGenreId.value = targetGenre.genreId
      } else {
        selectedGenreId.value = 'all'
      }
    } else if (!newGenreId) {
      selectedGenreId.value = 'all'
    }
  },
)

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="user-book-list">
    <!-- 左側：分類導覽列  -->
    <div class="category-sidebar d-none d-md-block">
      <v-card class="rounded-lg elevation-2 border-opacity-75" color="white">
        <v-list class="py-2" density="compact">
          <v-list-subheader class="text-h6 font-weight-bold text-primary mb-2"
            >書籍分類</v-list-subheader
          >

          <!-- 全部書籍 -->
          <v-list-item
            value="all"
            active-color="primary"
            :active="selectedGenreId === 'all'"
            @click="selectGenre('all')"
            rounded="lg"
            class="mb-2 mx-2"
            variant="tonal"
          >
            <template v-slot:prepend>
              <v-icon icon="mdi-apps"></v-icon>
            </template>
            <v-list-item-title class="font-weight-bold">全部書籍</v-list-item-title>
          </v-list-item>

          <v-divider class="my-2 mx-4"></v-divider>

          <!-- 分類列表 -->
          <div class="genre-list-container">
            <v-list-item
              v-for="genre in genres"
              :key="genre.genreId"
              :value="genre.genreId"
              active-color="primary"
              :active="selectedGenreId === genre.genreId"
              @click="selectGenre(genre.genreId)"
              rounded="lg"
              class="mb-1 mx-2 genre-item"
            >
              <template v-slot:prepend>
                <v-icon
                  :icon="selectedGenreId === genre.genreId ? 'mdi-book-open-page-variant' : 'mdi-book'"
                  size="small"
                  :color="selectedGenreId === genre.genreId ? 'primary' : 'grey'"
                ></v-icon>
              </template>
              <v-list-item-title :class="{ 'font-weight-bold': selectedGenreId === genre.genreId }">
                {{ genre.genreName }}
              </v-list-item-title>
            </v-list-item>
          </div>
        </v-list>
      </v-card>
    </div>

    <!-- 右側：書籍列表與工具列 (Centered) -->
    <v-container>
      <v-row justify="center">
        <v-col cols="12" md="10" lg="10">
          <!-- 標題與工具列 -->
          <v-row class="mb-6" align="center">
            <v-col cols="12" sm="5">
              <h2 class="text-h4 font-weight-bold text-primary">書籍專區</h2>
              <p class="text-subtitle-1 text-grey-darken-1">
                <span v-if="selectedGenreId === 'all'">探索我們精心挑選的好書</span>
                <span v-else>
                  分類：{{ genres.find((g) => g.genreId === selectedGenreId)?.genreName }}
                </span>
              </p>
            </v-col>

            <v-col cols="12" sm="7" class="d-flex align-center justify-end flex-wrap gap-4">
              <!-- 搜尋 -->
              <v-text-field
                v-model="search"
                label="搜尋書籍或作者..."
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                density="compact"
                hide-details
                bg-color="white"
                class="flex-grow-1"
                style="max-width: 300px; min-width: 200px"
              ></v-text-field>

              <!-- 顯示切換 -->
              <v-btn-toggle
                v-model="viewMode"
                mandatory
                color="primary"
                variant="outlined"
                density="compact"
                class="ms-2"
              >
                <v-btn value="grid" icon="mdi-view-grid"></v-btn>
                <v-btn value="list" icon="mdi-view-list"></v-btn>
              </v-btn-toggle>

              <!-- 手機版篩選按鈕 (當螢幕小顯示) -->
              <v-menu v-if="$vuetify.display.smAndDown">
                <template v-slot:activator="{ props }">
                  <v-btn
                    v-bind="props"
                    variant="tonal"
                    color="primary"
                    prepend-icon="mdi-filter-variant"
                    class="d-md-none ms-2"
                  >
                    分類
                  </v-btn>
                </template>
                <v-list>
                  <v-list-item
                    @click="selectGenre('all')"
                    :active="selectedGenreId === 'all'"
                    title="全部書籍"
                  ></v-list-item>
                  <v-list-item
                    v-for="g in genres"
                    :key="g.genreId"
                    @click="selectGenre(g.genreId)"
                    :active="selectedGenreId === g.genreId"
                    :title="g.genreName"
                  ></v-list-item>
                </v-list>
              </v-menu>
            </v-col>
          </v-row>

          <!-- 載入中 -->
          <div v-if="loading" class="text-center pa-10">
            <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
            <div class="mt-4 text-h6 text-grey">載入好書中...</div>
          </div>

          <!-- 書籍列表 -->
          <div v-else>
            <!-- GRID VIEW -->
            <v-row v-if="viewMode === 'grid' && filteredBooks.length > 0">
              <v-col
                v-for="book in filteredBooks"
                :key="book.bookId"
                cols="12"
                sm="6"
                md="4"
                lg="4"
                xl="3"
              >
                <BookCard :book="book" />
              </v-col>
            </v-row>

            <!-- LIST VIEW -->
            <div
              v-else-if="viewMode === 'list' && filteredBooks.length > 0"
              class="d-flex flex-column gap-3"
            >
              <v-card
                v-for="book in filteredBooks"
                :key="book.bookId"
                class="d-flex flex-row align-center pa-4 rounded-lg elevation-2 card-hover"
                @click="goToDetail(book.bookId)"
              >
                <!-- 圖片 -->
                <div class="me-4" style="width: 80px; height: 100px; flex-shrink: 0">
                  <v-img
                    v-if="getImageUrl(book)"
                    :src="getImageUrl(book)"
                    cover
                    class="rounded"
                    height="100%"
                  ></v-img>
                  <v-sheet
                    v-else
                    color="grey-lighten-3"
                    height="100%"
                    class="d-flex align-center justify-center rounded"
                  >
                    <v-icon icon="mdi-book-open-page-variant" color="grey"></v-icon>
                  </v-sheet>
                </div>

                <!-- 資訊 -->
                <div class="flex-grow-1">
                  <div class="text-h6 font-weight-bold text-primary">{{ book.bookName }}</div>
                  <div class="text-subtitle-2 text-grey-darken-1">{{ book.author }}</div>
                  <div class="text-caption text-grey mt-1 text-truncate" style="max-width: 400px">
                    {{ book.shortDesc || '暫無簡介' }}
                  </div>
                  <div class="mt-1">
                    <v-chip
                      v-for="g in book.genres"
                      :key="g.genreId"
                      size="x-small"
                      color="secondary"
                      variant="flat"
                      class="me-1"
                    >
                      {{ g.genreName }}
                    </v-chip>
                  </div>
                </div>

                <!-- 價格與操作 -->
                <div class="d-flex flex-column align-end ms-4">
                  <div class="text-h5 font-weight-bold price-text mb-2">
                    {{ formatPrice(book.price) }}
                  </div>
                  <v-btn variant="flat" color="primary" size="small" class="text-white">
                    詳情
                  </v-btn>
                </div>
              </v-card>
            </div>

            <!-- 無資料 -->
            <div v-if="filteredBooks.length === 0" class="text-center pa-10 text-grey">
              <v-icon icon="mdi-book-off-outline" size="64" class="mb-4"></v-icon>
              <div class="text-h6">暫無相關書籍</div>
              <v-btn
                v-if="selectedGenreId !== 'all'"
                variant="text"
                color="primary"
                class="mt-2"
                @click="selectGenre('all')"
              >
                查看所有書籍
              </v-btn>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <AiAssistant :books="filteredBooks.length > 0 ? filteredBooks : books" />
  </div>
</template>

<style scoped>
.user-book-list {
  /* max-width: 1400px; 移除最大寬度限制 */
  /* margin: 0 auto; */
  position: relative;
  /* 為了 absolute positioning */
}

/* 左側固定導覽列 */
.category-sidebar {
  position: fixed;
  left: 20px;
  top: 100px;
  /* 移除 bottom 讓高度動態調整 */
  /* bottom: 20px; */
  max-height: calc(100vh - 120px);
  /* 防止過長超出視窗 */
  width: 260px;
  z-index: 10;
  display: flex;
  flex-direction: column;
}

/* 讓內部的 Card 跟隨 Sidebar 高度限制，並支援 Scroll */
.category-sidebar > .v-card {
  display: flex;
  flex-direction: column;
  max-height: 100%;
}

.genre-list-container {
  overflow-y: auto;
  flex: 1;
  /* 佔滿剩餘空間 */
  /* 自訂捲軸樣式 (Optional) */
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
}

.gap-3 {
  gap: 12px;
}

.gap-4 {
  gap: 16px;
}

.price-text {
  color: #800020;
}

.card-hover {
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  cursor: pointer;
}

.card-hover:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

/* 讓側邊欄在滾動時固定 */
/* .top-sticky {
    position: sticky;
    top: 20px;
} */
</style>
