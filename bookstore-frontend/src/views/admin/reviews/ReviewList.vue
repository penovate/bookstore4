<template>
  <div class="center-body">
    <div align="center">
      <h2>所有評價</h2>

      <table border="1">
        <thead>
          <tr>
            <th class="status-cell">評價編號</th>
            <th class="status-cell">會員編號</th>
            <th class="status-cell">會員名稱</th>
            <th class="status-cell">書本編號</th>
            <th>書本名稱</th>
            <th class="status-cell">評分</th>
            <th>評價</th>
            <th>創建日期</th>
            <th>修改資料</th>
            <th>刪除資料</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="review in reviews" :key="review.reviewId">
            <td>
              <router-link :to="`/dev/admin/reviews/${review.reviewId}`">
                {{ review.reviewId }}
              </router-link>
            </td>
            <td>{{ review.userId }}</td>
            <td>{{ review.userName }}</td>
            <td>{{ review.bookId }}</td>
            <td>{{ review.bookName }}</td>
            <td>{{ review.rating }}</td>
            <td>{{ review.comment }}</td>
            <td>{{ review.createdAt }}</td>
            <td>
              <router-link :to="`/dev/admin/reviews/${review.reviewId}/update`">
                <button class="update-button">修改</button>
              </router-link>
            </td>
            <td>
              <button class="delete-button" @click="deleteReview(review.reviewId)">刪除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <h3>共 {{ reviews.length }} 筆評價資料</h3>

      <router-link to="/dev/admin/reviews/insert">
        <button class="system-button add-button">新增評價</button>
      </router-link>
      <router-link to="/home">
        <button class="system-button back-to-center-button">回到首頁</button>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const reviews = ref([])
const error = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await fetch('/api/public/admin/reviews')

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }

    reviews.value = await res.json()
  } catch (err) {
    console.error('載入評價失敗:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
})

const deleteReview = async (reviewId) => {
  const confirmed = confirm('確定要刪除這筆評價嗎？')
  if (!confirmed) return

  try {
    const res = await fetch(`/api/public/admin/reviews/${reviewId}`, {
      method: 'DELETE',
    })

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }

    // 前端即時移除，不影響其他功能
    reviews.value = reviews.value.filter((review) => review.reviewId !== reviewId)

    alert('刪除成功')
  } catch (err) {
    console.error('刪除評價失敗:', err)
    alert('刪除失敗，請查看後端或 console')
  }
}
</script>

<style scoped>
.center-body {
  font-family: '微軟正黑體', 'Arial', sans-serif;
  background-color: #fcf8f0;
  color: #4a4a4a;
  padding: 40px 0;
  min-height: 100vh;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border: 1px solid #e0d9c9;
  padding: 12px 10px;
}

th {
  background-color: #e8e4dc;
}

tr:nth-child(even) {
  background-color: #f7f3f0;
}

.update-button {
  background-color: #9fb89e;
}

.delete-button {
  background-color: #d89696;
  color: white;
}

.system-button {
  margin: 10px;
}

.add-button {
  background-color: #a07d58;
  color: white;
}

.back-to-center-button {
  background-color: #e8e4dc;
}
</style>
