import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/books': {
        target: 'http://localhost:8080', // 後端位址
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, ''), // 如果後端路徑不需要 /api 前綴可開啟
      },
      // 建議順便加入 user 相關的 api 代理
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/cart': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
    }
  }
})
