<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/productApi'
import { Calendar, Clock, MessageCircle, MapPin, Building } from 'lucide-vue-next'
import { formatPrice, typeMap, tradeTypeMapen2ko, getProductMainImage, formatAddress } from '@/utils/productUtil';

const route = useRoute()
const router = useRouter()

const propertyId = route.params.propertyId
const property = ref({}) // ProductDto 구조가 담길 변수
const loading = ref(true)

// 입력 데이터
const form = ref({
  date: '',   
  time: '',   
  message: '' 
})

// 1. 오늘 날짜와 최대 날짜를 저장할 변수
const minDate = ref('');
const maxDate = ref('');

// 2. 날짜를 'YYYY-MM-DD' 형식으로 변환하는 헬퍼 함수
const formatDate = (date) => {
  const year = date.getFullYear();
  // 월은 0부터 시작하므로 +1, 10보다 작으면 앞에 '0' 붙이기
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

// [초기화] 매물 정보 불러오기
onMounted(async () => {
  loading.value = true;
  const res = await getProductDetail(propertyId);
  
  if (res && res.success) {
    const data = res.data;

    if (data.status !== 'AVAILABLE') {
      alert("현재 예약 가능한 상태가 아닙니다.\n(이미 예약되었거나 거래가 완료된 매물입니다.)");
      router.push({ name: 'home' }); // 홈으로 리다이렉트
      return;
    }

    property.value = data;

    const today = new Date();
    const twoWeeksLater = new Date();
    twoWeeksLater.setDate(today.getDate() + 14);

    minDate.value = formatDate(today);
    maxDate.value = formatDate(twoWeeksLater);
  } else {
    alert("매물 정보를 불러올 수 없습니다.");
    router.back();
  }
  loading.value = false;
})

// 결제 페이지로 이동
const goToPayment = () => {
  if (!form.value.date || !form.value.time) {
    alert("예약 날짜와 시간을 선택해주세요.");
    return;
  }

  // 날짜/시간 포맷팅 (YYYY-MM-DDTHH:mm)
  const reservationDateTime = `${form.value.date}T${form.value.time}`;

  // 다음 단계(결제)로 데이터 전달
  router.push({
    name: 'reservation-payment',
    state: {
      reservationData: {
        // [백엔드 전송용 핵심 데이터]
        propertyId: property.value.productId, // 매물 ID
        agentId: property.value.agentId,      // 중개사 ID (매우 중요)
        reservationTime: reservationDateTime, // 예약 시간
        message: form.value.message,          // 메시지
        
        // [화면 표시용 데이터]
        propertyTitle: property.value.name || property.value.jibun, // 건물명 혹은 지번
        tradeType: property.value.tradeType,
        priceInfo: formatPrice(property.value),
        deposit: property.value.deposit || 0, // 예약금 계산용 (필요시)
        image: getProductMainImage(property.value),
      }
    }
  });
}
</script>

<template>
  <div class="app-main bg-[#FFFBE8] min-h-screen py-10">
    <div class="w-full max-w-xl mx-auto px-4" v-if="!loading">
      
      <h1 class="text-2xl font-bold text-[#AE8B72] mb-6 text-center">방문 예약하기</h1>

      <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-5 mb-6 flex gap-4 items-center">
        <img 
          :src="getProductMainImage(property)" 
          alt="매물 이미지" 
          class="w-28 h-28 object-cover rounded bg-gray-100 flex-shrink-0 border border-gray-100"
        >
        
        <div class="flex flex-col justify-center flex-1 min-w-0">
          <div class="flex items-center gap-2 mb-1">
            <span class="text-xs font-bold bg-[#AE8B72] text-white px-2 py-0.5 rounded">
              {{ typeMap[property.houseType] || property.houseType }}
            </span>
            <span class="text-xs font-bold border border-[#AE8B72] text-[#AE8B72] px-2 py-0.5 rounded">
              {{ tradeTypeMapen2ko[property.tradeType] || property.tradeType }}
            </span>
          </div>

          <h3 class="font-bold text-gray-800 text-lg truncate">
            {{ property.name || property.jibun }} 
            <span v-if="property.aptDong" class="text-sm font-normal text-gray-500 ml-1">
              {{ property.aptDong }} {{ property.floor }}
            </span>
          </h3>
          
          <div class="flex items-center gap-1 text-sm text-gray-500 mb-2 truncate">
            <MapPin :size="14" /> {{ property.sggNm }} {{ property.umdNm }} {{ property.jibun }}
          </div>

          <div class="text-[#AE8B72] font-bold text-lg">
              {{ formatPrice(property) }} <span class="text-sm font-normal text-gray-500">만원</span>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-6">
        
        <div class="mb-6">
          <label class="block text-sm font-bold text-[#AE8B72] mb-2 flex items-center gap-2">
            <Calendar :size="16"/> 방문 희망 일시
          </label>
          <div class="flex gap-2">
            <input 
              type="date" 
              v-model="form.date"
              :min="minDate"
              :max="maxDate"
              class="flex-1 input-base px-4 py-3 cursor-pointer"
              required
            />
            <input 
              type="time" 
              v-model="form.time"
              class="flex-1 input-base px-4 py-3 cursor-pointer"
              required
            />
          </div>
          <p class="text-xs text-gray-400 mt-2 pl-1">
            * 중개사의 일정에 따라 예약이 변경될 수 있습니다.
          </p>
        </div>

        <div class="mb-2">
          <label class="block text-sm font-bold text-[#AE8B72] mb-2 flex items-center gap-2">
            <MessageCircle :size="16"/> 중개사님께 전달할 메시지
          </label>
          <textarea 
            v-model="form.message"
            class="w-full input-base p-4 min-h-[120px] resize-none"
            placeholder="예) 반려동물 동반 가능 여부가 궁금합니다. 주차 공간이 있는지 확인 부탁드려요."
          ></textarea>
        </div>
      </div>

      <div class="mt-8">
        <button @click="goToPayment" class="btn-submit w-full py-4 text-lg shadow-md hover:bg-[#9C7A61] transition-colors">
          다음 단계 (결제)
        </button>
      </div>

    </div>
    
    <div v-else class="flex justify-center items-center h-screen text-[#AE8B72]">
      정보를 불러오는 중입니다...
    </div>
  </div>
</template>

<style scoped>
/* date/time input 커스텀 */
input[type="date"], input[type="time"] {
  @apply text-gray-700 bg-white border border-[#CEAC93] rounded-lg focus:ring-1 focus:ring-[#AE8B72] outline-none;
}
</style>