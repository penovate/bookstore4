// import axios from '@/api/axios'

export const getAllReviews = () => {
//   return axios.get('/api/admin/reviews')
return Promise.resolve({
    data: [
      {
        id: 1,
        bookName: 'Spring 入門',
        userName: 'James',
        rating: 5,
        content: '超好看',
      },
    ],
  })
}
