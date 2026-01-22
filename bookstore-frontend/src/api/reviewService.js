import axios from 'axios'

const API_URL = '/api/public/admin/reviews'

export default {
  // 1. 取得所有評論
  getAllReviews() {
    return axios.get(API_URL)
  },

  // 2. 新增評論
  createReview(reviewData) {
    return axios.post(API_URL, reviewData)
  },

  // 3. 編輯評論 
  updateReview(id, reviewData) {
    return axios.put(`${API_URL}/${id}`, reviewData)
  },

  // 4. 刪除評論
  deleteReview(id) {
    return axios.delete(`${API_URL}/${id}`)
  }
}

