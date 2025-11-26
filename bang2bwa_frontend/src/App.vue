<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav>
      <RouterLink to="/">방방봐 홈</RouterLink>
    </nav>
  </header>

  <main>
    <RouterView />
  </main>
</template>

<style scoped>
/* 간단한 스타일 */
header {
  background-color: #f8f9fa;
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}
nav a {
  text-decoration: none;
  color: #333;
  margin: 0 10px;
  font-weight: bold;
}
main {
  padding: 20px;
}
</style>