import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import SignupView from '@/views/user/SignupView.vue'
import LoginView from '@/views/user/LoginView.vue'
// import ProductManageView from '@/views/product/ProductManageView.vue'
import ProductFormView from '@/views/product/ProductFormView.vue'
import MyPageView from '@/views/user/MyPageView.vue'
import MyWishlistView from '@/views/user/MyWishlistView.vue'
import MyPageEditView from '@/views/user/MyPageEditView.vue'
import MyPasswordChangeView from '@/views/user/MyPasswordChangeView.vue'

import BoardView from "@/views/board/BoardView.vue";
import BoardList from '@/components/board/BoardList.vue'
import BoardDetail from '@/components/board/BoardDetail.vue';
import BoardWrite from '@/components/board/BoardWrite.vue';
import ReservationRequest from '@/views/reservation/ReservationRequest.vue'
import ReservationPayment from '@/views/reservation/ReservationPayment.vue'
import ReservationAction from '@/views/reservation/ReservationAction.vue'
import MyProductListView from '@/views/MyProductListView.vue'



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
    // 1. 예약 요청 페이지 (매물 ID를 받음)
    {
      path: "/reservation/request/:propertyId",
      name: "reservation-request",
      component: ReservationRequest,
      props: true, // 매물 정보를 props나 history state로 넘기기 위함
    },
    // 2. 결제 페이지
    {
      path: "/reservation/payment",
      name: "reservation-payment",
      component: ReservationPayment,
    },
    // 3. 만남 확인/신고 페이지 (예약 ID를 받음)
    {
      path: "/reservation/action/:reservationId",
      name: "reservation-action",
      component: ReservationAction,
    },
    {
    // 내 매물 관리 (중개인 전용)
    path: '/agent/products',
    name: 'my-product-list',
    component: MyProductListView,
    // (선택사항) 중개인만 접근 가능하도록 메타 데이터 설정
    meta: { auth: true, role: 'AGENT' } 
  	},
  ],
    // {
    //   path: '/product/manage',
    //   name: 'productManage',
    //   component: ProductManageView
    // },
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