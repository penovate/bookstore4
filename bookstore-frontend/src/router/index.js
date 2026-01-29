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

        // 進貨管理
        {
          path: 'logs',
          name: 'admin-logs',
          component: () => import('../views/admin/logs/StockLogsHome.vue'),
        },
        {
          path: 'logs/:id',
          name: 'admin-logs-detail',
          component: () => import('../views/admin/logs/StockLogDetail.vue'),
        },
        {
          path: 'logs/insert',
          name: 'admin-logs-insert',
          component: () => import('../views/admin/logs/InsertStockLog.vue'),
        },
        // 2. 用戶管理
        {
          path: 'users',
          name: 'userList',
          component: () => import('../views/admin/users/UserList.vue'),
          meta: { title: '會員管理列表' },
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
        // 2.5 優惠券管理
        {
          path: 'coupons',
          name: 'admin-coupons',
          component: () => import('../views/admin/coupons/AdminCoupon.vue'),
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
          path: 'logs/update/:id',
          name: 'admin-logs-update',
          component: () => import('../views/admin/logs/updateLogDetail.vue'),
        },
        {
          path: 'reports',
          name: 'admin-reports',
          component: () => import('../views/admin/logs/SalesData.vue'),
        },
        {
          path: 'bookclubs/insert',
          name: 'admin-bookclubs-insert',
          component: () => import('../views/admin/bookClubs/insertBookClub.vue'),
        },
        {
          path: 'bookclubs',
          name: 'admin-bookclubs',
          component: () => import('../views/admin/bookClubs/AdminBookClub.vue'),
        },
        {
          path: 'bookclubs/review/:id',
          name: 'admin-bookclubs-review',
          component: () => import('../views/admin/bookClubs/AdminBookClubDetail.vue'),
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
        // 4.評價管理
        {
          path: 'reviews',
          name: 'admin-reviews',
          component: () => import('../views/admin/reviews/ReviewList.vue'),
        },
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
      ],
    },
    // --- 前台網站區域 ---
    {
      path: '/dev/user',
      component: () => import('../views/Layout/UserLayout.vue'),
      children: [
        {
          path: 'login',
          name: 'user-login',
          component: () => import('../views/public/user/UserLogin.vue'),
        },
        {
          path: 'profile',
          name: 'user-profile',
          component: () => import('../views/public/user/UserProfile.vue'),
        },
        {
          path: 'register',
          name: 'user-register',
          component: () => import('../views/public/user/UserRegister.vue'),
        },
        {
          path: 'forgetpassword',
          name: 'user-forget-password',
          component: () => import('../views/public/user/UserForgetPwd.vue'),
        },
        {
          path: 'reset-password-by-email',
          name: 'user-reset-password-by-email',
          component: () => import('../views/public/user/ResetPasswordByEmail.vue'),
        },
        {
          path: 'set-new-password',
          name: 'set-new-password',
          component: () => import('../views/public/user/SetNewPassword.vue'),
        },
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
        {
          path: 'store',
          name: 'bookStore',
          component: () => import('../views/public/books/UserBookList.vue'), //測試購物車用，等宏孝加入書籍前台網頁後刪除
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
        {
          path: 'coupons',
          name: 'userCoupons',
          component: () => import('../views/public/user/UserCoupon.vue'),
        },
        // 讀書會專區
        {
          path: 'bookclubs',
          name: 'user-bookclubs',
          component: () => import('../views/public/club/UserBookClub.vue'),
        },
        {
          path: 'bookclubs/insert',
          name: 'user-bookclubs-insert',
          component: () => import('../views/public/club/UserInsertBookClub.vue'),
        },
        {
          path: 'bookclubs/detail/:id',
          name: 'user-bookclub-detail-page',
          component: () => import('../views/public/club/UserBookClubDetail.vue'),
        },
      ],
    },
  ],
})

// 路由守衛
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  const role = localStorage.getItem('userRole')
  const pageTitle = to.meta.title
  document.title = pageTitle
    ? `${pageTitle} | 
  網路書店`
    : '網路書店'

  const isAdminRoute = to.path.startsWith('/dev/admin') || to.name === 'home'

  const isUserProtectedRoute =
    ['myOrders', 'checkout', 'cart', 'userCoupons'].includes(to.name) ||
    to.path.startsWith('/dev/user/orders') ||
    to.path.startsWith('/dev/user/coupons')

  if (isAdminRoute) {
    if (!token) {
      return next({ name: 'login', query: { redirect: to.fullPath } })
    } else if (role === 'SUPER_ADMIN' || role === 'ADMIN') {
      return next()
    } else {
      Swal.fire('權限不足', '您沒有進入後台管理系統的權限', 'error')
      return next({ name: 'userHome' })
    }
  }

  if (isUserProtectedRoute && !token) {
    Swal.fire({
      title: '請先登入',
      text: '登入會員後即可查看',
      icon: 'info',
      confirmButtonColor: '#2e5c43',
    })
    return next({ name: 'user-login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
