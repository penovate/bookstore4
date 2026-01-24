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
            :items="reviews" 
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
                <v-chip 
                    :color="getStatusColor(item.status)" 
                    size="small" 
                    class="status-chip"
                >
                    {{ item.status }}
                </v-chip>
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
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const reviews = ref([]);
const loading = ref(false);

// 判斷目前是查看哪本書
const currentBookId = ref(route.params.bookId); 

// 定義表格欄位
const headers = [
    { title: '評價 ID', key: 'reviewId', align: 'start', sortable: true },
    { title: '會員 ID', key: 'memberId', align: 'start', sortable: true },
    { title: '評分', key: 'rating', align: 'center', sortable: true },
    { title: '評論內容', key: 'comment', align: 'start', sortable: false, width: '40%' },
    { title: '評價時間', key: 'reviewDate', align: 'center', sortable: true },
    { title: '狀態', key: 'status', align: 'center', sortable: true },
    { title: '操作', key: 'actions', align: 'center', sortable: false },
];

// 顏色判斷
const getStatusColor = (status) => {
    return status === '顯示中' ? 'success' : 'grey';
};

// 讀取資料函式
const loadReviews = async () => {
  loading.value = true;

  setTimeout(() => { 
      reviews.value = [
          { 
              reviewId: 101, 
              memberId: 'user001', 
              rating: 5, 
              comment: '這本書寫得太好了！受益良多，對於初學者來說非常友善。', 
              reviewDate: '2026-01-20', 
              status: '顯示中' 
          },
          { 
              reviewId: 102, 
              memberId: 'user002', 
              rating: 3, 
              comment: '內容還可以，但範例有點舊，希望能更新到最新版。', 
              reviewDate: '2026-01-21', 
              status: '隱藏' 
          },
          { 
              reviewId: 103, 
              memberId: 'testUser', 
              rating: 1, 
              comment: '書本有缺頁，運送過程好像撞到了。', 
              reviewDate: '2026-01-22', 
              status: '顯示中' 
          }
      ];
      loading.value = false;
  }, 500);
};

// 監聽路由變化
watch(
  () => route.params.bookId,
  (newId) => {
    currentBookId.value = newId;
    loadReviews();
  }
);

onMounted(() => {
  loadReviews();
});
</script>

<style scoped lang="scss">
/* 變數定義：保持配色一致 */
$primary-color: #2E5C43;
$bg-color-header: #F9FBE7;
$bg-color-hover: #F1F8E9;
$bg-color-even: #FAFAFA;
$text-grey: #757575;

.review-container {
    /* 如果需要可以加 padding */
}

/* 標題與按鈕區塊 */
.header-section {
    margin-bottom: 1.5rem; /* 取代 mb-4 */
}

.back-btn {
    color: $text-grey !important;
    margin-bottom: 0.5rem; /* 取代 mb-2 */
}

.title-wrapper {
    /* 控制標題跟 ID 的對齊 */
}

.page-title {
    display: inline-block; /* 取代 d-inline-block */
    font-size: 2.125rem; /* 對應 text-h4 */
    font-weight: 700; /* 對應 font-weight-bold */
    color: $primary-color; /* 對應 text-primary */
    line-height: 1.2;
}

.sub-info {
    font-size: 1rem; /* 對應 text-subtitle-1 */
    color: $text-grey; /* 對應 text-grey-darken-1 */
    margin-left: 0.75rem; /* 取代 ml-3 */
}

/* 表格區塊 */
.table-card {
    border-radius: 8px; /* 對應 rounded-lg */
    border-top: 4px solid $primary-color !important; /* 對應 border-t-4 */
    /* elevation-2 在 Vuetify 比較難完全用 CSS 模擬，建議保留或用 box-shadow */
    box-shadow: 0px 3px 1px -2px rgba(0, 0, 0, 0.2), 
                0px 2px 2px 0px rgba(0, 0, 0, 0.14), 
                0px 1px 5px 0px rgba(0, 0, 0, 0.12); 
}

/* Data Table 內部樣式覆寫 */
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

.status-chip {
    font-weight: 700;
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