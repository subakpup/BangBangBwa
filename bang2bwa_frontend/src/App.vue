<template>
  <div class="h-screen flex flex-col overflow-hidden font-sans bg-bg">
    
    <TheHeader />

    <main class="flex-1 flex flex-col overflow-hidden relative">
      <RouterView />
    </main>

  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import TheHeader from '@/components/common/TheHeader.vue'
</script>