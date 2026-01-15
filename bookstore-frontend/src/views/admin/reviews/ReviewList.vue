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
              <router-link :to="`/admin/reviews/${review.reviewId}`">
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
              <router-link :to="`/admin/reviews/${review.reviewId}/edit`">
                <button class="update-button">修改</button>
              </router-link>
            </td>
            <td>
              <button class="delete-button">刪除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <h3>共 {{ reviews.length }} 筆評價資料</h3>

      <button class="system-button add-button">新增評價資料</button>
      <button class="system-button back-to-center-button">回到評價首頁</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

/**
 * 🚧 暫時假資料（取代 JSP 的 request.getAttribute）
 */
const reviews = ref([
  {
    reviewId: 1,
    userId: 101,
    userName: '王小明',
    bookId: 5001,
    bookName: 'Java 入門',
    rating: 5,
    comment: '很好看',
    createdAt: '2024-01-01 10:30:00',
  },
  {
    reviewId: 2,
    userId: 102,
    userName: '陳小美',
    bookId: 5002,
    bookName: 'Spring Boot 實戰',
    rating: 4,
    comment: '內容扎實',
    createdAt: '2024-01-02 14:20:00',
  },
])
</script>

<style scoped>
/* 👉 這裡你可以「整段」從 JSP 的 <style> 貼過來 */

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
