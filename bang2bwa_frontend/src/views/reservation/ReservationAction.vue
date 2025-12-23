<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmReservation, reportNoShow, getReservationDetail, defendNoShow } from '@/api/reservationApi'
import { MapPin, UserCheck, AlertTriangle, Calendar, Loader2, ShieldAlert, Ban, CheckCircle, Clock } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()

const reservationId = route.params.reservationId
const reservationInfo = ref(null)
const loading = ref(false)
const pageLoading = ref(true)

// [1] 내 ID 가져오기 (JWT 디코딩)
const currentUserId = ref(null)

const parseJwt = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}

onMounted(async () => {
  const token = localStorage.getItem('accessToken') || localStorage.getItem('token');
  if (token) {
    const payload = parseJwt(token);
    if (payload) {
      currentUserId.value = payload.userId || payload.id || payload.sub || payload.user_id;
    }
  }
  await refreshData();
})

const refreshData = async () => {
  pageLoading.value = true;
  const res = await getReservationDetail(reservationId);
  if (res && res.success) {
    reservationInfo.value = res.data;
  } else {
    alert("정보를 불러올 수 없습니다.");
    router.replace({ name: 'home' });
  }
  pageLoading.value = false;
}

// =========================================================
// [핵심 로직] 상태 및 역할 판단
// =========================================================

// 1. 예약 정보 상의 역할 확인 ( == 사용으로 문자/숫자 비교 허용)
const amIUser = computed(() => reservationInfo.value?.userId == currentUserId.value);
const amIAgent = computed(() => reservationInfo.value?.agentId == currentUserId.value);

// 2. 누가 신고 당했는지 확인
const isUserReported = computed(() => {
  const info = reservationInfo.value;
  return info?.userConfirmed === 'N' || info?.user_confirmed === 'N';
});

const isAgentReported = computed(() => {
  const info = reservationInfo.value;
  return info?.agentConfirmed === 'N' || info?.agent_confirmed === 'N';
});

// 3. [판단] 나는 신고 당했는가? (가해자)
// [수정] status 체크 제거 -> NO_SHOW 상태에서도 내가 가해자인지 알아야 함
const amIReported = computed(() => {
  if (amIUser.value && isUserReported.value) return true;
  if (amIAgent.value && isAgentReported.value) return true;
  return false;
});

// 4. [판단] 나는 신고자인가? (피해자/신고자)
// [수정] status 체크 제거 -> NO_SHOW 상태에서도 내가 신고자인지 알아야 함
const amIReporter = computed(() => {
  if (amIUser.value && isAgentReported.value) return true;
  if (amIAgent.value && isUserReported.value) return true;
  return false;
});

// 5. 기타 상태들
const isQuit = computed(() => reservationInfo.value?.status === 'QUIT')
const isNoShowConfirmed = computed(() => reservationInfo.value?.status === 'NO_SHOW')
const isReportedStatus = computed(() => reservationInfo.value?.status === 'REPORTED')
const isFinalState = computed(() => isQuit.value || isNoShowConfirmed.value)


// =========================================================
// [기능] 위치 인증 및 핸들러
// =========================================================

const getCurrentLocation = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("위치 정보를 지원하지 않는 브라우저입니다."));
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude }),
      (err) => reject(new Error("위치 정보를 가져올 수 없습니다. 위치 권한을 허용해주세요."))
    );
  });
}

const handleConfirmOrDefend = async () => {
  loading.value = true;
  try {
    const location = await getCurrentLocation();
    let res;

    // 내가 신고 당한 상태(REPORTED)라면 -> 방어
    if (isReportedStatus.value && amIReported.value) {
      if (!confirm("현재 매물 근처에 계신가요?\n위치 인증을 통해 노쇼 신고를 취소하고 만남을 확정합니다.")) return;
      res = await defendNoShow(reservationId, location);
    } else {
      if (!confirm("만남이 확인되었습니까?\n확인 시 보증금이 반환 절차에 들어갑니다.")) return;
      res = await confirmReservation(reservationId, location);
    }
    
    if (res && (res.status === 200 || res.success)) {
      alert("처리가 완료되었습니다.");
      await refreshData();
    } else {
      alert(res.message || "오류가 발생했습니다.");
    }
  } catch (error) {
    alert(error.message);
  } finally {
    loading.value = false;
  }
}

const handleNoShow = async () => {
  if (!confirm("상대방이 오지 않았나요?\n허위 신고 시 불이익이 있을 수 있습니다.")) return;
  loading.value = true;
  try {
    const location = await getCurrentLocation();
    const res = await reportNoShow(reservationId, location);
    if (res && (res.status === 200 || res.success)) {
      alert("노쇼 신고가 접수되었습니다.");
      await refreshData();
    } else {
      alert(res.message || "오류가 발생했습니다.");
    }
  } catch (error) {
    alert(error.message);
  } finally {
    loading.value = false;
  }
}

const formattedTime = computed(() => {
  if (!reservationInfo.value?.visitDate) return '-';
  return reservationInfo.value.visitDate.replace('T', ' ').substring(0, 16);
})
</script>

<template>
  <div class="app-main bg-[#FFFBE8] min-h-screen py-10">
    <div class="w-full max-w-xl mx-auto px-4">
      
      <h1 class="text-2xl font-bold text-[#AE8B72] mb-6 text-center">예약 관리</h1>

      <div v-if="pageLoading" class="flex justify-center py-20 text-[#AE8B72]">
        <Loader2 class="animate-spin" :size="32" />
      </div>

      <div v-else-if="reservationInfo">
        
        <div v-if="isNoShowConfirmed">
          
          <div v-if="amIReporter" class="bg-blue-50 text-blue-800 rounded-lg p-5 mb-6 shadow-sm border border-blue-200">
            <div class="flex items-start gap-3">
              <CheckCircle class="text-blue-600 flex-shrink-0" :size="32" />
              <div>
                <h3 class="font-bold text-lg text-blue-700">노쇼 확정 (보증금 반환)</h3>
                <p class="text-sm text-blue-600 mt-1">
                  상대방의 노쇼가 확정되어, 고객님의 보증금이 정상적으로 반환되었습니다.
                </p>
              </div>
            </div>
          </div>

          <div v-else class="bg-gray-800 text-white rounded-lg p-5 mb-6 shadow-lg border-2 border-gray-600">
            <div class="flex items-start gap-3">
              <Ban class="text-red-500 flex-shrink-0" :size="32" />
              <div>
                <h3 class="font-bold text-lg text-red-400">노쇼 확정 (종료됨)</h3>
                <p class="text-sm text-gray-300 mt-1">
                  규정에 따라 보증금은 전액 몰수되었습니다.
                </p>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="isQuit" class="bg-green-50 border border-green-200 rounded-lg p-4 mb-6">
          <div class="flex items-start gap-3">
            <CheckCircle class="text-green-600 flex-shrink-0" :size="28" />
            <div>
              <h3 class="font-bold text-green-700 text-lg">정상 종료 (반환 완료)</h3>
              <p class="text-sm text-green-600 mt-1">보증금이 환불되었습니다.</p>
            </div>
          </div>
        </div>

        <div v-else-if="isReportedStatus">
          
          <div v-if="amIReported" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6 animate-pulse">
            <div class="flex items-start gap-3">
              <ShieldAlert class="text-red-600 flex-shrink-0" :size="28" />
              <div>
                <h3 class="font-bold text-red-700 text-lg">노쇼 신고를 당했습니다!</h3>
                <p class="text-sm text-red-600 mt-1">아래 버튼을 눌러 위치를 인증하고 방어하세요.</p>
              </div>
            </div>
          </div>

          <div v-else-if="amIReporter" class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <div class="flex items-start gap-3">
              <Clock class="text-blue-600 flex-shrink-0" :size="28" />
              <div>
                <h3 class="font-bold text-blue-700 text-lg">신고 접수 완료</h3>
                <p class="text-sm text-blue-600 mt-1">상대방의 확인을 기다리고 있습니다.</p>
              </div>
            </div>
          </div>

        </div>

        <div class="bg-white rounded-lg shadow-sm border border-[#CEAC93] p-6 mb-8 relative overflow-hidden" 
             :class="{ 'opacity-75 grayscale': isFinalState }">
          <div class="absolute top-0 left-0 w-full h-1 bg-[#AE8B72]"></div>
          
          <h3 class="text-lg font-bold text-gray-800 mb-4">방문 예약 정보</h3>
          <div class="flex gap-4 mb-4 items-center">
             <img :src="reservationInfo.productImage || 'https://placehold.co/150x150/E5D0C2/AE8B72?text=No+Image'" class="w-20 h-20 rounded object-cover bg-gray-100 border border-gray-100" />
             <div>
               <div class="text-xl font-bold text-gray-800 line-clamp-1">{{ reservationInfo.productName }}</div>
               <div class="text-sm text-gray-500 line-clamp-1">{{ reservationInfo.productAddress }}</div>
             </div>
          </div>
          <div class="bg-gray-50 rounded p-4 space-y-2">
             <div class="flex items-center gap-2 text-sm text-gray-700">
              <Calendar :size="16" class="text-[#AE8B72]"/>
              <span class="font-bold">예약 일시:</span> {{ formattedTime }}
            </div>
             <div class="flex items-center gap-2 text-sm text-gray-700">
               <span class="font-bold">현재 상태:</span> 
               <span class="font-bold" 
                 :class="{
                   'text-red-600': amIReported,
                   'text-blue-600': amIReporter,
                   'text-gray-600': isNoShowConfirmed && !amIReporter, // 노쇼 당사자는 회색
                   'text-blue-600': isNoShowConfirmed && amIReporter,  // 신고자는 파란색
                   'text-green-600': isQuit,
                   'text-[#AE8B72]': !isFinalState && !amIReported && !amIReporter
                 }">
                 {{ 
                    amIReported ? '신고 당함 (대응 필요)' : 
                    (amIReporter ? (isNoShowConfirmed ? '노쇼 확정 (반환됨)' : '신고 접수됨 (대기 중)') : 
                    (isQuit ? '정상 종료' : 
                    (isNoShowConfirmed ? '노쇼 확정 (몰수)' : 
                    (reservationInfo.status === 'RESERVED' ? '방문 예약됨' : reservationInfo.status)))) 
                 }}
               </span>
            </div>
          </div>
        </div>

        <div v-if="isFinalState || (isReportedStatus && amIReporter)" 
             class="text-center py-6 rounded-lg font-bold border"
             :class="(isQuit || amIReporter) ? 'bg-blue-50 border-blue-200 text-blue-600' : 'bg-gray-100 border-gray-200 text-gray-500'">
           
           <CheckCircle v-if="isQuit" class="mx-auto mb-2" :size="24"/>
           <CheckCircle v-else-if="isNoShowConfirmed && amIReporter" class="mx-auto mb-2" :size="24"/>
           <Clock v-else-if="amIReporter && isReportedStatus" class="mx-auto mb-2" :size="24"/>
           <Ban v-else class="mx-auto mb-2 opacity-50" :size="24"/>
           
           <span v-if="isQuit">정상적으로 종료된 예약입니다. (보증금 반환 완료)</span>
           <span v-else-if="isNoShowConfirmed && amIReporter">상대방의 노쇼로 종료되었습니다. (보증금 반환)</span>
           <span v-else-if="amIReporter && isReportedStatus">신고가 접수되었습니다. (상대방 확인 대기)</span>
           <span v-else>규정 위반으로 종료된 예약입니다. (조작 불가)</span>
        </div>

        <div v-else class="space-y-4">
          <button 
            @click="handleConfirmOrDefend" 
            :disabled="loading"
            class="w-full py-4 rounded-lg font-bold text-lg shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50"
            :class="(isReportedStatus && amIReported) ? 'bg-red-600 text-white animate-pulse ring-4 ring-red-200' : 'bg-[#AE8B72] text-white'"
          >
            <ShieldAlert v-if="isReportedStatus && amIReported" :size="20"/> 
            <UserCheck v-else :size="20"/>
            {{ (isReportedStatus && amIReported) ? '신고 이의 제기 (지금 위치 인증하기)' : '만남 확인 (보증금 반환)' }}
          </button>

          <button 
            v-if="!amIReported"
            @click="handleNoShow" 
            :disabled="loading"
            class="w-full py-4 rounded-lg bg-white border border-red-200 text-red-500 font-bold text-lg hover:bg-red-50 transition-colors disabled:opacity-50"
          >
            <span class="flex items-center justify-center gap-2">
              <AlertTriangle :size="20" class="flex-shrink-0"/>
              상대방이 오지 않았어요 (노쇼 신고)
            </span>
          </button>
        </div>

      </div>
    </div>
  </div>
</template>