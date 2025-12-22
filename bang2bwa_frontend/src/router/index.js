import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import SignupView from '@/views/user/SignupView.vue'
import LoginView from '@/views/user/LoginView.vue'
import ProductManageView from '@/views/product/ProductManageView.vue'
import ProductFormView from '@/views/product/ProductFormView.vue'
import MyPageView from '@/views/user/MyPageView.vue'
import MyWishlistView from '@/views/user/MyWishlistView.vue'
import MyPageEditView from '@/views/user/MyPageEditView.vue'
import MyPasswordChangeView from '@/views/user/MyPasswordChangeView.vue'

import BoardView from "@/views/board/BoardView.vue";
import BoardList from '@/components/board/BoardList.vue'
import BoardDetail from '@/components/board/BoardDetail.vue';
import BoardWrite from '@/components/board/BoardWrite.vue';



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
      path: "/board",
      component: BoardView,
      children: [
        {
          path: "",
          name: "board-list",
          component: BoardList,
        },
        {
          path: ":id",
          name: "board-detail",
          component: BoardDetail,
        },
        {
          path: "write",
          name: "board-write",
          component: BoardWrite,
        },
        {
          path: "edit/:id",
          name: "board-edit",
          component: BoardWrite,
        },
      ],
    },
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
});

export default router