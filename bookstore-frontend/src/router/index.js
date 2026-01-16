import { createRouter, createWebHistory } from 'vue-router'
import Swal from 'sweetalert2'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dev/user/home',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/admin/LoginView.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/admin/Home.vue'),
    },
    {
      // 平行測試路由
      path: '/dev/admin',
      component: () => import('../views/Layout/AdminLayout.vue'),
      children: [
        {
          path: 'books',
          name: 'admin-books',
          component: () => import('../views/admin/books/BooksHome.vue'),
        },
        {
          path: 'books/insert',
          name: 'admin-books-insert',
          component: () => import('../views/admin/books/insertBook.vue'),
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
          path: 'users',
          name: 'userList',
          component: () => import('../views/admin/users/UserList.vue'),
        },
        {
          path: 'users/get/:id',
          name: 'userDetail',
          component: () => import('../views/admin/users/GetUser.vue'),
        },
        {
          path: 'users/insert',
          name: 'userInsert',
          component: () => import('../views/admin/users/UserInsert.vue'),
        },
        {
          path: 'users/update/:id',
          name: 'userUpdate',
          component: () => import('../views/admin/users/UserUpdate.vue'),
        },
        {
        path: 'reviews',
        name: 'admin-reviews',
        component: () => import('../views/admin/reviews/ReviewList.vue'),
        },
        // 暫用開始行
        {
        path: 'reviews/:id',
        name: 'review-detail',
        component: () => import('../views/admin/reviews/GetReview.vue'),
        },
        {
        path: 'reviews/insert',
        name: 'review-insert',
        component: () => import('../views/admin/reviews/ReviewInsert.vue'),
        },
        {
        path: 'reviews/:id/update',
        name: 'review-update',
        component: () => import('../views/admin/reviews/ReviewUpdate.vue'),
        },
        // 暫用結束行
        {
          path: 'users/logs',
          name: 'admin-operation-logs',
          component: () => import('../views/admin/users/UserLogList.vue'),
        },
      ],
    },
    {
      path: '/dev/user',
      component: () => import('../views/Layout/UserLayout.vue'),
      children: [
        {
          path: 'home',
          name: 'userHome',
          component: () => import('../views/public/HomePage.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  const role = localStorage.getItem('userRole')

  const isAdminRoute = to.path.startsWith('/dev/admin') || to.name === 'home'

  if (isAdminRoute) {
    if (!token) {
      next({ name: 'login' })
    } else if (role === 'SUPER_ADMIN' || role === 'ADMIN') {
      next()
    } else {
      Swal.fire('權限不足', '您沒有進入後台管理系統的權限', 'error')
      next({ name: 'userHome' })
    }
    return
  }

  if (to.name === 'login') {
    if (token && (role === 'SUPER_ADMIN' || role === 'ADMIN')) {
      next({ name: 'home' })
    } else {
      next()
    }
    return
  }

  next()
})

export default router
