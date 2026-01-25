import axios from 'axios'

const API_URL = '/api/clubs'
const apiClient = axios.create({
  baseURL: API_URL,
  withCredentials: true,
})

// Request interceptor: 自動帶入 JWT Token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.data) {
      const { code, message } = error.response.data
      if (code && message) {
        console.error(`[BookClubService] Error Code: ${code}, Message: ${message}`)
        const formattedError = new Error(`[${code}] ${message}`)
        formattedError.originalError = error
        return Promise.reject(formattedError)
      }
    }
    return Promise.reject(error)
  },
)

export default {
  // 取得所有讀書會
  getAllClubs() {
    return apiClient.get('/allClubs')
  },

  // 取得單筆讀書會
  getClub(id) {
    return apiClient.get(`/clubs/${id}`)
  },

  // 刪除讀書會
  deleteClub(id) {
    return apiClient.delete(`/delete/${id}`)
  },

  // 更新讀書會 (包含狀態審核，需帶入完整 Bean 與 檔案)
  // 注意：後端 Controller 需要 Multipart 格式
  updateClub(id, clubData, proposalFile, proofFile) {
    const formData = new FormData()
    const jsonBlob = new Blob([JSON.stringify(clubData)], { type: 'application/json' })

    // 注意後端 @RequestPart("data")
    formData.append('data', jsonBlob)

    if (proposalFile) {
      formData.append('proposal', proposalFile)
    }

    if (proofFile) {
      formData.append('proof', proofFile)
    }

    // 使用 PUT 方法
    return apiClient.put(`/update/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
