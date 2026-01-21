<template>
  <v-container>
    <v-row class="mb-4">
      <v-col cols="12">
        <div class="d-flex align-center">
          <h2 class="text-h4 font-weight-bold mr-4">書籍評論</h2>
          <v-chip color="primary" variant="outlined">
            共 {{ reviews.length }} 則評論
          </v-chip>
        </div>
        
        <div class="d-flex align-center mt-2">
          <span class="text-h3 font-weight-bold text-amber-darken-2 mr-2">
            {{ averageRating }}
          </span>
          <div>
            <v-rating
              :model-value="Number(averageRating)"
              color="amber-darken-2"
              density="compact"
              half-increments
              readonly
              size="small"
            ></v-rating>
            <div class="text-caption text-grey">平均星等</div>
          </div>
        </div>
      </v-col>
    </v-row>

    <v-card class="mb-6" elevation="2" border>
      <v-card-title class="bg-grey-lighten-4">
        撰寫評論
      </v-card-title>
      <v-card-text class="pt-4">
        <v-form ref="form" v-model="valid">
          <div class="mb-2">
            <div class="text-subtitle-1 mb-1">您的評分 (必填)</div>
            <v-rating
              v-model="newReview.rating"
              color="amber-darken-2"
              hover
              density="comfortable"
            ></v-rating>
            <div v-if="errors.rating" class="text-caption text-red">
              請點選星星給予評分
            </div>
          </div>

          <v-textarea
            v-model="newReview.comment"
            label="寫下您的心得(必填)..."
            placeholder="這本書最讓你印象深刻的是什麼？"
            variant="outlined"
            rows="3"
            auto-grow
            counter="500"
            :error="errors.comment"
            :error-messages="errors.comment ? '請填寫評論內容' : ''"
          ></v-textarea>
        </v-form>
      </v-card-text>
      
      <v-card-actions class="px-4 pb-4">
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="elevated"
          @click="submitReview"
          prepend-icon="mdi-send"
          :loading="isSubmitting"
        >
          送出評論
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-divider class="my-6"></v-divider>

    <div v-if="reviews.length > 0">
      <v-card
        v-for="review in reviews"
        :key="review.reviewId"
        class="mb-4"
        variant="flat"
        border
      >
        <v-card-text>
          <div class="d-flex flex-row">
            <div class="mr-4">
              <v-avatar color="secondary" size="48">
                <span class="text-h6 text-white">{{ (review.userName && review.userName[0]) || review.userId }}</span>
              </v-avatar>
            </div>

            <div class="flex-grow-1">
              <div class="d-flex justify-space-between align-start">
                <div>
                  <div class="text-subtitle-1 font-weight-bold">
                    {{ review.userName || `User ${review.userId}` }}
                  </div>
                  <div class="text-caption text-grey">
                    {{ formatDate(review.createdAt) }}
                  </div>
                </div>
                
                <div class="d-flex align-center">
                  <v-rating
                    :model-value="review.rating"
                    color="amber-darken-2"
                    density="compact"
                    readonly
                    size="small"
                  ></v-rating>

                  <v-btn
                    variant="text"
                    size="small"
                    color="grey-lighten-1"
                    class="ml-2"
                    icon
                    @click="handleReport(review)"
                  >
                    <span class="mdi mdi-alert" style="font-size: 20px;"></span>
                    <v-tooltip activator="parent" location="top">檢舉</v-tooltip>
                  </v-btn>
                </div>
              </div>

              <div class="mt-3 text-body-1">
                {{ review.comment || "此使用者未填寫文字評論。" }}
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>

    <v-sheet
      v-else
      class="d-flex flex-column align-center justify-center py-8 text-grey"
    >
      <v-icon icon="mdi-message-text-outline" size="64" class="mb-2"></v-icon>
      <div>目前還沒有評論，搶先成為第一個評論者吧！</div>
    </v-sheet>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import Swal from 'sweetalert2';
import reviewService from '@/api/reviewService.js';

const props = defineProps({
  bookId: {
    type: [Number, String],
    default: 1
  }
});

// --- 狀態 ---
const reviews = ref([]);
const isSubmitting = ref(false);
const valid = ref(false);
const newReview = ref({
  rating: 0,
  comment: ""
});
const errors = ref({
  rating: false,
  comment: false
});

// --- 從後端撈資料 ---
const fetchReviews = async () => {
  try {
    const response = await reviewService.getAllReviews();
    const allReviews = response.data;
    
    // 過濾出目前這本書的評論
    reviews.value = allReviews.filter(r => r.bookId == props.bookId);

    // 依照時間排序 (新的在上面)
    reviews.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  } catch (error) {
    console.error("取得評論失敗:", error);
  }
};

// --- 監聽 bookId 改變 ---
watch(() => props.bookId, () => {
  fetchReviews();
});

// --- 初始化 ---
onMounted(() => {
  fetchReviews();
});

// --- 計算平均分數 ---
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0;
  const sum = reviews.value.reduce((acc, curr) => acc + curr.rating, 0);
  return (sum / reviews.value.length).toFixed(1);
});

// --- 格式化日期 ---
const formatDate = (dateArrayOrString) => {
  if (!dateArrayOrString) return '';
  
  let date;
  if (Array.isArray(dateArrayOrString)) {
    // 處理 Java 陣列格式 [year, month, day, hour, minute, second]
    date = new Date(
      dateArrayOrString[0],
      dateArrayOrString[1] - 1,
      dateArrayOrString[2],
      dateArrayOrString[3],
      dateArrayOrString[4],
      dateArrayOrString[5] || 0
    );
  } else {
    date = new Date(dateArrayOrString);
  }

  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// --- 送出評論 ---
const submitReview = async () => {
  // 1. 重置錯誤狀態
  errors.value.rating = false;
  errors.value.comment = false;

  let hasError = false;

  // 2. 驗證 Rating (必填)
  if (newReview.value.rating === 0) {
    errors.value.rating = true;
    hasError = true;
  }

  // 3. ▼▼▼ 新增驗證：Comment (必填且不能只有空白) ▼▼▼
  if (!newReview.value.comment || !newReview.value.comment.trim()) {
    errors.value.comment = true;
    hasError = true;
  }

  // 如果有任何錯誤，就停止執行
  if (hasError) {
    return;
  }

  isSubmitting.value = true;
  
  // 準備要送給後端的資料
  const payload = {
    userId: 1, // ★★★ 之後記得改成動態抓取當前使用者的 ID
    bookId: props.bookId,
    rating: newReview.value.rating,
    comment: newReview.value.comment
  };
  try {
    const response = await reviewService.createReview(payload);
    
    // 成功後，把回傳的新資料加到列表最上方
    reviews.value.unshift(response.data);

    Swal.fire({
      icon: 'success',
      title: '評論已送出',
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 1500
    });

    // 重置表單
    newReview.value.rating = 0;
    newReview.value.comment = "";
  } catch (error) {
    console.error(error);
    Swal.fire({
      icon: 'error',
      title: '評論失敗',
      text: '系統發生錯誤，請稍後再試'
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 處理檢舉
const handleReport = async (review) => {
  const { value: reason } = await Swal.fire({
    title: '檢舉評論',
    text: `您確定要檢舉 User ${review.userId} 的評論嗎？請選擇原因：`,
    input: 'select',
    inputOptions: {
      'spam': '垃圾廣告訊息',
      'inappropriate': '不當內容或人身攻擊',
      'spoiler': '劇透未標示',
      'other': '其他原因'
    },
    inputPlaceholder: '請選擇檢舉原因',
    showCancelButton: true,
    confirmButtonText: '送出檢舉',
    cancelButtonText: '取消',
    confirmButtonColor: '#d33',
    cancelButtonColor: '#aaa',
    inputValidator: (value) => {
      return !value && '請選擇一個原因！'
    }
  });

  if (reason) {
    console.log(`已檢舉評論 ID: ${review.reviewId}, 原因: ${reason}`);
    Swal.fire({
      icon: 'success',
      title: '檢舉已送出',
      text: '我們會盡快審核您的回報，謝謝！',
      timer: 2000,
      showConfirmButton: false
    });
  }
};
</script>

<style scoped>
/* Vuetify 已經處理大部分樣式，這裡只需微調 */
.text-error {
  animation: shake 0.3s;
}

@keyframes shake {
  0% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
  100% { transform: translateX(0); }
}
</style>