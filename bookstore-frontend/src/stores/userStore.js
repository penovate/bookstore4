import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const name = ref(localStorage.getItem('userName') || '訪客')
  const role = ref(localStorage.getItem('userRole') || '')
  const isLoggedIn = ref(!!localStorage.getItem('userToken'))

  const login = (userData) => {
    name.value = userData.userName
    role.value = userData.role
    isLoggedIn.value = true
    localStorage.setItem('userToken', userData.token)
    localStorage.setItem('userRole', userData.role)
    localStorage.setItem('userName', userData.userName)
  }

  const logout = () => {
    name.value = '訪客'
    role.value = ''
    isLoggedIn.value = false
    localStorage.clear()
  }

  return { name, role, isLoggedIn, login, logout }
})
