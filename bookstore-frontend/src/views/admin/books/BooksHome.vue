<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import bookService from '@/api/bookService.js';
import Swal from 'sweetalert2';

const router = useRouter();
const books = ref([]);
const loading = ref(false);
const search = ref('');

// 圖片放大功能狀態
const zoomedImageUrl = ref(null);
const zoomPosition = ref({ x: 0, y: 0 });

// 定義表格欄位
const headers = [
    // [修正] 移除書籍序號，新增圖片 (置於第一欄)
    { title: '圖片', key: 'bookImage', sortable: false, align: 'center', width: '80px' },
    { title: '書籍名稱', key: 'bookName', sortable: true, align: 'start', width: '20%' },
    { title: '作者', key: 'author', sortable: true, align: 'center' },
    { title: '譯者', key: 'translator', sortable: true, align: 'center' },
    { title: '出版社', key: 'press', sortable: true, align: 'center' },
    // [修正] key 改為 genres
    { title: '類型', key: 'genres', sortable: false, align: 'center' },
    { title: '價錢', key: 'price', sortable: true, align: 'center' },
    { title: 'ISBN', key: 'isbn', sortable: true, align: 'center' },
    { title: '庫存', key: 'stock', sortable: true, align: 'center' },
    { title: '修改', key: 'actions_edit', sortable: false, align: 'center' },
    { title: '封存', key: 'actions_archive', sortable: false, align: 'center' },
    { title: '銷售狀態', key: 'onShelf', sortable: true, align: 'center' },
];

// 讀取書籍資料
const loadBooks = async () => {
    loading.value = true;
    try {
        const response = await bookService.getAllBooks();
        console.log('API Response:', response);
        books.value = response.data;
    } catch (error) {
        console.error('讀取失敗:', error);
    } finally {
        loading.value = false;
    }
};

// 狀態切換處理 (上架/下架)
const toggleStatus = async (item) => {
    const newStatus = item.onShelf === 1 ? 0 : 1;
    try {
        await bookService.updateStatus(item.bookId, newStatus);
        item.onShelf = newStatus;
    } catch (error) {
        console.error('狀態更新失敗:', error);
        // 若需要回滾: item.onShelf = item.onShelf === 1 ? 0 : 1;
    }
};

// 跳轉到修改頁面
const handleEdit = (item) => {
    router.push(`/dev/admin/books/edit/${item.bookId}`);
};

// 執行封存
const handleArchive = async (item) => {
    try {
        const result = await Swal.fire({
            title: '確定要封存嗎？',
            text: "封存後書籍將不出現在前台",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#2E5C43',
            cancelButtonColor: '#d33',
            confirmButtonText: '是，封存！'
        });

        if (result.isConfirmed) {
            await bookService.archiveBook(item.bookId);
            await loadBooks();
            Swal.fire('已封存', '書籍狀態已更新', 'success');
        }
    } catch (error) {
        console.error(error);
        Swal.fire('錯誤', '封存失敗', 'error');
    }
};

// [新增] 圖片路徑處理
const getImageUrl = (imageBean) => {
    if (imageBean && imageBean.imageUrl) {
        // 假設後端圖片位於 http://localhost:8080/images/
        // 如果您的圖片路徑不同，請在此修改
        return `http://localhost:8080/upload-images/${imageBean.imageUrl}`;
    }
    return '';
};

// 顯示放大圖片
const showZoom = (event, imageBean) => {
    const url = getImageUrl(imageBean);
    if (!url) return;
    zoomedImageUrl.value = url;
    zoomPosition.value = { x: event.clientX, y: event.clientY };
};

// 關閉放大圖片
const closeZoom = () => {
    zoomedImageUrl.value = null;
};

onMounted(() => {
    loadBooks();
});
</script>

<template>
    <div>
        <!-- 放大圖片顯示層 (Overlay) -->
        <div v-if="zoomedImageUrl" class="zoomed-image-overlay elevation-10 rounded-lg" :style="{
            top: `${zoomPosition.y}px`,
            left: `${zoomPosition.x}px`
        }" @mouseleave="closeZoom">
            <v-img :src="zoomedImageUrl" cover width="100%" height="100%"></v-img>
        </div>

        <!-- 標題與搜尋區 -->
        <v-row class="mb-4" align="center">
            <v-col cols="12" md="4">
                <h2 class="text-h4 font-weight-bold text-primary">書籍管理列表</h2>
                <v-btn color="primary" class="mt-2" prepend-icon="mdi-plus"
                    @click="router.push('/dev/admin/books/insert')">
                    新增書籍
                </v-btn>
            </v-col>
            <v-col cols="12" md="4" offset-md="4">
                <v-text-field v-model="search" label="搜尋書籍..." prepend-inner-icon="mdi-magnify" variant="outlined"
                    density="compact" hide-details color="primary" class="bg-white rounded"></v-text-field>
            </v-col>
        </v-row>

        <!-- 書籍列表區 -->
        <v-card class="rounded-lg elevation-2 border-t-4 border-primary">
            <v-data-table :headers="headers" :items="books" :loading="loading" :search="search" class="forest-table"
                hover>

                <!-- [新增] 圖片顯示 Slot -->
                <template v-slot:item.bookImage="{ item }">
                    <v-img :src="getImageUrl(item.bookImageBean)" aspect-ratio="1" cover
                        class="bg-grey-lighten-2 rounded my-1" style="width: 50px; height: 50px; cursor: pointer;"
                        @click="showZoom($event, item.bookImageBean)">
                        <!-- 圖片載入失敗 Placeholder -->
                        <template v-slot:placeholder>
                            <div class="d-flex align-center justify-center fill-height">
                                <v-icon icon="mdi-image-off" color="grey"></v-icon>
                            </div>
                        </template>
                        <!-- 圖片載入錯誤 Error -->
                        <template v-slot:error>
                            <div class="d-flex align-center justify-center fill-height">
                                <v-icon icon="mdi-image-broken" color="grey"></v-icon>
                            </div>
                        </template>
                    </v-img>
                </template>

                <!-- [修正] 類型顯示 Slot (處理陣列) -->
                <template v-slot:item.genres="{ item }">
                    <div class="text-caption">
                        {{item.genres?.map(g => g.genreName).join(', ') || '-'}}
                    </div>
                </template>

                <!-- 修改按鈕 -->
                <template v-slot:item.actions_edit="{ item }">
                    <v-btn color="primary" class="text-white" size="small" elevation="1" @click="handleEdit(item)">
                        修改
                    </v-btn>
                </template>

                <!-- 封存按鈕 -->
                <template v-slot:item.actions_archive="{ item }">
                    <v-btn color="secondary" class="text-white" size="small" elevation="1" @click="handleArchive(item)">
                        封存
                    </v-btn>
                </template>

                <!-- 銷售狀態開關 -->
                <template v-slot:item.onShelf="{ item }">
                    <div class="d-flex flex-column align-center">
                        <v-switch v-model="item.onShelf" color="success" hide-details density="compact" :true-value="1"
                            :false-value="0" @update:model-value="toggleStatus(item)" class="ms-2"></v-switch>
                        <span class="text-caption text-grey-darken-1" style="line-height: 1;">
                            {{ item.onShelf === 1 ? '上架中' : '下架中' }}
                        </span>
                    </div>
                </template>

                <!-- 處理空值顯示 -->
                <template v-slot:item.translator="{ value }">
                    {{ value || '-' }}
                </template>

            </v-data-table>
        </v-card>
    </div>
</template>

<style scoped lang="scss">
:deep(.v-data-table-header) {
    background-color: #F9FBE7 !important;
}

:deep(.v-data-table-header__content) {
    font-weight: bold;
    color: #2E5C43;
    font-size: 1rem;
}

:deep(.v-data-table__tr:nth-child(even)) {
    background-color: #FAFAFA;
}

:deep(.v-data-table__tr:hover) {
    background-color: #F1F8E9 !important;
}

/* 放大圖片層樣式 */
.zoomed-image-overlay {
    position: fixed;
    width: 300px;
    height: 300px;
    z-index: 9999;
    transform: translate(-50%, -50%);
    background-color: white;
    border: 4px solid #F5F5DC;
}

.v-switch {
    transform: scale(0.8);
}
</style>
