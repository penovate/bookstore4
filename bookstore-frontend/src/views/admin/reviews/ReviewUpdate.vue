<template>
  <div class="admin-page">
    <h1>修改評價資料</h1>

    <!-- 主卡片 -->
    <div class="detail-container">
      <table class="detail-table">
        <tbody>
          <tr>
            <th>評價編號</th>
            <td>{{ form.reviewId }}</td>
          </tr>
          <tr>
            <th>會員編號</th>
            <td>
              <input v-model="form.userId" />
            </td>
          </tr>
          <tr>
            <th>會員名稱</th>
            <td>{{ form.userName }}</td>
          </tr>
          <tr>
            <th>書本編號</th>
            <td>
              <input v-model="form.bookId" />
            </td>
          </tr>
          <tr>
            <th>書本名稱</th>
            <td>{{ form.bookName }}</td>
          </tr>
          <tr>
            <th>書本評分</th>
            <td>
              <select v-model="form.rating">
                <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
              </select>
            </td>
          </tr>
          <tr>
            <th>評價內容</th>
            <td>
              <textarea v-model="form.comment" rows="4"></textarea>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 按鈕區 -->
    <div class="action-buttons">
      <button class="system-button primary-action-button" @click="submit">修改評價</button>
      <button class="system-button back-button" @click="goBack">返回所有評價</button>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 👉 目前先用假資料（之後再接後端）
const mockReviews = [
  {
    reviewId: 1,
    userId: 101,
    userName: '王小明',
    bookId: 5001,
    bookName: 'Java 入門',
    rating: 5,
    comment: '很好看',
  },
  {
    reviewId: 2,
    userId: 102,
    userName: '陳小美',
    bookId: 5002,
    bookName: 'Spring Boot 實戰',
    rating: 4,
    comment: '內容扎實',
  },
]

// 根據 route param 找到要修改的那筆
const reviewId = Number(route.params.id)
const origin = mockReviews.find((r) => r.reviewId === reviewId)

// 表單資料（重點）
const form = reactive({ ...origin })

const submit = () => {
  console.log('送出修改資料', form)
  // 之後改成 axios.put(...)
  router.push('/dev/admin/reviews')
}

const goBack = () => {
  router.push('/dev/admin/reviews')
}
</script>

<style>
.admin-page {
  font-family: '微軟正黑體', 'Arial', sans-serif;
  background-color: #fcf8f0;
  color: #4a4a4a;

  min-height: 100vh;
  padding: 40px 0;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.admin-page h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #7b5e47;
  letter-spacing: 2px;
  font-size: 26px;
}

/* 主卡片 */
.detail-container {
  width: 900px;
  max-width: 90%;
  margin: 0 auto;
  background-color: #ffffff;
  border-radius: 6px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.detail-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed; /* ⭐ 防止欄位亂撐 */
}

.detail-table th {
  width: 30%;
  background-color: #e8e4dc;
  padding: 14px;
  text-align: center;
  font-weight: bold;
  color: #4a4a4a;
}

.detail-table td {
  padding: 14px;
  background-color: #faf7f2;
}

.detail-table tr:not(:last-child) th,
.detail-table tr:not(:last-child) td {
  border-bottom: 1px solid #e0d9c9;
}

/* 表單欄位 */
.detail-table input,
.detail-table select,
.detail-table textarea {
  width: 100%;
  box-sizing: border-box; /* ⭐ 關鍵 */
  padding: 8px 10px;
  border: 1px solid #dcd5c7;
  border-radius: 4px;
  font-size: 14px;
}

textarea {
  resize: vertical;
}

/* 按鈕 */
.action-buttons {
  margin-top: 30px;
  text-align: center;
}

.system-button {
  min-width: 160px;
  height: 45px;
  margin: 0 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.primary-action-button {
  background-color: #a07d58;
  color: white;
}

.back-button {
  background-color: #e8e4dc;
}
</style>
