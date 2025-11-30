<script setup>
import KakaoMap from '@/components/map/KakaoMap.vue';
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // 현재 URL 정보

const route = useRoute(); // Spring의 HttpServletRequest
const currentType = ref(''); // 현재 보고 있는 매물 타입

const typeMap = {
  'apt': '아파트',
  'oneroom': '원룸',
  'officetel': '오피스텔'
};

const loadData = (rawType) => {
  const typeName = typeMap[rawType] || '전체 매물';
  currentType.value = typeName;

  // TODO: 나중에 여기에 axios로 백엔드에 요청 보낼 때도 rawType('APT')를 보내면 됨!
};

// URL의 쿼리 파라미터가 바뀔 때마다 실행
watch(
  () => route.query.type, 
  (newType) => {
    loadData(newType);
  }
);

onMounted(() => {
  const type = route.query.type;
  loadData(type);
});
</script>

<template>
  <div class="flex h-full">
    <aside class="w-[400px] bg-white border-r p-4">
      <h2 class="text-xl font-bold mb-4">
        <span class="text-primary">{{ currentType }}</span>
      </h2>
      <div class="p-4 bg-gray-100 rounded">
        {{ currentType }} 매물 1...
      </div>
    </aside>

    <div class="flex-1 bg-gray-200 flex items-center justify-center">
      <KakaoMap />
    </div>
  </div>
</template>