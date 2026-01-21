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

  // 3. 刪除評論 (未來可能用到)
  deleteReview(id) {
    return axios.delete(`${API_URL}/${id}`)
  }
}
// export const getAllReviews = () => {
// //   return axios.get('/api/admin/reviews')
// return Promise.resolve({
//     data: [
//       {
//         id: 1,
//         bookName: 'Spring 入門',
//         userName: 'James',
//         rating: 5,
//         content: '超好看',
//       },
//     ],
//   })
// }
