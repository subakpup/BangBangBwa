<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserInfo, withdrawUser } from '@/api/myPageApi'
import { useRouter } from 'vue-router'
import { statusMap, tradeTypeMapen2ko, formatPrice } from '@/utils/productUtil'
import { User } from 'lucide-vue-next'

const router = useRouter()

const userInfo = ref({
  userId: null,
  email: '',
  name: '',
  phone: '',
  birth: '',
  role: '', 
  ceoName: '',
  realtorAgency: '',
  businessNumber: '',
  reservation: null 
})

const reservationItem = ref({
  "tradeType" : null,
  "dealAmount": null,
  "deposit": null,
  "montlyRent": null,
});

onMounted(async () => {
  const response = await getUserInfo();
  if (response && response.data) { 
    const data = response.data;
    userInfo.value = data; 
    if (!userInfo.value.role) userInfo.value.role = 'ROLE_USER';

    reservationItem.value.tradeType = userInfo.value.reservation.type;
    reservationItem.value.dealAmount = userInfo.value.reservation.dealAmount;
    reservationItem.value.deposit = userInfo.value.reservation.deposit;
    reservationItem.value.montlyRent = userInfo.value.reservation.monthlyRent;

  }
})

const roleName = computed(() => {
  const r = userInfo.value.role;
  if (r === 'ROLE_AGENT') return '공인중개사';
  if (r === 'ROLE_ADMIN') return '관리자';
  return '일반 회원';
})

const handleWithdraw = async () => {
  if(!confirm("정말로 탈퇴하시겠습니까?")) return;
  const res = await withdrawUser();
  if (res) {
    alert("탈퇴되었습니다.");
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.clear();
    router.push('/').then(() => {
      router.go();
    });
  }
}
</script>

<template>
  <div class="app-main flex flex-col items-center pt-10 bg-[#FFFBE8] pb-20">
    
    <div class="mypage-wrapper">
      <h2 class="signup-title mb-6">마이페이지</h2>

      <div class="profile-card">
        
        <div class="profile-avatar-box">
            <User :size="48" :stroke-width="2" />
          <!-- <CircleUser :size="48" :stroke-width="1.5" /> -->
        </div>

        <div class="profile-name-row">
          <h3 class="profile-name">{{ userInfo.name }}</h3>
          <span class="role-badge"
                :class="userInfo.role === 'ROLE_AGENT' ? 'role-badge-agent' : 'role-badge-user'">
            {{ roleName }}
          </span>
        </div>
        <p class="profile-email">{{ userInfo.email }}</p>

        <div class="info-list-container">
          <div class="info-row">
            <span class="info-label">전화번호</span>
            <span class="info-value">{{ userInfo.phone }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">생년월일</span>
            <span class="info-value">{{ userInfo.birth || '-' }}</span>
          </div>
        </div>

        <div v-if="userInfo.role === 'ROLE_AGENT'" class="agent-info-box">
          <h4 class="agent-info-header">
            <i class="fa-solid fa-briefcase mr-1"></i> 중개사 정보
          </h4>
          <div class="space-y-2">
            <div class="info-row">
              <span class="info-label">사무소명</span>
              <span class="info-value">{{ userInfo.realtorAgency }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">대표자명</span>
              <span class="info-value">{{ userInfo.ceoName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">사업자 번호</span>
              <span class="info-value">{{ userInfo.businessNumber }}</span>
            </div>
          </div>
        </div>

        <div class="mypage-btn-group">
          <button @click="router.push('/mypage/wishlist')" class="btn-outline-brown">
            <i class="fa-solid fa-heart mr-1"></i> 찜 목록
          </button>
          <button @click="router.push('/mypage/edit')" class="btn-fill-brown">
            <i class="fa-solid fa-pen mr-1"></i> 정보 수정
          </button>
        </div>

        <button @click="handleWithdraw" class="btn-text-withdraw">
          회원 탈퇴하기
        </button>
      </div>


      <div v-if="userInfo.reservation" class="reservation-card">
        <div class="reservation-deco-bar"></div>
        
        <div class="reservation-header">
          <h3 class="reservation-title">
            <i class="fa-regular fa-calendar-check mr-2 text-[#AE8B72]"></i>
            최근 예약 내역
          </h3>
          <span class="reservation-status-badge">
            {{ statusMap[userInfo.reservation.status] || userInfo.reservation.status }}
          </span>
        </div>

        <div class="reservation-content">
          <div>
            <p class="reservation-sub-title">예약 매물</p>
            <p class="font-bold text-gray-800">{{ userInfo.reservation.name }}</p>
            <p class="text-sm text-gray-500">{{ userInfo.reservation.address }} {{ userInfo.reservation.floor }}</p>
          </div>

          <div class="flex gap-4">
            <div class="reservation-time-box">
              <p class="reservation-sub-title">방문 일시</p>
              <p class="text-sm font-bold text-gray-700">
                {{ userInfo.reservation.visitDate ? userInfo.reservation.visitDate.replace('T', ' ') : '-' }}
              </p>
            </div>
            <div class="reservation-time-box">
              <p class="reservation-sub-title">거래 유형</p>
              <p class="text-sm font-bold text-[#AE8B72]">
                {{ tradeTypeMapen2ko[userInfo.reservation.type] || userInfo.reservation.type }}
              </p>
            </div>
          </div>

          <div class="reservation-price-box">
            <b class="text-[#AE8B72]">{{ formatPrice(reservationItem) }}</b>
          </div>

          <div v-if="userInfo.reservation.description" class="reservation-desc-box">
            "{{ userInfo.reservation.description }}"
          </div>
        </div>
      </div>
      
      <div v-else class="text-center py-6 text-gray-400 text-sm">
        잡혀있는 방문 예약 일정이 없습니다.
      </div>

    </div>
  </div>
</template>