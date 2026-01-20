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
            <div v-if="errors.rating" class="text-error text-caption text-red">
              請點選星星給予評分
            </div>
          </div>

          <v-textarea
            v-model="newReview.comment"
            label="寫下您的心得..."
            placeholder="這本書最讓你印象深刻的是什麼？"
            variant="outlined"
            rows="3"
            auto-grow
            counter="500"
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
        >
          送出評論
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-divider class="my-6"></v-divider>

    <div v-if="reviews.length > 0">
      <v-card
        v-for="review in reviews"
        :key="review.review_id"
        class="mb-4"
        variant="flat"
        border
      >
        <v-card-text>
          <div class="d-flex flex-row">
    <div class="mr-4">
      <v-avatar color="secondary" size="48">
        <span class="text-h6 text-white">{{ review.user_id }}</span>
      </v-avatar>
    </div>

    <div class="flex-grow-1">
      <div class="d-flex justify-space-between align-start">
        <div>
          <div class="text-subtitle-1 font-weight-bold">
            User {{ review.user_id }}
          </div>
          <div class="text-caption text-grey">
            {{ formatDate(review.created_at) }}
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
            icon="mdi-flag-variant-outline"
            variant="text"
            size="small"
            color="grey-lighten-1"
            class="ml-2"
            title="檢舉此評論"
            @click="handleReport(review)"
          >
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
import { ref, computed } from 'vue';
import Swal from 'sweetalert2'; // 引用專案中的 SweetAlert2

// --- 接收 Props (預留給之後書籍詳情頁傳入 book_id) ---
const props = defineProps({
  bookId: {
    type: [Number, String],
    default: 1 // 測試用預設值
  }
});

// --- Mock Data (模擬資料庫回傳) ---
const reviews = ref([
  {
    review_id: 1,
    user_id: 101,
    book_id: 1,
    rating: 5,
    comment: "內容非常精彩，整本書都讓我停不下來！使用 Vuetify 做介面真的很漂亮。",
    created_at: "2025-01-12T14:32:10"
  },
  {
    review_id: 2,
    user_id: 205,
    book_id: 1,
    rating: 3,
    comment: "還可以，部分內容有點普通。",
    created_at: "2025-02-14T20:41:03"
  },
  {
    review_id: 3,
    user_id: 33,
    book_id: 1,
    rating: 4,
    comment: "", // 測試空評論
    created_at: "2025-01-30T12:55:19"
  }
]);

// --- 表單狀態 ---
const valid = ref(false);
const newReview = ref({
  rating: 0,
  comment: ""
});
const errors = ref({
  rating: false
});

// --- 計算屬性 ---
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0;
  const sum = reviews.value.reduce((acc, curr) => acc + curr.rating, 0);
  return (sum / reviews.value.length).toFixed(1);
});

// --- 方法 ---

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 送出評論
const submitReview = () => {
  // 1. 驗證: Rating 必填
  if (newReview.value.rating === 0) {
    errors.value.rating = true;
    return;
  }
  errors.value.rating = false;

  // 2. 模擬送後端 payload
  const mockPayload = {
    review_id: Date.now(), // 用時間戳當臨時 ID
    user_id: 888, // 模擬當前登入者
    book_id: props.bookId,
    rating: newReview.value.rating,
    comment: newReview.value.comment,
    created_at: new Date().toISOString()
  };

  // 3. 更新畫面 (加到最上面)
  reviews.value.unshift(mockPayload);

  // 4. 跳出成功提示 (使用 SweetAlert2)
  Swal.fire({
    icon: 'success',
    title: '評論已送出',
    text: '感謝您的分享！',
    timer: 1500,
    showConfirmButton: false,
    confirmButtonColor: '#a5886d' // 配合你的主題色
  });

  // 5. 重置表單
  newReview.value.rating = 0;
  newReview.value.comment = "";
};
// 處理檢舉
const handleReport = async (review) => {
  // 1. 彈出選單詢問原因
  const { value: reason } = await Swal.fire({
    title: '檢舉評論',
    text: `您確定要檢舉 User ${review.user_id} 的評論嗎？請選擇原因：`,
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
    confirmButtonColor: '#d33', // 紅色代表警告動作
    cancelButtonColor: '#aaa',
    inputValidator: (value) => {
      return !value && '請選擇一個原因！'
    }
  });

  // 2. 如果使用者有選原因並送出
  if (reason) {
    // 這裡未來會呼叫後端 API，例如: reviewService.reportReview(review.review_id, reason)
    console.log(`已檢舉評論 ID: ${review.review_id}, 原因: ${reason}`);

    // 3. 顯示成功訊息
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