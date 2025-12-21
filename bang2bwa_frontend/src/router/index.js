import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import SignupView from '@/views/user/SignupView.vue'
import LoginView from '@/views/user/LoginView.vue'
import MyPageView from '@/views/user/MyPageView.vue'
import MyWishlistView from '@/views/user/MyWishlistView.vue'
import MyPageEditView from '@/views/user/MyPageEditView.vue'
import MyPasswordChangeView from '@/views/user/MyPasswordChangeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/signup",
      name: "signup",
      component: SignupView,
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/mypage",
      name: "mypage",
      component: MyPageView,
    },
    {
      path: "/mypage/wishlist",
      name: "wishlist",
      component: MyWishlistView,
    },
    {
      path: "/mypage/edit",
      name: "myedit",
      component: MyPageEditView,
    },
    {
      path: "/mypage/edit/password",
      name: "password-change",
      component: MyPasswordChangeView,
    },
  ],
});

export default router