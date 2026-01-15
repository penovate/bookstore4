import { createRouter, createWebHistory } from 'vue-router'
import Swal from 'sweetalert2'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
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
      ],
    },
    {
      path: '/dev/user',
      component: () => import('../views/Layout/UserLayout.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  const role = localStorage.getItem('userRole')

  if (to.name !== 'login') {
    if (!token) {
      next({ name: 'login' })
    } else {
      if (role === 'SUPER_ADMIN' || role === 'ADMIN') {
        next()
      } else {
        Swal.fire('權限不足', '您沒有進入後台管理系統的權限', 'error')
        next({ name: 'login' })
      }
    }
  } else {
    if (token && (role === 'SUPER_ADMIN' || role === 'ADMIN')) {
      next({ name: 'home' })
    } else {
      next()
    }
  }
})

export default router
