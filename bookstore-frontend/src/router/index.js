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
    // --- 後台管理區域 ---
    {
      path: '/dev/admin',
      component: () => import('../views/Layout/AdminLayout.vue'),
      children: [
        // 1. 書籍管理
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
        // 2. 用戶管理
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
          path: 'users/logs',
          name: 'admin-operation-logs',
          component: () => import('../views/admin/users/UserLogList.vue'),
        },
        // 3. 訂單管理
        {
          path: 'orders',
          name: 'orderMenu',
          component: () => import('../views/admin/orders/OrderMenu.vue'),
        },
        {
          path: 'orders/list',
          name: 'orderList',
          component: () => import('../views/admin/orders/OrderList.vue'),
        },
        {
          path: 'orders/insert',
          name: 'orderInsert',
          component: () => import('../views/admin/orders/OrderInsert.vue'),
        },
        {
          path: 'orders/detail/:id',
          name: 'orderDetail-admin',
          component: () => import('../views/admin/orders/OrderDetail.vue'),
        },
        {
          path: 'orders/update/:id',
          name: 'orderUpdate',
          component: () => import('../views/admin/orders/OrderUpdate.vue'),
        },
        {
          path: 'orders/items/add/:id',
          name: 'orderAddItem',
          component: () => import('../views/admin/orders/OrderAddItem.vue'),
        },
        {
          path: 'orders/items/update/:itemId',
          name: 'orderItemUpdate',
          component: () => import('../views/admin/orders/OrderItemUpdate.vue'),
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
      ],
    },
    // --- 前台網站區域 ---
    {
      path: '/dev/user',
      component: () => import('../views/Layout/UserLayout.vue'),
      children: [
        {
          path: 'store',
          name: 'bookStore',
          component: () => import('../views/public/books/BookStore.vue'), //測試購物車用，等宏孝加入書籍前台網頁後刪除
        },
        {
          path: 'home',
          name: 'userHome',
          component: () => import('../views/public/HomePage.vue'),
        },
        {
          path: 'cart',
          name: 'cart',
          component: () => import('../views/public/cart/Cart.vue'),
        },
        {
          path: 'checkout',
          name: 'checkout',
          component: () => import('../views/public/orders/Checkout.vue'),
        },
        {
          path: 'orders',
          name: 'myOrders',
          component: () => import('../views/public/orders/MyOrders.vue'),
        },
      ],
    },
  ],
})

// 路由守衛
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
