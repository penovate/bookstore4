import { forestTheme, userTheme } from '@/assets/styles/theme.js'
import '@mdi/font/css/materialdesignicons.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import Swal from 'sweetalert2'
import GoogleSignInPlugin from 'vue3-google-signin'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'

import App from './App.vue'
import router from './router'

const app = createApp(App)

axios.defaults.withCredentials = true;

app.use(GoogleSignInPlugin, {
  clientId: '820056363256-hofl35eid0nukf8283riv209ij3gl6h0.apps.googleusercontent.com',
})

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.clear()
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
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'forestTheme',
    themes: {
      forestTheme,
      userTheme,
    },
  },
})

app.use(createPinia())
app.use(router)
app.use(vuetify)

app.mount('#app')
