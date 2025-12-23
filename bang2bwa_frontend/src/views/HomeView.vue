<template>
  <div class="home-container">

    <FilterBar ref="filterBar" @filter-change="handleFilterChange" @open-ai="handleOpenAi" />

    <div class="content-wrapper">

      <aside class="sidebar custom-scrollbar">
        <div v-if="!selectProperty" class="flex flex-col h-full w-full">
          
          <div class="sidebar-header">
            <h2 class="sidebar-title">
              <span>{{ currentType }}</span>
              <span class="sidebar-count">({{ productList.length }}개)</span>
            </h2>
          </div>

          <div class="list-wrapper">
            <ProductList 
              :items="productList" 
              :loading="loading"
              :is-last-page="isLastPage"
              @item-click="handleItemClick" 
              @load-more="handleLoadMore"
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

      <div class="map-container">
        <KakaoMap 
          ref="kakaoMapRef" 
          :items="productList" 
          @marker-click="handleItemClick"
          @bounds-changed="handleMapBoundsChanged"
        />
      </div>

    </div>

    <Teleport to="body">
      <AiModal
        :show="showAiModal" 
        @close="showAiModal = false" 
        @search="handleAiSearchResult" 
      />
    </Teleport>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';

import { searchProducts } from '@/api/productApi';              
import { tradeTypeMap, typeMap } from '@/utils/productUtil';      
import KakaoMap from '@/components/map/KakaoMap.vue';             
import FilterBar from '@/components/home/FilterBar.vue';          
import AiModal from '@/components/modal/AiModal.vue';             
import ProductList from '@/components/home/ProductList.vue';      
import ProductDetail from '@/components/home/ProductDetail.vue';  

const route = useRoute();        
const currentType = ref('');      
const productList = ref([]);      
const showAiModal = ref(false);   
const filterBar = ref(null);      
const selectProperty = ref(null); 
const kakaoMapRef = ref(null);

// 무한 스크롤 및 검색 상태 관리
const loading = ref(false);
const isLastPage = ref(false);

const isMovingToTarget = ref(false);

// 현재 검색 조건을 저장하는 객체
const currentSearchParams = ref({
  keyword: '',
  houseType: '',
  tradeType: '',
  excluUseAr: '',
  floor: '',
  minLat: null, maxLat: null,
  minLng: null, maxLng: null,
  page: 1,
  limit: 20
});

/**
 * 통합 검색 함수
 * @param {Object} filterData - 필터바에서 넘어온 데이터
 * @param {Boolean} isReset - 리스트 초기화 여부 (true: 새로 검색, false: 더보기)
 */
const executeSearch = async (filterData = {}, isReset = true) => {
  if (loading.value || (!isReset && isLastPage.value)) return;

  loading.value = true;

  try {
    // 1. 검색 조건 업데이트
    if (filterData) {
      if (filterData.keyword !== undefined) currentSearchParams.value.keyword = filterData.keyword;
      if (filterData.excluUseAr !== undefined) currentSearchParams.value.excluUseAr = filterData.excluUseAr;
      if (filterData.floor !== undefined) currentSearchParams.value.floor = filterData.floor;
      
      // 라우트 쿼리 or 필터값 적용
      const houseType = route.query.type || '';
      const tradeType = tradeTypeMap[filterData.tradeType] || '전체';
      
      currentSearchParams.value.houseType = houseType;
      currentSearchParams.value.tradeType = tradeType;
    }

    // 2. 리셋이면 페이지 1로 초기화
    if (isReset) {
      currentSearchParams.value.page = 1;
      isLastPage.value = false;
      // productList.value = []; // 깜빡임 방지
    }

    // 3. API 호출
    const response = await searchProducts(currentSearchParams.value);

    if (response.success === 'SUCCESS') {
      const newItems = response.data;

      if (newItems.length < currentSearchParams.value.limit) {
        isLastPage.value = true;
      }

      if (isReset) {
        productList.value = newItems; // 교체
        updateTitle(filterData);      // 타이틀 업데이트
      } else {
        productList.value = [...productList.value, ...newItems]; // 추가
      }

      // 다음 페이지 준비
      currentSearchParams.value.page++;

    } else {
      console.error(response.message);
    }
  } catch (error) {
    console.error('검색 실패', error);
  } finally {
    loading.value = false;
  }
};

// 타이틀 업데이트 로직 분리
const updateTitle = (filterData) => {
  if (filterData && filterData.keyword) {
    currentType.value = `'${filterData.keyword}' 검색 결과`;
  } else {
    const typeKey = route.query.type || 'APART';
    currentType.value = typeMap[typeKey];
  }
};

// ====================================================
// 핸들러 함수
// ====================================================

// 1. 필터 바 변경 감지 -> 재검색 (Reset)
const handleFilterChange = (filterData) => {
  executeSearch(filterData, true);
};

// 2. 무한 스크롤 요청 -> 이어보기 (No Reset)
const handleLoadMore = () => {
  console.log("스크롤 바닥 감지! 더 불러옵니다...");
  executeSearch(null, false); // 기존 조건 유지, 페이지만 증가
};

// 3. 지도 영역 변경 감지 -> 재검색 (Reset)
const handleMapBoundsChanged = (bounds) => {
  
  if (isMovingToTarget.value) {
    isMovingToTarget.value = false; // 다음 이동을 위해 플래그 초기화
    return;
  }

  currentSearchParams.value.minLat = bounds.minLat;
  currentSearchParams.value.maxLat = bounds.maxLat;
  currentSearchParams.value.minLng = bounds.minLng;
  currentSearchParams.value.maxLng = bounds.maxLng;

  executeSearch(null, true);
};

// 4. AI 모달 요청
const handleOpenAi = () => {
  showAiModal.value = true;
};

// 5. AI 분석 결과 처리
const handleAiSearchResult = async (aiResultList) => {
  showAiModal.value = false;
  productList.value = aiResultList;
  currentType.value = `AI 맞춤 추천 결과`;
  selectProperty.value = null;
  isLastPage.value = true; // AI 결과는 페이징 안 함

  await nextTick();

  if (aiResultList.length > 0 && kakaoMapRef.value) {
    const topPick = aiResultList[0];
    kakaoMapRef.value.selectItem(topPick);
  }
};

// 6. 매물 클릭
const handleItemClick = (item) => {
  selectProperty.value = item;
  if (kakaoMapRef.value) {
    isMovingToTarget.value = true;
    kakaoMapRef.value.selectItem(item);
  }
};

// 7. 상세 페이지 닫기
const closeDetail = () => {
  selectProperty.value = null;
  if (kakaoMapRef.value) {
    kakaoMapRef.value.resetSelection();
  }
};

// URL 변경 감지
watch(
  () => route.query.type,
  (newType) => {
    if (filterBar.value) {
      filterBar.value.resetFilter();
    }
    selectProperty.value = null;
    // 타입 변경 시에도 필터 초기화 후 검색 실행
    handleFilterChange({ houseType: newType });
  }
);

// 초기 진입 시
onMounted(() => {
  handleFilterChange({});
});
</script>