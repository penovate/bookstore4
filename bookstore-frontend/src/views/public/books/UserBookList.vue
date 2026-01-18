<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import bookService from '@/api/bookService.js';
import BookCard from './BookCard.vue';
import Swal from 'sweetalert2';

const router = useRouter();
const books = ref([]);
const loading = ref(false);
const search = ref('');
const viewMode = ref('grid'); // 'grid' | 'list'

// 載入書籍
const loadBooks = async () => {
    loading.value = true;
    try {
        const response = await bookService.getAllBooks();
        // 只顯示上架中的書籍 (onShelf === 1)
        books.value = response.data.filter(book => book.onShelf === 1);
    } catch (error) {
        console.error('載入書籍失敗:', error);
        Swal.fire('錯誤', '無法載入書籍資料', 'error');
    } finally {
        loading.value = false;
    }
};

// 篩選邏輯 (搜尋)
const filteredBooks = computed(() => {
    if (!search.value) return books.value;
    const lowerSearch = search.value.toLowerCase();
    return books.value.filter(book =>
        book.bookName.toLowerCase().includes(lowerSearch) ||
        (book.author && book.author.toLowerCase().includes(lowerSearch))
    );
});

// 取得圖片 URL helper
const getImageUrl = (book) => {
    if (book.bookImageBean && book.bookImageBean.imageUrl) {
        return `http://localhost:8080/upload-images/${book.bookImageBean.imageUrl}`;
    }
    return '';
};

// 格式化金額 helper
const formatPrice = (price) => {
    return price ? `$${Number(price).toLocaleString()}` : '$0';
};

const goToDetail = (bookId) => {
    router.push({ name: 'user-book-detail', params: { id: bookId } });
};

onMounted(() => {
    loadBooks();
});
</script>

<template>
    <div class="user-book-list">
        <!-- 標題與工具列 -->
        <v-row class="mb-6" align="center">
            <v-col cols="12" md="4">
                <h2 class="text-h4 font-weight-bold text-primary">書籍專區</h2>
                <p class="text-subtitle-1 text-grey-darken-1">探索我們精心挑選的好書</p>
            </v-col>

            <v-col cols="12" md="8" class="d-flex align-center justify-end flex-wrap gap-4">
                <!-- 搜尋 -->
                <v-text-field v-model="search" label="搜尋書籍或作者..." prepend-inner-icon="mdi-magnify" variant="outlined"
                    density="compact" hide-details bg-color="white" class="flex-grow-1"
                    style="max-width: 300px;"></v-text-field>

                <!-- 顯示切換 -->
                <v-btn-toggle v-model="viewMode" mandatory color="primary" variant="outlined" density="compact"
                    class="ms-4">
                    <v-btn value="grid" icon="mdi-view-grid"></v-btn>
                    <v-btn value="list" icon="mdi-view-list"></v-btn>
                </v-btn-toggle>
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
                <v-col v-for="book in filteredBooks" :key="book.bookId" cols="12" sm="6" md="4" lg="3">
                    <BookCard :book="book" />
                </v-col>
            </v-row>

            <!-- LIST VIEW -->
            <div v-else-if="viewMode === 'list' && filteredBooks.length > 0" class="d-flex flex-column gap-3">
                <v-card v-for="book in filteredBooks" :key="book.bookId"
                    class="d-flex flex-row align-center pa-4 rounded-lg elevation-2 card-hover"
                    @click="goToDetail(book.bookId)">
                    <!-- 圖片 -->
                    <div class="me-4" style="width: 80px; height: 100px; flex-shrink: 0;">
                        <v-img v-if="getImageUrl(book)" :src="getImageUrl(book)" cover class="rounded"
                            height="100%"></v-img>
                        <v-sheet v-else color="grey-lighten-3" height="100%"
                            class="d-flex align-center justify-center rounded">
                            <v-icon icon="mdi-book-open-page-variant" color="grey"></v-icon>
                        </v-sheet>
                    </div>

                    <!-- 資訊 -->
                    <div class="flex-grow-1">
                        <div class="text-h6 font-weight-bold text-primary">{{ book.bookName }}</div>
                        <div class="text-subtitle-2 text-grey-darken-1">{{ book.author }}</div>
                        <div class="text-caption text-grey mt-1 text-truncate" style="max-width: 400px;">
                            {{ book.shortDesc || '暫無簡介' }}
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
            </div>
        </div>
    </div>
</template>

<style scoped>
.user-book-list {
    max-width: 1200px;
    margin: 0 auto;
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
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}

.card-hover:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}
</style>
