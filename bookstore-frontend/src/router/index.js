import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/public/users/LoginView.vue'),
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
  ],
})

export default router
