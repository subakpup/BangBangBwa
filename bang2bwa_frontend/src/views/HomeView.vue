<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // 현재 URL 정보

import KakaoMap from '@/components/map/KakaoMap.vue';
import FilterBar from '@/components/home/FilterBar.vue';

const route = useRoute(); // Spring의 HttpServletRequest
const currentType = ref('전체 매물'); // 현재 보고 있는 매물 타입

const typeMap = {
  'apt': '아파트',
  'oneroom': '원룸',
  'officetel': '오피스텔'
};

const loadData = (rawType) => {
  const typeName = typeMap[rawType] || '전체 매물';
  currentType.value = typeName;

  // TODO: 나중에 여기에 axios로 백엔드에 요청 보낼 때도 rawType('APT')를 보내면 됨
};

// 필터 바에서 값이 바뀔 때마다 실행되는 함수
const handleFilterChange = (filterData) => {
  console.log('필터 변경 감지:', filterData);
  // 예: { keyword: '강남', type: '전세', floor: '1층', area: '33㎡ 이하' }
  // TODO: 여기서 백엔드 API를 호출해서 filteredList를 갱신하고, 지도 마커를 다시 찍어야 함
};

const handleOpenAi = () => {
  console.log('AI 추천 모달 열기 요청');
  // TODO: showAiModal.value = true;
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
  <div class="flex flex-col h-full w-full relative">
    
    <FilterBar 
      @filter-change="handleFilterChange" 
      @open-ai="handleOpenAi"
    />

    <div class="flex flex-1 overflow-hidden relative">
      
      <aside class="w-[400px] bg-white border-r p-4 flex-shrink-0 z-10 overflow-y-auto">
        <h2 class="text-xl font-bold mb-4">
          <span class="text-primary">{{ currentType }}</span> 리스트
        </h2>
        <div class="p-4 bg-gray-100 rounded text-gray-500 text-center h-32 flex items-center justify-center">
          매물 리스트 컴포넌트 자리
        </div>
      </aside>

      <div class="flex-1 bg-gray-100 relative z-0">
        <KakaoMap />
      </div>

    </div>
  </div>
</template>