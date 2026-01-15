<template>
  <div class="admin-page">
    <h1>評論管理</h1>

    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>書名</th>
          <th>使用者</th>
          <th>評分</th>
          <th>內容</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="review in reviews" :key="review.reviewId">
          <td>{{ review.reviewId }}</td>
          <td>{{ review.bookName }}</td>
          <td>{{ review.userName }}</td>
          <td>{{ review.rating }}</td>
          <td>{{ review.comment }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const reviews = ref([])

onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/public/admin/reviews')
    reviews.value = await res.json()
    console.log('reviews:', reviews.value)
  } catch (err) {
    console.error('取得評論失敗', err)
  }
})
</script>
