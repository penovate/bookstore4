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
            counter="250"
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
        v-for="review in paginatedReviews"
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
                  <div class="d-flex align-center">
                    <div class="text-subtitle-1 font-weight-bold">
                      {{ review.userName || `User ${review.userId}` }}
                    </div>
                    <v-rating
                      :model-value="review.rating"
                      color="amber-darken-2"
                      density="comfortable"
                      readonly
                      size="x-small"
                      class="ml-2"
                    ></v-rating>
                  </div>

                  <div class="text-caption text-grey mt-1">
                    {{ formatDate(review.createdAt) }}
                    <span v-if="review.updatedAt && review.updatedAt !== review.createdAt" class="ml-2 text-grey-lighten-1">
                      (已編輯)
                    </span>
                  </div>
                </div>
                
                <div class="d-flex align-center">
                  <template v-if="isOwner(review)">
                    <v-btn
                      variant="text"
                      size="small"
                      color="primary"
                      icon
                      class="mr-1"
                      @click="openEditDialog(review)"
                    >
                      <v-icon>mdi-pencil</v-icon>
                      <v-tooltip activator="parent" location="top">編輯</v-tooltip>
                    </v-btn>

                    <v-btn
                      variant="text"
                      size="small"
                      color="error"
                      icon
                      class="mr-1"
                      @click="handleDelete(review)"
                    >
                      <v-icon>mdi-delete</v-icon>
                      <v-tooltip activator="parent" location="top">刪除</v-tooltip>
                    </v-btn>
                  </template>

                  <v-btn
                    variant="text"
                    size="small"
                    color="grey-lighten-1"
                    icon
                    @click="handleReport(review)"
                  >
                    <span class="mdi mdi-alert-circle-outline" style="font-size: 20px;"></span>
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

      <div class="d-flex justify-center mt-6">
        <v-pagination
          v-model="page"
          :length="totalPages"
          :total-visible="7"
          color="primary"
          rounded="circle"
        ></v-pagination>
      </div>
    </div>

    <v-sheet
      v-else
      class="d-flex flex-column align-center justify-center py-8 text-grey"
    >
      <v-icon icon="mdi-message-text-outline" size="64" class="mb-2"></v-icon>
      <div>目前還沒有評論，搶先成為第一個評論者吧！</div>
    </v-sheet>

    <v-dialog v-model="editDialog" max-width="500">
      <v-card>
        <v-card-title class="bg-primary text-white">編輯評論</v-card-title>
        <v-card-text class="pt-4">
          <v-form ref="editForm">
            <div class="mb-2">
              <div class="text-subtitle-1 mb-1">調整評分</div>
              <v-rating
                v-model="editingData.rating"
                color="amber-darken-2"
                hover
                density="comfortable"
              ></v-rating>
            </div>
            <v-textarea
              v-model="editingData.comment"
              label="評論內容"
              variant="outlined"
              rows="3"
              counter="500"
              auto-grow
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="editDialog = false">取消</v-btn>
          <v-btn color="primary" variant="elevated" @click="submitEdit" :loading="isUpdating">儲存編輯</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import Swal from 'sweetalert2';
import reviewService from '@/api/reviewService.js';
import { useUserStore } from '@/stores/userStore';

const props = defineProps({
  bookId: {
    type: [Number, String],
    default: 1
  }
});

const userStore = useUserStore();

// --- 狀態 ---
const reviews = ref([]);
const isSubmitting = ref(false);
const valid = ref(false);

// --- 分頁 ---
const page = ref(1);        
const itemsPerPage = 5;

// --- 編輯 ---
const editDialog = ref(false);
const isUpdating = ref(false);
const editingData = ref({
  reviewId: null,
  rating: 0,
  comment: ''
});

const newReview = ref({
  rating: 0,
  comment: ""
});
const errors = ref({
  rating: false,
  comment: false
});

// --- JWT 解碼小工具 (不用改其他檔案的關鍵！) ---
const parseJwt = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

// 統一取得當前登入者 ID 的 Computed 屬性
const currentUserId = computed(() => {
  let id = userStore.userId || localStorage.getItem('userId');
  if (!id) {
    const token = localStorage.getItem('userToken');
    if (token) {
      const decoded = parseJwt(token);
      if (decoded) id = decoded.userId || decoded.id || decoded.sub;
    }
  }
  return id ? Number(id) : null;
});

// 判斷評論是否為當前登入者所有
const isOwner = (review) => {
  if (!currentUserId.value) return false;
  return review.userId === currentUserId.value;
};

// --- 從後端撈資料 ---
const fetchReviews = async () => {
  try {
    const response = await reviewService.getAllReviews();
    const allReviews = response.data;
    
    // 過濾出目前這本書的評論
    reviews.value = allReviews.filter(r => r.bookId == props.bookId);

    // 依照時間排序 (新的在上面)
    reviews.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    // 每次重新抓資料時，回到第一頁
    page.value = 1;

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

// 分頁計算邏輯
const totalPages = computed(() => {
  return Math.ceil(reviews.value.length / itemsPerPage);
});

// 目前這一頁該顯示哪些資料
const paginatedReviews = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return reviews.value.slice(start, end);
});

// --- 格式化日期 ---
const formatDate = (dateArrayOrString) => {
  if (!dateArrayOrString) return '';
  
  let date;

  if (Array.isArray(dateArrayOrString)) {
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

  // 2. 驗證 Rating (必填)
  if (newReview.value.rating === 0) {
    errors.value.rating = true;
  }

  // 3. ▼▼▼ 新增驗證：Comment (必填且不能只有空白) ▼▼▼
  if (!newReview.value.comment || !newReview.value.comment.trim()) {
    errors.value.comment = true;
  }

  // 如果有任何錯誤，就停止執行
  if (errors.value.rating || errors.value.comment) return;

  isSubmitting.value = true;
  
  // 從 Pinia 狀態管理中取得當前登入者資訊
  let userName = userStore.userName || localStorage.getItem('userName');

  // 防呆：如果 Store 裡沒有 ID (代表可能沒登入或狀態遺失)
  if (!currentUserId) {
      Swal.fire({
          icon: 'warning',
          title: '請先登入',
          text: '登入後才能發表評論',
          confirmButtonText: '前往登入',
          confirmButtonColor: '#2E5C43'
      }).then((result) => {
          if (result.isConfirmed) { 
              window.location.href = '/dev/user/login'; // 直接跳轉
          }
      });
      isSubmitting.value = false;
      return;
  }

  // 準備要送給後端的資料
  const payload = {
    userId: Number(currentUserId.value), 
    bookId: props.bookId,
    rating: newReview.value.rating,
    comment: newReview.value.comment
  };
  
  try {
    const response = await reviewService.createReview(payload);
    const newReviewData = response.data;
    
    if (!newReviewData.userName && userName) {
    newReviewData.userName = userName;
    }

    // 成功後，把回傳的新資料加到列表最上方
    reviews.value.unshift(newReviewData);

    // 送出後跳回第一頁
    page.value = 1;

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

// 編輯功能邏輯
const openEditDialog = (review) => {
  editingData.value = {
    reviewId: review.reviewId,
    rating: review.rating,
    comment: review.comment
  };
  editDialog.value = true;
};

const submitEdit = async () => {
  if (!editingData.value.comment || !editingData.value.comment.trim()) {
     Swal.fire({ icon: 'error', title: '內容不能為空', timer: 1500, showConfirmButton: false });
     return;
  }

  isUpdating.value = true;
  try {
    // 呼叫 updateReview (對應 reviewService.js)
    const payload = {
      bookId: props.bookId, 
      userId: currentUserId.value,
      rating: editingData.value.rating,
      comment: editingData.value.comment
    };

    await reviewService.updateReview(editingData.value.reviewId, payload);

    // 更新前端資料
    const targetReview = reviews.value.find(r => r.reviewId === editingData.value.reviewId);
    if (targetReview) {
      targetReview.rating = editingData.value.rating;
      targetReview.comment = editingData.value.comment;
      targetReview.updatedAt = new Date().toISOString(); // 更新時間
    }

    editDialog.value = false;
    Swal.fire({ icon: 'success', title: '修改成功', toast: true, position: 'top-end', showConfirmButton: false, timer: 1500 });

  } catch (error) {
    console.error(error);
    Swal.fire('錯誤', '更新失敗，請稍後再試。', 'error');
  } finally {
    isUpdating.value = false;
  }
};

// =======================
// ★ 新增: 刪除功能邏輯
// =======================
const handleDelete = (review) => {
  Swal.fire({
    title: '確定要刪除嗎？',
    text: "刪除後將無法復原這則評論！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#aaa',
    confirmButtonText: '是的，刪除',
    cancelButtonText: '取消'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        // 呼叫 deleteReview (對應 reviewService.js)
        await reviewService.deleteReview(review.reviewId);
        
        // 前端移除資料
        const index = reviews.value.findIndex(r => r.reviewId === review.reviewId);
        if (index !== -1) {
          reviews.value.splice(index, 1);
        }

        Swal.fire('已刪除', '您的評論已成功刪除。', 'success');
        
        // 檢查分頁：若刪除後該頁為空，且不是第一頁，往前跳一頁
        if (paginatedReviews.value.length === 0 && page.value > 1) {
          page.value--;
        }

      } catch (error) {
        console.error(error);
        Swal.fire('錯誤', '刪除失敗，請稍後再試。', 'error');
      }
    }
  });
};

// 處理檢舉
const handleReport = async (review) => {
  const { value: reason } = await Swal.fire({
    title: '檢舉評論',
    text: `您確定要檢舉 ${review.userName || 'User ' + review.userId} 的評論嗎？請選擇原因：`,
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