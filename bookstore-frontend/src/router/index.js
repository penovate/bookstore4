import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/public/users/LoginView.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/admin/Home.vue'),
    },
    {
      path: '/users',
      name: 'usersHome',
      component: () => import('../views/admin/users/UsersHome.vue'),
    },
    {
      path: '/users/list',
      name: 'userList',
      component: () => import('../views/admin/users/UserList.vue'),
    },
    {
      path: '/users/get/:id',
      name: 'userDetail',
      component: () => import('../views/admin/users/GetUser.vue'),
    },
    {
      path: '/users/insert',
      name: 'userInsert',
      component: () => import('../views/admin/users/UserInsert.vue'),
    },
    {
      path: '/users/update/:id',
      name: 'userUpdate',
      component: () => import('../views/admin/users/UserUpdate.vue'),
    },

    {
      //平行測試路由
      path: '/dev/admin',
      component: () => import('../views/Layout/AdminLayout.vue'),
      // meta: { requiresAuth: true },
      children: [
        {
          path: 'books',
          name: 'admin-books',
          component: () => import('../views/admin/books/BooksHome.vue'), // 您的書籍列表頁
        },
        {
          path: 'books/insert',
          name: 'admin-books-insert',
          component: () => import('../views/admin/books/insertBook.vue'),
        },
        {
          path: 'books/:id',
          name: 'user-book-detail',
          component: () => import('../views/public/books/UserBookDetail.vue'),
        },
        {
          path: 'books/update/:id',
          name: 'admin-books-update',
          component: () => import('../views/admin/books/updateBook.vue'),
        },
        {
          path: 'books/get/:id',
          name: 'admin-books-get',
          component: () => import('../views/admin/books/getBook.vue'),
        },
        {
          path: 'logs',
          name: 'admin-logs',
          component: () => import('../views/admin/logs/StockLogsHome.vue'),
        },
        {
          path: 'logs/insert',
          name: 'admin-logs-insert',
          component: () => import('../views/admin/logs/InsertStockLog.vue'),
        },
        {
          path: 'logs/:id',
          name: 'admin-logs-detail',
          component: () => import('../views/admin/logs/StockLogDetail.vue'),
        },
        {
          path: 'logs/update/:id',
          name: 'admin-logs-update',
          component: () => import('../views/admin/logs/updateLogDetail.vue'),
        },
        {
          path: 'reports',
          name: 'admin-reports',
          component: () => import('../views/admin/logs/SalesData.vue'),
        },
      ],
    },
    {
      path: '/dev/user',
      component: () => import('../views/Layout/UserLayout.vue'),
      children: [
        {
          path: 'books',
          name: 'user-books',
          component: () => import('../views/public/books/UserBookList.vue'),
        },
        {
          path: 'books/:id',
          name: 'user-book-detail',
          component: () => import('../views/public/books/UserBookDetail.vue'),
        },
      ],
    },
  ],
})

export default router
