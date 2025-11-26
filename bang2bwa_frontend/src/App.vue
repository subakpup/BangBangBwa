<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute(); // 현재 경로 정보를 가져오는 훅(Hook)

// 현재 라우트의 meta 정보에 hideHeader가 있는지 확인
// true면 헤더를 숨겨야 하므로, !를 붙여서 "숨김이 아닐 때만 보여줘"로 논리를 만듭니다.
const showHeader = computed(() => !route.meta.hideHeader); 
</script>

<template>
  <div id="app" class="h-screen flex flex-col overflow-hidden font-sans bg-[#FFFBE8]">

    <header v-if="showHeader"
      class="bg-white border-b border-[#E3C9A5] h-16 flex items-center justify-between px-6 shadow-sm z-20">
      <div class="flex items-center gap-8">
        <router-link to="/" class="text-2xl font-bold text-[#AE8B72] flex items-center gap-2">
          방방봐
        </router-link>

        <nav class="hidden md:flex items-center gap-6 text-gray-600 font-medium">
          <router-link to="/estates" class="hover:text-[#AE8B72] transition">아파트</router-link>
          <router-link to="/board" class="hover:text-[#AE8B72] transition">게시판</router-link>
        </nav>
      </div>

      <div class="flex items-center gap-4">
        <button class="text-gray-600 hover:text-[#AE8B72]">로그인</button>
        <button class="bg-[#AE8B72] text-white px-4 py-2 rounded-lg text-sm hover:opacity-90">회원가입</button>
      </div>
    </header>

    <main class="flex-1 flex flex-col overflow-hidden">
      <RouterView />
    </main>

  </div>
</template>