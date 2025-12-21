import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import SignupView from '@/views/user/SignupView.vue'
import LoginView from '@/views/user/LoginView.vue'
import ProductManageView from '@/views/product/ProductManageView.vue'
import ProductFormView from '@/views/product/ProductFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/product/manage',
      name: 'productManage',
      component: ProductManageView
    },
    {
      path: '/product/register',
      name: 'productRegister',
      component: ProductFormView
    },
    {
      path: '/product/edit/:id',
      name: 'productEdit',
      component: ProductFormView
    },
  ]
})

export default router