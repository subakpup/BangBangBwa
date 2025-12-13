<template>
  <div class="list-container">
    <div v-if="items.length > 0" class="space-y-3">
      <div v-for="item in items" :key="item.productId" 
        @click="emit('item-click', item)" 
        class="product-card">
        
        <div class="card-img-box">
          <img :src="item.image" class="card-img" alt="방 사진">
        </div>
        
        <div class="card-info">
          <span class="card-type">{{ typeMap[item.houseType] || item.houseType }}</span>
          <h3 class="card-price">{{ formatPrice(item) }}</h3>
          <p class="card-title">{{ item.name }}</p>
          <p class="text-sm text-gray-500 truncate">{{ item.jibun }}</p>
          <p class="text-xs text-gray-400 mt-1 truncate">
            {{ item.floor }} | {{ item.excluUseAr }}㎡ | {{ item.desc }}
          </p>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>등록된 매물이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { formatPrice, typeMap } from '@/utils/productUtil';

const props = defineProps(['items']); // productList를 받음
const emit = defineEmits(['item-click']); // 클릭 시 부모에게 알림
</script>