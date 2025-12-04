<script setup>
import { RouterLink } from 'vue-router'
import { Home } from 'lucide-vue-next'
</script>

<template>
  <header class="bg-white border-b border-accent h-16 flex items-center justify-between px-6 shadow-sm z-20">
    <div class="flex items-center gap-8">
        <RouterLink to="/" class="text-2xl font-bold text-primary flex items-center gap-2">
            <Home class="w-8 h-8" /> 방방봐
        </RouterLink>
        
        <nav class="hidden md:flex items-center gap-6 text-gray-600 font-medium">
            <RouterLink :to="{ path: '/', query: { type: 'apart' }}" class="hover:text-primary transition">아파트</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'oneroom' }}" class="hover:text-primary transition">원룸</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'officetel' }}" class="hover:text-primary transition">오피스텔</RouterLink>
            
            <RouterLink to="/notice" class="hover:text-primary transition">공지사항</RouterLink>
            <RouterLink to="/board" class="hover:text-primary transition">게시판</RouterLink>
        </nav>
    </div>

    <div class="flex items-center gap-4">
        <button class="text-gray-600 hover:text-primary font-medium">로그인</button>
        <button class="bg-primary text-white px-4 py-2 rounded-lg text-sm hover:opacity-90 transition shadow-sm">회원가입</button>
    </div>
  </header>
</template>