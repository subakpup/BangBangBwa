<template>
  <div class="home-container">

    <FilterBar ref="filterBar" @filter-change="handleFilterChange" @open-ai="handleOpenAi" />

    <div class="content-wrapper">

      <aside class="sidebar">
        
        <div v-if="selectProperty" class="h-full w-full">
          <ProductDetail 
            :key="selectProperty.productId"
            :item="selectProperty" 
            @close="closeDetail" 
          />
        </div>

        <div v-else class="flex flex-col h-full w-full">
          
          <div class="sidebar-header" v-if="isSearchMode">
            <h2 class="sidebar-title">
              <span>{{ currentType }}</span>
              <span class="sidebar-count">({{ productList.length }}개)</span>
            </h2>
          </div>

          <div class="list-wrapper" ref="sidebarRef">
            
            <MainHome
              v-if="!isSearchMode"
              @open-ai="handleOpenAi"
            />

            <ProductList 
              ref="productListRef"
              v-else
              :items="productList" 
              :loading="loading"
              :is-last-page="isLastPage"
              @item-click="handleItemClick" 
              @load-more="handleLoadMore"
            />
          </div>
        </div>
      </aside>

      <div class="map-container">
        <KakaoMap 
          ref="kakaoMapRef" 
          :items="productList" 
          @marker-click="handleItemClick"
          @bounds-changed="handleMapBoundsChanged"
          @reset-selection="selectProperty = null"
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
import { ref, watch, onMounted, nextTick, computed } from 'vue';
import { useRoute } from 'vue-router';

import { searchProducts } from '@/api/productApi';              
import { tradeTypeMap, typeMap } from '@/utils/productUtil';      
import KakaoMap from '@/components/map/KakaoMap.vue';             
import FilterBar from '@/components/home/FilterBar.vue';          
import AiModal from '@/components/modal/AiModal.vue';             
import ProductList from '@/components/home/ProductList.vue';      
import ProductDetail from '@/components/home/ProductDetail.vue';
import MainHome from '@/components/home/MainHome.vue';

const route = useRoute();        
const currentType = ref('');      
const productList = ref([]);      
const showAiModal = ref(false);   
const filterBar = ref(null);      
const selectProperty = ref(null); 
const kakaoMapRef = ref(null);
const productListRef = ref(null);
const loading = ref(false);
const isLastPage = ref(false);
const isMovingToTarget = ref(false);
const isAiMode = ref(false);

const isSearchMode = computed(() => {
  return Object.keys(route.query).length > 0;
})

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

const executeSearch = async (filterData = {}, isReset = true) => {
  if (loading.value || (!isReset && isLastPage.value)) return;

  loading.value = true;

  try {
    if (filterData) {
      if (filterData.keyword !== undefined) currentSearchParams.value.keyword = filterData.keyword;
      if (filterData.excluUseAr !== undefined) currentSearchParams.value.excluUseAr = filterData.excluUseAr;
      if (filterData.floor !== undefined) currentSearchParams.value.floor = filterData.floor;
      
      const houseType = route.query.type || '';
      const tradeType = tradeTypeMap[filterData.tradeType] || '전체';
      
      currentSearchParams.value.houseType = houseType;
      currentSearchParams.value.tradeType = tradeType;
    }

    if (isReset) {
      currentSearchParams.value.page = 1;
      isLastPage.value = false;
    }

    const response = await searchProducts(currentSearchParams.value);

    if (response.success === 'SUCCESS') {
      const newItems = response.data;

      if (newItems.length < currentSearchParams.value.limit) {
        isLastPage.value = true;
      }

      if (isReset) {
        productList.value = newItems; 
        updateTitle(filterData);      
      } else {
        productList.value = [...productList.value, ...newItems]; 
      }

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

const updateTitle = (filterData) => {
  if (filterData && filterData.keyword) {
    currentType.value = `'${filterData.keyword}' 검색 결과`;
  } else {
    const typeKey = route.query.type || '';
    currentType.value = typeMap[typeKey] || '검색 결과';
  }
};

const handleFilterChange = (filterData) => {
  isAiMode.value = false;
  executeSearch(filterData, true);
};

const handleLoadMore = () => {
  executeSearch(null, false); 
};

const handleMapBoundsChanged = (bounds) => {
  if (!isSearchMode.value) return;

  if (isMovingToTarget.value) {
    isMovingToTarget.value = false; 
    return;
  }

  if (productListRef.value) {
    productListRef.value.scrollToTop();
  }

  currentSearchParams.value.minLat = bounds.minLat;
  currentSearchParams.value.maxLat = bounds.maxLat;
  currentSearchParams.value.minLng = bounds.minLng;
  currentSearchParams.value.maxLng = bounds.maxLng;

  if (isAiMode.value) {
    return;
  }

  executeSearch(null, true);
};

const handleOpenAi = () => {
  showAiModal.value = true;
};

const handleAiSearchResult = async (aiResultList) => {
  showAiModal.value = false;
  isAiMode.value = true;
  productList.value = aiResultList;
  currentType.value = `AI 맞춤 추천 결과`;
  selectProperty.value = null;
  isLastPage.value = true; 

  await nextTick();

  if (aiResultList.length > 0 && kakaoMapRef.value) {
    const topPick = aiResultList[0];
    kakaoMapRef.value.selectItem(topPick);
  }
};

const handleItemClick = (item) => {
  selectProperty.value = item;
  if (kakaoMapRef.value) {
    isMovingToTarget.value = true;
    kakaoMapRef.value.selectItem(item);
  }
};

const closeDetail = () => {
  selectProperty.value = null;
  if (kakaoMapRef.value) {
    kakaoMapRef.value.resetSelection();
  }
};

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

onMounted(() => {
  if (isSearchMode.value) {
    handleFilterChange({});
  }
});
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 400px;
  background-color: white;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  z-index: 20;
}

.sidebar-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f3f4f6;
  background-color: white;
}

.sidebar-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sidebar-count {
  font-size: 0.875rem;
  color: #ae8b72;
  font-weight: 600;
}

.map-container {
  flex: 1;
  position: relative;
  background-color: #f3f4f6;
}
</style>