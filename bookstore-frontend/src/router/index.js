import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../apps/users/LoginView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../apps/users/LoginView.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../apps/Home.vue'),
    },
    {
      path: '/users',
      name: 'usersHome',
      component: () => import('../apps/users/UsersHome.vue'),
    },
    {
      path: '/users/list',
      name: 'userList',
      component: () => import('../apps/users/UserList.vue'),
    },
    {
      path: '/users/get/:id',
      name: 'userDetail',
      component: () => import('../apps/users/GetUser.vue'),
    },
    {
      path: '/users/insert',
      name: 'userInsert',
      component: () => import('../apps/users/UserInsert.vue'),
    },
    {
      path: '/users/update/:id',
      name: 'userUpdate',
      component: () => import('../apps/users/UserUpdate.vue'),
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
          path: 'orders',
          name: 'admin-orders',
          component: () => import('../views/admin/orders/OrdersHome.vue'),
        }
      ]
    },
  ],
})

export default router
