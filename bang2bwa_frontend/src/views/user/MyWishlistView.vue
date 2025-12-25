<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// [변경] 새로운 API 함수 import
import { getMyWishProductList } from '@/api/myPageApi'
import { removeWishList } from '@/api/productApi'
import { Heart } from 'lucide-vue-next'
import { getProductMainImage, typeMap, formatPrice } from '@/utils/productUtil'

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

const handleRemoveWish = async (item) => {
  if( !confirm(`${item.name} 매물을 찜 목록에서 삭제하시겠습니까?`)) return;

  const response = await removeWishList(item.productId);

  if(response && response.success === "SUCCESS") {
    wishItems.value = wishItems.value.filter(p => p.productId !== item.productId);
  }

  alert(response.message || "삭제에 실패하였습니다.");
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
          class="wish-card group relative"
          @click="router.push(`/product/${item.productId}`)"
        >
          <div class="card-img-box">
            <img :src="getProductMainImage(item)" alt="매물 사진" class="card-img" />
          </div>

          <button 
               class="absolute top-2 right-2 bg-white/90 rounded-full p-2 hover:bg-red-50 transition z-10 shadow-sm group/btn"
               @click.stop="handleRemoveWish(item)"
             >
               <Heart class="w-5 h-5 fill-red-500 text-red-500 group-hover/btn:scale-110 transition-transform" />
             </button>
          
          <div class="card-info">
            <div class="flex justify-between items-start">
              <span class="card-type">{{ typeMap[item.houseType] || item.houseType }}</span>
              
              <i class="fa-solid fa-heart text-red-500 hover:scale-110 transition cursor-pointer"></i>
            </div>
            
            <h3 class="card-price mt-1 text-[#AE8B72]">
                {{ formatPrice(item) }}
            </h3>
            
            <p class="card-title mt-1 text-lg">
              {{ item.name }} 
              <span v-if="item.aptDong" class="text-sm font-normal text-gray-500 ml-1">
                {{ item.aptDong }}
              </span>
            </p>

            <p class="text-xs text-gray-400 mt-2">
              {{ item.sggNm }} {{ item.umdNm }} | {{ item.floor }}
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