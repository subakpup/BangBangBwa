<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { requestReservation, getBankList, acceptReservation } from '@/api/reservationApi'
import { Calendar, MessageCircle, CreditCard, CheckCircle, Info } from 'lucide-vue-next'
import { tradeTypeMapen2ko } from '@/utils/productUtil';

const router = useRouter()

// 고정된 예약금 (10,000원)
const RESERVATION_FEE = 10000;

// 상태 변수
const reservationData = ref({});
const bankList = ref([]) 
const selectedBankId = ref(null) 
const loading = ref(false)
const paymentType = ref('USER_DEPOSIT'); // 기본값: 유저 예약금 결제

onMounted(async () => {
  // history.state에서 데이터 가져오기
  const stateData = history.state.reservationData;
  
  if (!stateData) {
    alert("잘못된 접근입니다.");
    router.replace({ name: 'home' });
    return;
  }
  
  reservationData.value = stateData;
  
  if (stateData.type) {
    paymentType.value = stateData.type;
  }

  // 은행 목록 불러오기
  const res = await getBankList();
  if (res && res.success) {
    bankList.value = res.data;
  } else {
    alert("결제 정보를 불러오는데 실패했습니다.");
  }
})

// 결제 및 예약 요청 핸들러
const handlePayment = async () => {
  // 1. 유효성 검사
  if (!selectedBankId.value) {
    alert("결제할 카드사를 선택해주세요.");
    return;
  }

  if (!confirm(`${RESERVATION_FEE.toLocaleString()}원을 결제하시겠습니까?`)) return;

  loading.value = true;

  try {
    let res;

    // [핵심] 타입에 따른 분기 처리
    if (paymentType.value === 'AGENT_ACCEPT') {
      // === Case A: 중개인 예약 승인 ===
      
      // AcceptRequestDto 구조: { reservationId, bankId }
      // reservationId는 이전 페이지에서 reservationData에 담겨 넘어옴
      const payload = {
        reservationId: reservationData.value.reservationId, 
        bankId: selectedBankId.value
      };
      
      // API 호출 (중개인 승인)
      res = await acceptReservation(payload.reservationId, payload.bankId);

    } else {
      // === Case B: 사용자 예약 요청 (기본) ===
      
      // ReservationRequestDto 구조
      const payload = {
        productId: reservationData.value.propertyId,
        visitDate: `${reservationData.value.reservationTime}:00`, // 초 단위 추가 (백엔드 포맷 맞춤)
        message: reservationData.value.message,
        bankId: selectedBankId.value
      };
      
      // API 호출 (유저 예약 요청)
      res = await requestReservation(payload);
    }

    // 결과 처리
    if (res && (res.status === 200 || res.success)) {
      const msg = paymentType.value === 'AGENT_ACCEPT' 
        ? "예약 승인 및 결제가 완료되었습니다." 
        : "결제가 완료되었습니다. 예약 요청이 전송되었습니다.";
        
      alert(msg);
      
      // 완료 후 이동할 페이지 (중개인은 매물 관리로, 유저는 홈으로)
      if (paymentType.value === 'AGENT_ACCEPT') {
         router.replace({ name: 'my-product-list' });
      } else {
         router.replace({ name: 'home' }); 
      }
      
    } else {
      alert(res.message || "요청 처리 중 오류가 발생했습니다.");
    }
  } catch (error) {
    console.error(error);
    alert("시스템 오류가 발생했습니다.");
  } finally {
    loading.value = false;
  }
}

// 날짜 포맷팅 (화면 표시용)
const formattedDate = computed(() => {
  if (!reservationData.value.reservationTime) return '-';
  // ISO string인 경우 T 분리, 아니면 그대로 표시 시도
  const timeStr = String(reservationData.value.reservationTime);
  if (timeStr.includes('T')) {
      const [date, time] = timeStr.split('T');
      return `${date} ${time.substring(0, 5)}`;
  }
  return timeStr;
})

// 안내 메시지 (타입에 따라 다르게)
const infoMessage = computed(() => {
    if (paymentType.value === 'AGENT_ACCEPT') {
        return "예약 승인을 위해 중개 수수료를 결제합니다. <br/>노쇼 발생 시 보상금으로 지급됩니다.";
    }
    return "노쇼 방지를 위해 소정의 예약금을 미리 결제합니다. <br/>만남이 성사될 시, 수수료를 제외한 90%의 금액을 반환합니다.";
})
</script>

<template>
  <div class="app-main bg-[#FFFBE8] min-h-screen py-10">
    <div class="w-full max-w-xl mx-auto px-4">
      
      <h1 class="text-2xl font-bold text-[#AE8B72] mb-6 text-center">
        {{ paymentType === 'AGENT_ACCEPT' ? '예약 승인 결제' : '예약금 결제' }}
      </h1>

      <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-6 mb-4">
        <h3 class="text-lg font-bold text-gray-800 mb-4 border-b border-gray-100 pb-2">예약 정보 확인</h3>
        
        <div class="flex gap-4 mb-4">
          <img 
            :src="reservationData.image || 'https://placehold.co/150x150/E5D0C2/AE8B72?text=No+Image'" 
            class="w-20 h-20 rounded bg-gray-100 object-cover border border-gray-100"
          />
          <div>
            <div class="text-sm font-bold text-[#AE8B72] mb-1">
                {{ tradeTypeMapen2ko[reservationData.tradeType] || '부동산' }}
            </div>
            <div class="text-gray-800 font-bold text-lg mb-1 line-clamp-1">
                {{ reservationData.propertyTitle || '매물 정보 없음' }}
            </div>
            <div class="text-sm text-gray-500">
                {{ reservationData.priceInfo || '' }}
            </div>
          </div>
        </div>

        <div class="space-y-2 text-sm text-gray-600 bg-gray-50 p-4 rounded">
          <div class="flex items-center gap-2">
            <Calendar :size="16" class="text-[#AE8B72]"/> 
            <span class="font-bold">방문 일시:</span> {{ formattedDate }}
          </div>
          <div class="flex items-start gap-2">
            <MessageCircle :size="16" class="text-[#AE8B72] mt-0.5"/> 
            <span class="font-bold whitespace-nowrap">요청 사항:</span> 
            <span class="line-clamp-2">{{ reservationData.message || '없음' }}</span>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-6 mb-4">
        <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
          <CreditCard :size="20"/> 카드사 선택
        </h3>
        
        <div v-if="bankList.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-3">
          <label 
            v-for="bank in bankList" 
            :key="bank.bankId"
            class="payment-card cursor-pointer relative border rounded-lg p-3 hover:border-[#AE8B72] transition-colors bg-gray-50 hover:bg-[#FFFBE8]" 
            :class="{ 'border-[#AE8B72] bg-[#FFFBE8] ring-1 ring-[#AE8B72]': selectedBankId === bank.bankId }"
          >
            <input 
              type="radio" 
              v-model="selectedBankId" 
              :value="bank.bankId" 
              class="hidden"
            >
            <div class="text-center">
              <CreditCard :size="24" class="mx-auto mb-2 text-gray-400" :class="{'text-[#AE8B72]': selectedBankId === bank.bankId}"/>
              <span class="text-sm font-bold block truncate text-gray-700">{{ bank.bankName }}</span>
            </div>
            
            <div v-if="selectedBankId === bank.bankId" class="absolute top-2 right-2 text-[#AE8B72]">
                <CheckCircle :size="16" />
            </div>
          </label>
        </div>

        <div v-else class="text-center py-8 text-gray-400 text-sm">
          결제 가능한 카드사 정보를 불러오는 중입니다...
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-6">
        
        <div class="flex items-center gap-2 mb-4 text-xs text-gray-500 bg-gray-50 p-2 rounded">
          <Info :size="14" class="flex-shrink-0" />
          <span v-html="infoMessage"></span>
        </div>

        <div class="flex justify-between items-center mb-6">
          <span class="text-gray-600 font-bold">총 결제 금액</span>
          <span class="text-2xl font-bold text-[#AE8B72]">{{ RESERVATION_FEE.toLocaleString() }}원</span>
        </div>

        <button 
          @click="handlePayment" 
          :disabled="loading || !selectedBankId"
          class="w-full py-4 text-lg font-bold text-white bg-[#AE8B72] rounded-lg shadow-md hover:bg-[#9C7A61] transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center gap-2"
        >
          <span v-if="loading">결제 처리중...</span>
          <span v-else>{{ RESERVATION_FEE.toLocaleString() }}원 결제하기</span>
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.payment-card {
    transition: all 0.2s ease;
}
</style>