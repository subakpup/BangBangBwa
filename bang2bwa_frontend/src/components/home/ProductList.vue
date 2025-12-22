<template>
  <div class="list-container h-full">
    <div v-if="items && items.length > 0" class="space-y-4 pb-4">

      <div 
        v-for="item in items" 
        :key="item.productId" 
        @click="emit('item-click', item)"
        class="product-card group relative bg-white transition-all duration-300 hover:shadow-lg cursor-pointer flex flex-col"
        :class="{ 'ai-card-border': item.aiRecommended }"
      >
        <div v-if="item.aiRecommended" class="absolute -top-3 -left-2 z-20 animate-bounce-slight">
          <span class="bg-gradient-to-r from-violet-600 to-indigo-600 text-white text-xs font-bold px-3 py-1.5 rounded-full shadow-lg flex items-center gap-1 border border-white/20">
            <Sparkles class="w-3 h-3 text-yellow-300" /> AI 강력추천
          </span>
        </div>

        <div class="flex p-4 gap-4 items-start">
          
          <div class="w-24 h-24 flex-shrink-0 bg-gray-100 rounded-lg overflow-hidden relative border border-gray-100">
            <img 
              :src="item.image || '/src/assets/default_room.png'"
              class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" 
              alt="방 사진"
            >
          </div>

          <div class="flex-1 min-w-0 flex flex-col justify-between h-full">
            <div>
              <div class="flex justify-between items-start">
                <span class="text-[10px] font-bold text-gray-500 bg-gray-100 px-2 py-0.5 rounded">
                  {{ typeMap[item.houseType] || item.houseType }}
                </span>
              </div>

              <h3 class="text-lg font-bold text-gray-900 mt-1 truncate">{{ formatPrice(item) }}</h3>
              <p class="text-sm text-gray-800 font-medium truncate">{{ item.name }}</p>
              <p class="text-xs text-gray-500 truncate mt-0.5">{{ item.jibun }}</p>
              <p class="text-xs text-gray-400 mt-1 truncate">
                {{ item.floor }}층 | {{ item.excluUseAr }}㎡
              </p>
            </div>
            </div>
          
        </div> 
        <div v-if="item.aiRecommended" class="px-4 pb-4 mt-auto">
          <div class="bg-violet-50 rounded-lg p-3 border border-violet-100 w-full">
            <p class="text-xs text-violet-700 font-semibold flex items-start gap-1.5 leading-relaxed break-keep">
              <Bot class="w-4 h-4 flex-shrink-0 mt-0.5" />
              <span>{{ item.aiReason }}</span>
            </p>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="h-full flex flex-col items-center justify-center text-gray-500 space-y-2 py-10">
      <SearchX class="w-12 h-12 text-gray-300" />
      <p>조건에 맞는 매물이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { formatPrice, typeMap } from '@/utils/productUtil';
import { Sparkles, Bot, SearchX } from 'lucide-vue-next';

const props = defineProps(['items']);
const emit = defineEmits(['item-click']);
</script>

<style scoped>
/* ✨ AI 추천 카드 전용 스타일 */
.ai-card-border {
  position: relative;
  border-radius: 0.75rem;
  background: linear-gradient(#fff, #fff) padding-box,
              linear-gradient(135deg, #7c3aed 0%, #2563eb 100%) border-box;
  border: 3px solid transparent; 
  box-shadow: 0 4px 20px -2px rgba(124, 58, 237, 0.15);
  transform: translateY(0);
  overflow: visible; 
  margin-top: 10px;
  margin-bottom: 10px;
  z-index: 10;
}

.ai-card-border:hover {
  box-shadow: 0 10px 25px -5px rgba(124, 58, 237, 0.3);
  transform: translateY(-2px);
  z-index: 20;
}

.product-card:not(.ai-card-border) {
  border: 1px solid #f3f4f6;
  border-radius: 0.75rem;
}

@keyframes bounce-slight {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

.animate-bounce-slight {
  animation: bounce-slight 2s infinite ease-in-out;
}
</style>