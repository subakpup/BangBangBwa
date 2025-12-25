<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getMyProductList, deleteProduct } from '@/api/productApi'
import { getMyReservationProducts, rejectReservation, getMessage } from '@/api/reservationApi'
import { Building2, Calendar, MapPin, CheckCircle, XCircle, DollarSign,  Home, Edit, Trash2, Loader2, Clock, Plus } from 'lucide-vue-next'
import { formatMoney, getProductMainImage, statusMap, typeMap, formatPrice } from '@/utils/productUtil'

const router = useRouter()

// 상태 변수
const activeTab = ref('ALL') // 'ALL'(전체매물) vs 'RESERVED'(예약확인)
const productList = ref([])
const loading = ref(false)

// =========================================================
// [핵심 1] 데이터 조회 (탭에 따라 아예 다른 API 호출)
// =========================================================
const fetchData = async () => {
  loading.value = true;
  productList.value = []; // 리스트 초기화

  try {
    let res;
    
    // 1. 전체 매물 탭
    if (activeTab.value === 'ALL') {
      res = await getMyProductList();
      if (res && res.success) {
        productList.value = res.data;
      }
    } 
    // 2. 예약 확인 탭
    else {
      res = await getMyReservationProducts();
      if (res && res.success) {
        // (1) PENDING과 RESERVED만 필터링
        const filteredList = res.data.filter(p => p.status === 'PENDING' || p.status === 'RESERVED');

        // (2) 정렬 로직 적용
        // 우선순위 1: PENDING이 무조건 위로
        // 우선순위 2: RESERVED 끼리는 visitDate 오름차순 (빠른 날짜가 위로)
        productList.value = filteredList.sort((a, b) => {
          if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
          if (a.status !== 'PENDING' && b.status === 'PENDING') return 1;
          
          // 둘 다 상태가 같거나, 둘 다 RESERVED인 경우 날짜 비교
          if (a.visitDate && b.visitDate) {
             return new Date(a.visitDate) - new Date(b.visitDate);
          }
          return 0;
        });
      }
    }
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

// 탭이 바뀌면 즉시 데이터 새로고침
watch(activeTab, () => {
  fetchData();
})

// 초기 로드
onMounted(() => {
  fetchData();
  checkPendingStatus();
})

// =========================================================
// [전체 매물 탭 전용] 수정 / 삭제
// =========================================================

const handleEdit = (product) => {
  router.push({ 
    name: 'productEdit', 
    params: { id: product.productId } 
  });
}

const handleDelete = async (product) => {
  if (!confirm(`'${product.name}' 매물을 정말 삭제하시겠습니까?\n삭제 후에는 복구할 수 없습니다.`)) return;

  const res = await deleteProduct(product.productId);
  if (res && res.success) {
    alert("매물이 삭제되었습니다.");
    // [즉시 반영] 서버 재호출 없이 리스트에서 제거
    productList.value = productList.value.filter(p => p.productId !== product.productId);
  } else {
    alert(res.message || "삭제 실패");
  }
}

// =========================================================
// [예약 확인 탭 전용] 승인 / 거절
// =========================================================

const handleApprove = async (product) => {
  if (!confirm(`'${product.name}'의 방문 예약을 승인하시겠습니까?`)) return;

  const message = await getMessage(product.reservationId);

  router.push({
    name: 'reservation-payment',
    state: {
      reservationData: {
        type: 'AGENT_ACCEPT',
        reservationId: product.reservationId, 
        propertyId: product.productId,
        propertyTitle: product.name,
        tradeType: product.tradeType,
        priceInfo: formatPriceSimple(product),
        reservationTime: product.visitDate,
        message: message.data
      }
    }
  });
}

// [핵심 2] 예약 거절 (즉시 삭제 로직)
const handleReject = async (product) => {
  const reason = prompt(`'${product.name}' 예약을 거절하시겠습니까?\n거절 사유를 입력해주세요.`);
  if (reason === null) return; // 취소 시 중단

  const res = await rejectReservation(product.reservationId, reason);
  
  if (res && res.success) {
    alert("예약이 거절되었습니다.");
    // [절대 규칙] 예약 탭 리스트에서 해당 카드를 '제거'합니다.
    productList.value = productList.value.filter(p => p.reservationId !== product.reservationId);
    checkPendingStatus();
  } else {
    alert(res.message || "오류 발생");
  }
}

// [추가] 붉은 점(알림) 표시 여부 상태
const hasPending = ref(false); 

// [추가] 백그라운드에서 PENDING 상태가 있는지 확인하는 함수
const checkPendingStatus = async () => {
  try {
    // 탭과 상관없이 예약 데이터를 가져와서 확인해봅니다.
    const res = await getMyReservationProducts();
    if (res && res.success) {
      // 'PENDING' 상태인 항목이 하나라도 있으면 true
      const pendingItems = res.data.filter(p => p.status === 'PENDING');
      hasPending.value = pendingItems.length > 0;
    }
  } catch (e) {
    console.error("알림 상태 확인 실패", e);
  }
}

// =========================================================
// 유틸리티 (가격, 날짜 포맷)
// =========================================================

const formatPriceSimple = (p) => {
   if (p.tradeType === 'SALE') return formatMoney(p.dealAmount);
   if (p.tradeType === 'LEASE') return formatMoney(p.deposit);
   return `${formatMoney(p.deposit)}/${p.monthlyRent}`;
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return dateStr.replace('T', ' ').substring(0, 16);
}

const getStatusClass = (status) => {
  switch (status) {
    case 'AVAILABLE': return 'bg-green-100 text-green-700 border-green-200';
    case 'RESERVED': return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'PENDING': return 'bg-yellow-100 text-yellow-700 border-yellow-200';
    case 'REPORTED': return 'bg-red-100 text-red-700 border-red-200';
    case 'SOLD_OUT': return 'bg-gray-100 text-gray-500 border-gray-200';
    default: return 'bg-gray-50 text-gray-600 border-gray-200';
  }
}
</script>

<template>
  <div class="app-main bg-[#FFFBE8] min-h-screen py-10">
    <div class="w-full max-w-6xl mx-auto px-4">
      
      <div class="flex flex-col md:flex-row justify-between items-center mb-8">
        <h1 class="text-2xl font-bold text-[#AE8B72] flex items-center gap-2">
          <Building2 /> 내 매물 관리
        </h1>

        <div class="flex items-center gap-3">
        <button 
            @click="router.push({ name: 'productRegister' })"
            class="px-5 py-2 bg-[#AE8B72] text-white rounded-lg text-sm font-bold shadow-md hover:bg-[#9C7A61] transition-all flex items-center gap-1"
          >
            <Plus :size="18" /> 매물 등록
          </button>
        
        <div class="flex bg-white rounded-lg p-1 shadow-sm border border-[#CEAC93] mt-4 md:mt-0">
          <button 
            @click="activeTab = 'ALL'"
            class="px-6 py-2 rounded-md text-sm font-bold transition-all"
            :class="activeTab === 'ALL' ? 'bg-[#AE8B72] text-white shadow' : 'text-gray-500 hover:bg-gray-50'"
          >
            전체 매물
          </button>
          <button 
            @click="activeTab = 'RESERVED'"
            class="px-6 py-2 rounded-md text-sm font-bold transition-all flex items-center gap-1"
            :class="activeTab === 'RESERVED' ? 'bg-[#AE8B72] text-white shadow' : 'text-gray-500 hover:bg-gray-50'"
          >
            예약 확인
            <span v-if="activeTab !== 'RESERVED' && hasPending" class="w-2 h-2 bg-red-500 rounded-full"></span>
          </button>
        </div>
        </div>
      </div>

      <div v-if="loading" class="flex justify-center py-20 text-[#AE8B72]">
        <Loader2 class="animate-spin" :size="32" />
      </div>

      <div v-else-if="productList.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        
        <div 
          v-for="(product, index) in productList" 
          :key="product.productId + '-' + index"
          class="bg-white rounded-xl shadow-sm border border-[#E5D0C2] overflow-hidden hover:shadow-md transition-shadow group flex flex-col"
        >
          <div class="h-48 bg-gray-200 relative flex-shrink-0">
             <img :src="getProductMainImage(product)" class="w-full h-full object-cover"/>
             
             <div class="absolute top-3 left-3 px-3 py-1 rounded-full text-xs font-bold border shadow-sm"
                  :class="getStatusClass(product.status)">
               {{ statusMap[product.status] || product.status }}
             </div>
             
             <div class="absolute top-3 right-3 bg-white/90 px-2 py-1 rounded text-xs font-bold text-gray-600">
               {{ typeMap[product.houseType] || product.houseType }}
             </div>
          </div>

          <div class="p-5 flex flex-col flex-grow">
            <div class="flex-grow">
              <h3 class="font-bold text-lg text-gray-800 truncate mb-1 group-hover:text-[#AE8B72] transition-colors">
                {{ product.name }}
              </h3>
              <div class="flex items-center gap-1 text-sm text-gray-500 mb-4">
                <MapPin :size="14"/> {{ product.address }}
              </div>

              <div class="flex justify-between items-center text-sm text-gray-600 bg-gray-50 p-3 rounded mb-4">
                <div class="text-center flex-1 border-r border-gray-200">
                  <span class="block text-xs text-gray-400">전용면적</span>
                  <span class="font-bold">{{ product.excluUseAr }}㎡</span>
                </div>
                <div class="text-center flex-1">
                   <span class="block text-xs text-gray-400">건축년도</span>
                   <span class="font-bold">{{ product.buildYear }}년</span>
                </div>
              </div>

              <div class="flex items-center gap-2 text-[#AE8B72] font-bold text-lg mb-2">
                 <DollarSign :size="20"/> {{ formatPrice(product) }}
              </div>
              
              <div v-if="activeTab === 'RESERVED'" class="mb-4 text-sm text-gray-600 bg-yellow-50 p-2 rounded border border-yellow-100">
                 <div class="flex items-center gap-2">
                   <Calendar :size="16" class="text-yellow-700"/> 
                   <span class="font-bold text-yellow-800">방문 요청:</span> {{ formatDate(product.visitDate) }}
                 </div>
              </div>
            </div>

            <div class="border-t border-gray-100 pt-4 mt-2">
              
              <div v-if="activeTab === 'ALL'" class="flex gap-2">
                <button 
                  @click="handleEdit(product)"
                  class="flex-1 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm font-bold hover:bg-gray-200 transition-colors flex justify-center items-center gap-1"
                >
                  <Edit :size="16"/> 수정하기
                </button>
                <button 
                  @click="handleDelete(product)"
                  class="flex-1 py-2 bg-white border border-gray-200 text-gray-500 rounded-lg text-sm font-bold hover:bg-red-50 hover:text-red-500 hover:border-red-200 transition-colors flex justify-center items-center gap-1"
                >
                  <Trash2 :size="16"/> 삭제하기
                </button>
              </div>

              <div v-else-if="activeTab === 'RESERVED'" class="w-full">
                
                <div v-if="product.status === 'PENDING'" class="flex gap-2">
                  <button 
                    @click="handleApprove(product)"
                    class="flex-1 py-2 bg-[#AE8B72] text-white rounded-lg text-sm font-bold hover:bg-[#9C7A61] transition-colors flex justify-center items-center gap-1"
                  >
                    <CheckCircle :size="16"/> 예약 승인
                  </button>
                  <button 
                    @click="handleReject(product)"
                    class="flex-1 py-2 bg-white border border-red-200 text-red-500 rounded-lg text-sm font-bold hover:bg-red-50 transition-colors flex justify-center items-center gap-1"
                  >
                    <XCircle :size="16"/> 예약 거절
                  </button>
                </div>

                <div v-else-if="product.status === 'RESERVED'" class="text-center py-2 bg-blue-50 border border-blue-100 text-blue-600 rounded-lg text-sm font-bold flex justify-center items-center gap-2">
                   <Clock :size="16" /> 예약 승인 완료 (방문 대기)
                </div>

              </div>

            </div>
          </div>
        </div>

      </div>

      <div v-else class="text-center py-20 bg-white rounded-lg border border-dashed border-[#CEAC93] text-gray-400">
         <Home :size="48" class="mx-auto mb-4 opacity-30"/>
         <p v-if="activeTab === 'ALL'">등록된 매물이 없습니다.</p>
         <p v-else>현재 처리할 예약 요청이 없습니다.</p>
      </div>

    </div>
  </div>
</template>