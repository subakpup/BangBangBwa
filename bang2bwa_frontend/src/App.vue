<template>
  <div class="app-layout">
    
    <TheHeader />

    <main class="app-main">
      <RouterView />
    </main>

  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import TheHeader from '@/components/common/TheHeader.vue'
</script>