import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import SignupView from '@/views/user/SignupView.vue'
import LoginView from '@/views/user/LoginView.vue'

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
  ],
});

export default router