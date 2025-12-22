<template>
  <div class="app-main bg-[#FFFBE8]">
    <div class="board-wrapper">
      
      <RouterView />
      
    </div>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style scoped>
/* 필요한 경우 트랜지션 효과 등을 추가할 수 있습니다 */
</style>