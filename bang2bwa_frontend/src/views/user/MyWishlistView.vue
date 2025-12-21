<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// [변경] 새로운 API 함수 import
import { getMyWishProductList } from '@/api/myPageApi'

const router = useRouter()
const wishItems = ref([])
const loading = ref(true)

// 데이터 로딩
onMounted(async () => {
  const response = await getMyWishProductList();
  
  // 백엔드 응답 구조에 따라 데이터 추출 (List<ProductDto>)
  // 만약 ApiResponse로 감싸져 있다면 response.data, 그냥 리스트면 response
  if (response) {
    // API 응답이 { message: "...", data: [...] } 형태인 경우를 대비해
    // response.data가 배열인지 확인하거나 response 자체가 배열인지 확인
    wishItems.value = Array.isArray(response) ? response : (response.data || []);
  }
  loading.value = false;
})

// [Helper 1] 숫자 포맷팅 (예: 10000 -> 10,000)
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toLocaleString();
}

// [Helper 2] 가격 표시 로직 (ProductDto 필드 활용)
const getPriceTag = (item) => {
  const tradeType = item.tradeType; // SALE, LEASE, RENT

  if (tradeType === 'SALE') {
    return `매매 ${formatNumber(item.dealAmount)}`;
  } else if (tradeType === 'LEASE') {
    return `전세 ${formatNumber(item.deposit)}`;
  } else if (tradeType === 'RENT') {
    return `월세 ${formatNumber(item.deposit)} / ${formatNumber(item.monthlyRent)}`;
  }
  return '';
}

// [Helper 3] 주택 유형 한글 변환
const getHouseTypeName = (type) => {
  const typeMap = {
    'APART': '아파트',
    'OFFICETEL': '오피스텔',
    'ONE_ROOM': '원룸',
  };
  return typeMap[type] || type;
}

// [Helper 4] 이미지 URL 추출 (ProductImageDto 구조에 따라 수정 필요)
const getImageUrl = (images) => {
  if (images && images.length > 0) {
    // ProductImageDto에 savePath, saveName, url 중 어떤 필드가 있는지 확인 필요.
    // 일단 url이나 savePath로 가정합니다.
    return images[0].url || images[0].savePath || 'https://via.placeholder.com/300'; 
  }
  return 'https://via.placeholder.com/300?text=No+Image';
}
</script>

<template>
  <div class="app-main bg-[#FFFBE8]">
    
    <div class="wishlist-wrapper">
      
      <div class="wishlist-header">
        <h2 class="wishlist-title">내 찜 목록</h2>
        <span class="wishlist-count">총 {{ wishItems.length }}개</span>
      </div>

      <div v-if="!loading && wishItems.length > 0" class="wishlist-grid">
        
        <div 
          v-for="item in wishItems" 
          :key="item.productId" 
          class="wish-card group"
          @click="router.push(`/product/${item.productId}`)"
        >
          <div class="card-img-box">
            <img :src="getImageUrl(item.images)" alt="매물 사진" class="card-img" />
          </div>
          
          <div class="card-info">
            <div class="flex justify-between items-start">
              <span class="card-type">{{ getHouseTypeName(item.houseType) }}</span>
              
              <i class="fa-solid fa-heart text-red-500 hover:scale-110 transition cursor-pointer"></i>
            </div>
            
            <h3 class="card-price mt-1 text-[#AE8B72]">
                {{ getPriceTag(item) }}
            </h3>
            
            <p class="card-title mt-1 text-lg">
              {{ item.name }} 
              <span v-if="item.aptDong" class="text-sm font-normal text-gray-500 ml-1">
                {{ item.aptDong }}
              </span>
            </p>

            <p class="text-xs text-gray-400 mt-2">
              {{ item.sggNm }} {{ item.umdNm }} | {{ item.floor }}층
            </p>
          </div>
        </div>

      </div>

      <div v-else-if="!loading" class="wish-empty-state">
        <i class="fa-regular fa-heart text-5xl mb-4 text-gray-300"></i>
        <p>아직 찜한 매물이 없습니다.</p>
        <button @click="router.push('/')" class="btn-go-home">
          매물 보러 가기
        </button>
      </div>

    </div>
  </div>
</template>