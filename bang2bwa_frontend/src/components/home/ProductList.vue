<template>
  <div class="overflow-y-auto custom-scrollbar p-4"> <div v-if="items.length > 0" class="space-y-3">
      <div v-for="item in items" :key="item.productId" 
        @click="emit('item-click', item)" 
        class="flex gap-3 border p-3 rounded-lg hover:border-primary hover:shadow-md cursor-pointer transition bg-white group">
        
        <div class="w-24 h-24 bg-gray-200 rounded-md flex-shrink-0 overflow-hidden">
          <img :src="item.image" class="w-full h-full object-cover group-hover:scale-105 transition duration-500" alt="방 사진">
        </div>
        
        <div class="flex flex-col justify-center flex-1 min-w-0">
          <span class="text-xs text-primary font-bold">{{ typeMap[item.houseType] || item.houseType }}</span>
          <h3 class="font-bold text-lg truncate">{{ formatPrice(item) }}</h3>
          <p class="text-sm text-gray-600 font-bold truncate">{{ item.name }}</p>
          <p class="text-sm text-gray-500 truncate">{{ item.jibun }}</p>
          <p class="text-xs text-gray-400 mt-1 truncate">
            {{ item.floor }} | {{ item.excluUseAr }}㎡ | {{ item.desc }}
          </p>
        </div>
      </div>
    </div>

    <div v-else class="h-64 flex flex-col items-center justify-center text-gray-400">
      <p>등록된 매물이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { formatPrice, typeMap } from '@/utils/productUtil';

const props = defineProps(['items']); // productList를 받음
const emit = defineEmits(['item-click']); // 클릭 시 부모에게 알림
</script>