import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const name = ref(localStorage.getItem('userName') || '訪客')
  const role = ref(localStorage.getItem('userRole') || '')
  const isLoggedIn = ref(!!localStorage.getItem('userToken'))
  const userId = ref(localStorage.getItem('userId') || null)
  const img = ref(localStorage.getItem('userImg') || '')

  const login = (userData) => {
    name.value = userData.userName
    role.value = userData.role
    userId.value = userData.userId
    isLoggedIn.value = true
    img.value = userData.img || ''
    localStorage.setItem('userToken', userData.token)
    localStorage.setItem('userRole', userData.role)
    localStorage.setItem('userName', userData.userName)
    localStorage.setItem('userId', userData.userId)
    if (img.value) {
      localStorage.setItem('userImg', img.value)
    }
  }

  const logout = () => {
    name.value = '訪客'
    role.value = ''
    isLoggedIn.value = false
    userId.value = null
    img.value = ''
    localStorage.clear()
  }

  return { name, role, isLoggedIn, userId, img, login, logout }
})
