<template>
  <v-container class="review-container">
    <v-row class="header-section" align="center">
      <v-col>
        <v-btn 
          variant="text" 
          prepend-icon="mdi-arrow-left"
          @click="router.push({ name: 'admin-reviews' })"
          class="back-btn"
        >
          返回評價管理
        </v-btn>
        
        <div class="title-wrapper">
            <h2 class="page-title">評價詳細列表</h2>
            <span v-if="currentBookId" class="sub-info">
                (目前查看書籍 ID: {{ currentBookId }})
            </span>
        </div>
      </v-col>
    </v-row>

    <v-card class="table-card">
        <v-data-table 
            :headers="headers" 
            :items="filteredReviews" 
            :loading="loading" 
            item-value="reviewId"
            class="forest-table"
            hover
        >
            <template v-slot:item.rating="{ item }">
                <v-rating
                    :model-value="item.rating"
                    color="amber"
                    density="compact"
                    half-increments
                    readonly
                    size="small"
                ></v-rating>
            </template>

            <template v-slot:item.status="{ item }">
                <div class="d-flex align-center justify-center">
                    <v-switch
                        :model-value="item.status === 1"
                        color="success"
                        hide-details
                        density="compact"
                        @click.prevent="handleToggleStatus(item)"
                    ></v-switch>
                    <span
                        :class="item.status === 1 ? 'text-success' : 'text-error'"
                        class="ml-2 font-weight-bold text-caption"
                    >
                        {{ item.status === 1 ? '顯示中' : '已隱藏' }}
                    </span>
                </div>
            </template>

            <template v-slot:item.actions="{ item }">
                <v-btn 
                    icon 
                    size="small" 
                    variant="text"
                    class="action-btn"
                    @click="console.log('查看/編輯', item.reviewId)"
                >
                    <v-icon>mdi-pencil</v-icon>
                </v-btn>
            </template>

            <template v-slot:no-data>
                <div class="no-data-text">目前沒有評價資料</div>
            </template>
        </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, watch, computed} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const reviews = ref([]);
const loading = ref(false);

// 判斷目前是查看哪本書
const currentBookId = ref(route.params.bookId); 

// 定義表格欄位
const headers = [
    { title: '評價 ID', key: 'reviewId', align: 'start', sortable: true },
    { title: '會員 ID', key: 'userId', align: 'start', sortable: true },
    { title: '評分', key: 'rating', align: 'center', sortable: true },
    { title: '評價內容', key: 'comment', align: 'start', sortable: false, width: '40%' },
    { title: '評價時間', key: 'createdAt', align: 'center', sortable: true },
    { title: '狀態', key: 'status', align: 'center', sortable: true },
    { title: '操作', key: 'actions', align: 'center', sortable: false },
];

// 計算屬性：如果有指定 currentBookId，就只顯示該本書的評論
// (因為目前後端 API 是抓全部，我們在前端做過濾)
const filteredReviews = computed(() => {
    if (!currentBookId.value) return reviews.value;
    // 注意：後端傳回來的 bookId 可能是數字，如果是字串要轉型比較
    return reviews.value.filter(r => r.bookId == currentBookId.value);
});

// 讀取資料函式
const loadReviews = async () => {
  loading.value = true;
  try {
      const response = await axios.get('/api/public/admin/reviews');
      reviews.value = response.data;
  } catch (error) {
      console.error('讀取評價失敗:', error);
  } finally {
      loading.value = false;
  }
};

// 更新狀態函式 (呼叫 PUT API)
// 狀態切換邏輯 
const handleToggleStatus = (item) => {
    // 1(顯示)， 0(隱藏)
    const newStatus = item.status === 1 ? 0 : 1;
    const actionText = newStatus === 0 ? '隱藏' : '顯示';
    const confirmColor = newStatus === 0 ? '#d33' : '#4CAF50';

    Swal.fire({
        title: `確定要 ${actionText} 這則評價嗎？`,
        text: newStatus === 0 ? "隱藏後，前台將無法看到此評價" : "該評價將重新顯示於前台",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: `確定${actionText}`,
        cancelButtonText: '取消',
        confirmButtonColor: confirmColor,
        cancelButtonColor: '#aaa',
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const updatedReview = { ...item, status: newStatus };

                // 呼叫後端 API
                await axios.put(`/api/public/admin/reviews/${item.reviewId}`, updatedReview);
                
                // 更新前端畫面
                item.status = newStatus;
                
                Swal.fire({ 
                    icon: 'success', 
                    title: '更新成功', 
                    timer: 1000, 
                    showConfirmButton: false 
                });
            } catch (error) {
                console.error('更新狀態失敗:', error);
                Swal.fire({ 
                    icon: 'error', 
                    title: '更新失敗', 
                    text: '請稍後再試' 
                });
            }
        }
    });
};

// 監聽路由變化
watch(
  () => route.params.bookId,
  (newId) => {
    currentBookId.value = newId;
  }
);

onMounted(() => {
  loadReviews();
});
</script>

<style scoped lang="scss">
/* 變數定義 */
$primary-color: #2E5C43;
$bg-color-header: #F9FBE7;
$bg-color-hover: #F1F8E9;
$bg-color-even: #FAFAFA;
$text-grey: #757575;

/* 複製 UserList 的 switch 縮放樣式 */
.v-switch {
  transform: scale(0.8);
}

/* 定義狀態文字顏色 (如果全域沒有定義的話，這裡補上保險) */
.text-success {
    color: #4CAF50 !important;
}
.text-error {
    color: #FF5252 !important;
}

.review-container {
    /* padding */
}

.header-section {
    margin-bottom: 1.5rem; 
}

.back-btn {
    color: $text-grey !important;
    margin-bottom: 0.5rem;
}

.title-wrapper {
}

.page-title {
    display: inline-block; 
    font-size: 2.125rem; 
    font-weight: 700; 
    color: $primary-color; 
    line-height: 1.2;
}

.sub-info {
    font-size: 1rem; 
    color: $text-grey; 
    margin-left: 0.75rem; 
}

.table-card {
    border-radius: 8px; 
    border-top: 4px solid $primary-color !important; 
    box-shadow: 0px 3px 1px -2px rgba(0, 0, 0, 0.2), 
                0px 2px 2px 0px rgba(0, 0, 0, 0.14), 
                0px 1px 5px 0px rgba(0, 0, 0, 0.12); 
}

.forest-table {
    :deep(.v-data-table-header) {
        background-color: $bg-color-header !important;
    }

    :deep(.v-data-table-header__content) {
        font-weight: bold;
        color: $primary-color;
        font-size: 1rem;
    }

    :deep(.v-data-table__tr:nth-child(even)) {
        background-color: $bg-color-even;
    }

    :deep(.v-data-table__tr:hover) {
        background-color: $bg-color-hover !important;
    }
}

.action-btn {
    color: $primary-color !important;
}

.no-data-text {
    padding-top: 1.25rem;
    padding-bottom: 1.25rem;
    color: $text-grey;
}
</style>