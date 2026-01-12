import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import Swal from 'sweetalert2'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

import App from './App.vue'
import router from './router'

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('userToken')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userName')

      Swal.fire({
        icon: 'warning',
        title: '登入已過期',
        text: '請重新登入系統',
        confirmButtonColor: '#a5886d',
      }).then(() => {
        window.location.href = '/login'
      })
    }
    return Promise.reject(error)
  },
)

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
  },
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vuetify)

app.mount('#app')
