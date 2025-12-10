<template>
  <div class="flex flex-col h-full w-full relative">

    <FilterBar ref="filterBar" @filter-change="handleFilterChange" @open-ai="handleOpenAi" />

    <div class="flex flex-1 overflow-hidden relative">

      <aside class="w-[400px] bg-white border-r p-4 flex-shrink-0 z-10 overflow-y-auto">
        <div v-if="!selectProperty" class="flex flex-col h-full w-full">
          
          <div class="p-4 border-b border-gray-100 flex-shrink-0">
            <h2 class="text-xl font-bold">
              <span class="text-primary">{{ currentType }}</span> 리스트
              <span class="text-sm text-gray-500 font-normal">({{ productList.length }}개)</span>
            </h2>
          </div>

          <div class="flex-1 overflow-hidden">
            <ProductList 
              :items="productList" 
              @item-click="handleItemClick" 
            />
          </div>
        </div>

        <div v-else class="h-full w-full">
          <ProductDetail 
            :item="selectProperty" 
            @close="closeDetail" 
          />
        </div>
      </aside>

      <div class="flex-1 bg-gray-100 relative z-0">
        <KakaoMap 
          ref="kakaoMapRef" 
          :items="productList" 
          @marker-click="handleItemClick"/>
      </div>

    </div>
    <Teleport to="body">
      <AiModal
        :show="showAiModal" 
        @close="showAiModal = false" 
        @search="handleAiSearchResult" />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

import { tradeTypeMap, typeMap } from '@/utils/productUtil';
import KakaoMap from '@/components/map/KakaoMap.vue';             // 카카오맵
import FilterBar from '@/components/home/FilterBar.vue';          // 홈뷰 헤더(필터 바)
import AiModal from '@/components/modal/AiModal.vue';             // AI 모달
import ProductList from '@/components/home/ProductList.vue';      // 매물 리스트
import ProductDetail from '@/components/home/ProductDetail.vue';  // 매물 상세 정보

const route = useRoute();         // Spring의 HttpServletRequest
const currentType = ref('');      // 현재 보고 있는 매물 타입
const productList = ref([]);      // 매물 리스트
const showAiModal = ref(false);   // 모달 상태
const filterBar = ref(null);      // 필터바 상태
const selectProperty = ref(null); // 선택된 매물
const kakaoMapRef = ref(null)     // 카카오 맵

// 필터 바 변경 감지
const handleFilterChange = async (filterData) => {
  try {
    const houseType = route.query.type || '';
    const tradeType = tradeTypeMap[filterData.tradeType] || '전체';

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
};

// 매물 클릭 함수
const handleItemClick = (item) => {
  selectProperty.value = item;

  if (kakaoMapRef.value) {
    kakaoMapRef.value.selectItem(item);
  }
};

// 상세 페이지 뒤로 가기
const closeDetail = () => {
  selectProperty.value = null;

  if (kakaoMapRef.value) {
    kakaoMapRef.value.resetSelection();
  }
};

// URL의 쿼리 파라미터가 바뀔 때마다 실행
watch(
  () => route.query.type,
  (newType) => {
    if (filterBar.value) {
      filterBar.value.resetFilter();
    }
    selectProperty.value = null;
    handleFilterChange({ houseType: newType });
  }
);

// 처음 접속 시 실행
onMounted(() => {
  handleFilterChange({});
});
</script>