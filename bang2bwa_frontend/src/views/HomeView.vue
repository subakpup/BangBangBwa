<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

import KakaoMap from '@/components/map/KakaoMap.vue';     // 카카오맵
import FilterBar from '@/components/home/FilterBar.vue';  // 홈뷰 헤더(필터 바)
import AiModal from '@/components/modal/AiModal.vue';     // AI 모달

import { dummyData } from '@/data/dummy';                 // 더미 데이터

const route = useRoute();            // Spring의 HttpServletRequest
const currentType = ref('전체 매물');  // 현재 보고 있는 매물 타입
const productList = ref([]);         // 매물 리스트
const showAiModal = ref(false);      // 모달 상태

// URL 파라미터 매핑
const typeMap = {
  'APART': '아파트',
  'ONEROOM': '원룸',
  'OFFICETEL': '오피스텔'
};

// 거래 종류 매핑
const tradeTypeMap = {
  '매매': 'SALE',
  '전세': 'LEASE',
  '월세': 'RENT',
};

// 데이터 로딩
const loadData = (rawType) => {
  const typeKey = rawType || 'APART';

  const typeName = typeMap[typeKey] || '전체 매물';
  currentType.value = typeName;

  productList.value = dummyData[typeKey] || [];
};

// 필터 바 변경 감지
const handleFilterChange = async (filterData) => {
  try {
    const houseType = route.query.type || '';
    const tradeType = tradeTypeMap[filterData.tradeType];

    const response = await axios.post('http://localhost:8080/products/search', {
      keyword: filterData.keyword,
      houseType: houseType,
      tradeType: tradeType,
      excluUseAr: filterData.excluUseAr,
      floor: filterData.floor,
    });

    // 응답처리
    if (response.data.success === 'SUCCESS') {
        const searchList = response.data.data;
        productList.value = searchList;
        
        if (filterData.keyword) {
            currentType.value = `'${filterData.keyword}' 검색 결과`;
        } else {
          const typeKey = houseType || 'APART';
          currentType.value = typeMap[typeKey];
        }
    } else {
        alert(response.data.message); // 실패 메시지 띄우기
    }
    
  } catch (error) {
    console.error('검색 실패', error);
    productList.value = [];
  }
};

// AI 모달 요청
const handleOpenAi = () => {
  showAiModal.value = true;
};

// AI 분석 결과 처리 함수
const handleAiSearchResult = () => {
  alert('분석 완료!');
}

// 가격 포맷팅 함수
const formatPrice = (item) => {
  if (item.tradeType === '매매') {
    const unit = item.dealAmount >= 10000 ? `${Math.floor(item.dealAmount / 10000)}억` : '';
    const remain = item.dealAmount % 10000 > 0 ? `${item.dealAmount % 10000}만` : '';
    return `매매 ${unit}${remain}`;
  }
  else if (item.tradeType === '전세') {
    const unit = item.dealAmount >= 10000 ? `${Math.floor(item.dealAmount / 10000)}억` : '';
    const remain = item.dealAmount % 10000 > 0 ? `${item.dealAmount % 10000}만` : '';
    return `전세 ${unit}${remain}`;
  }
  else {
    return `월세 ${item.deposit}/${item.monthlyRent}`;
  }
}

// URL의 쿼리 파라미터가 바뀔 때마다 실행
watch(
  () => route.query.type,
  (newType) => {
    loadData(newType);

    handleFilterChange({ houseType: newType });
  }
);

// 처음 접속 시 실행
onMounted(() => {
  handleFilterChange({});
});
</script>

<template>
  <div class="flex flex-col h-full w-full relative">

    <FilterBar @filter-change="handleFilterChange" @open-ai="handleOpenAi" />

    <div class="flex flex-1 overflow-hidden relative">

      <aside class="w-[400px] bg-white border-r p-4 flex-shrink-0 z-10 overflow-y-auto">
        <h2 class="text-xl font-bold mb-4">
          <span class="text-primary">{{ currentType }}</span> 리스트
          <span class="text-sm text-gray-500 font-normal">({{ productList.length }}개)</span>
        </h2>

        <div v-if="productList.length > 0" class="space-y-3">
          <div v-for="item in productList" :key="item.productId"
            class="flex gap-3 border p-3 rounded-lg hover:border-primary hover:shadow-md cursor-pointer transition bg-white">
            <div class="w-24 h-24 bg-gray-200 rounded-md flex-shrink-0 overflow-hidden">
              <img :src="item.image" class="w-full h-full object-cover" alt="방 사진">
            </div>
            <div class="flex flex-col justify-center">
              <span class="text-xs text-primary font-bold">{{ item.houseType }}</span>
              <h3 class="font-bold text-lg">{{ formatPrice(item) }}</h3>
              <p class="text-sm text-gray-600 font-bold">{{ item.name }}</p>
              <p class="text-sm text-gray-500 truncate">{{ item.jibun }}</p>
              <p class="text-xs text-gray-400 mt-1">
                {{ item.floor }}층 | {{ item.excluUseAr }}㎡ | {{ item.desc }}
              </p>
            </div>
          </div>
        </div>

        <div v-else class="h-64 flex flex-col items-center justify-center text-gray-400">
          <p>등록된 매물이 없습니다.</p>
        </div>
      </aside>

      <div class="flex-1 bg-gray-100 relative z-0">
        <KakaoMap :items="productList" />
      </div>

    </div>
    <Teleport to="body">
      <AiModal :show="showAiModal" @close="showAiModal = false" @search="handleAiSearchResult" />
    </Teleport>
  </div>
</template>