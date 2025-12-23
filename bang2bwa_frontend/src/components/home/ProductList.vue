<template>
  <div class="list-container h-full overflow-y-auto custom-scrollbar p-2">
    
    <div v-if="items && items.length > 0" class="space-y-4">
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
              <p class="text-xs text-gray-500 truncate mt-0.5">{{ formatAddress(item) }}</p>
              <p class="text-xs text-gray-400 mt-1 truncate">
                {{ item.floor }} | {{ item.excluUseAr }}㎡
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

    <div v-else-if="!loading" class="h-full flex flex-col items-center justify-center text-gray-500 space-y-2 py-10">
      <SearchX class="w-12 h-12 text-gray-300" />
      <p>조건에 맞는 매물이 없습니다.</p>
    </div>

    <div ref="observerTarget" class="py-6 flex justify-center items-center w-full">
      <div v-if="loading" class="flex items-center gap-2 text-violet-600 text-sm font-medium">
        <Loader2 class="w-5 h-5 animate-spin" />
        <span>매물 불러오는 중...</span>
      </div>
      <div v-else-if="isLastPage && items.length > 0" class="text-xs text-gray-400">
        마지막 매물입니다.
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { formatPrice, typeMap, formatAddress } from '@/utils/productUtil';
import { Sparkles, Bot, SearchX, Loader2 } from 'lucide-vue-next';

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  isLastPage: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['item-click', 'load-more']);

const observerTarget = ref(null);
let observer = null;

const startObservation = () => {
  if (observer) observer.disconnect();

  observer = new IntersectionObserver((entries) => {
    // 타겟이 화면에 보이고, 로딩 중이 아니며, 마지막 페이지가 아닐 때
    if (entries[0].isIntersecting && !props.loading && !props.isLastPage) {
      emit('load-more');
    }
  }, {
    threshold: 0.1, // 타겟이 10% 보이면 실행
    rootMargin: '100px' // 바닥에 닿기 100px 전 미리 로딩
  });

  if (observerTarget.value) {
    observer.observe(observerTarget.value);
  }
};

onMounted(() => {
  startObservation();
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});

// 아이템이 바뀌면 옵저버 재연결 (안정성 확보)
watch(() => props.items, () => {
    // DOM 업데이트 후 옵저버가 타겟을 놓칠 수 있으므로 재설정
    setTimeout(startObservation, 100);
});
</script>

<style scoped>
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