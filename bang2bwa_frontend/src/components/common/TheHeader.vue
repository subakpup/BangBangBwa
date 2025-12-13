<template>
  <header class="header-container">
    <div class="flex items-center gap-8">
        <RouterLink to="/" class="header-logo">
            <Home class="w-8 h-8" /> 방방봐
        </RouterLink>
        
        <nav class="header-nav">
            <RouterLink :to="{ path: '/', query: { type: 'APART' }}" class="nav-link">아파트</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'ONEROOM' }}" class="nav-link">원룸</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'OFFICETEL' }}" class="nav-link">오피스텔</RouterLink>
            
            <RouterLink to="/notice" class="nav-link">공지사항</RouterLink>
            <RouterLink to="/board" class="nav-link">게시판</RouterLink>
        </nav>
    </div>

    <div class="header-auth">
        <button class="btn-login">로그인</button>
        <RouterLink to="/signup" class="btn-signup">회원가입</RouterLink>
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { Home } from 'lucide-vue-next'
</script>