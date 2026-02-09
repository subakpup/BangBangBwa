<template>
    <div class="signup-wrapper">
        <div class="signup-container">
            <h1 class="signup-title">회원정보 수정</h1>

            <div class="flex justify-end mb-4">
                <button 
                    @click="router.push('/mypage/edit/password')"
                    class="text-sm text-gray-500 hover:text-[#AE8B72] flex items-center gap-1 transition-colors"
                >
                    <i class="fa-solid fa-lock text-xs"></i> 비밀번호 변경하러 가기
                </button>
            </div>

            <form @submit.prevent="handleUpdate">
                
                <div class="input-group">
                    <div class="input-row bg-gray-50">
                        <span class="text-[#CEAC93] text-sm mr-4 w-16 whitespace-nowrap">이메일</span>
                        <input type="email" 
                               v-model="form.email" 
                               class="form-input text-gray-500 cursor-not-allowed" 
                               readonly
                               disabled />
                        <i class="fa-solid fa-lock text-gray-300 text-sm"></i>
                    </div>

                    <div class="input-row">
                        <span class="text-[#CEAC93] text-sm mr-4 w-16 whitespace-nowrap">이름</span>
                        <input type="text" 
                               v-model="form.name" 
                               class="form-input" 
                               placeholder="이름" 
                               @input="errors.name = false"
                               required />
                    </div>

                    <div class="input-row">
                        <span class="text-[#CEAC93] text-sm mr-4 whitespace-nowrap">생년월일</span>
                        <input type="date" 
                               v-model="form.birth" 
                               class="form-input date-input" 
                               @input="errors.birth = false"
                               required />
                    </div>

                    <div class="input-row border-b-0">
                        <span class="text-[#CEAC93] text-sm mr-4 w-16 whitespace-nowrap">전화번호</span>
                        <input type="tel" 
                               v-model="form.phone" 
                               class="form-input" 
                               placeholder="(- 없이 입력)" 
                               @input=handlePhoneInput
                               required />
                    </div>
                </div>

                <div class="error-msg" v-if="errors.name">
                    <span>이름을 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="errors.birth">
                    <span>생년월일을 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="errors.phone">
                    <span>휴대전화번호를 입력해주세요.</span>
                </div>

                <div v-if="form.role === 'ROLE_AGENT'" class="mg-top">
                    <h3 class="text-[#AE8B72] text-base mb-2 pl-1 flex items-center gap-2">
                        <i class="fa-solid fa-briefcase"></i> 중개사 정보 수정
                    </h3>
                    <div class="input-group">
                        <div class="input-row">
                            <span class="text-[#CEAC93] text-sm mr-4 whitespace-nowrap">대표자명</span>
                            <input type="text" 
                                   v-model="form.ceoName" 
                                   class="form-input" 
                                   placeholder="대표자명" 
                                   @input="errors.agent = false">
                        </div>
                        <div class="input-row">
                            <span class="text-[#CEAC93] text-sm mr-4 whitespace-nowrap">사무소명</span>
                            <input type="text" 
                                   v-model="form.realtorAgency" 
                                   class="form-input" 
                                   placeholder="부동산 상호명" 
                                   @input="errors.agent = false">
                        </div>
                        <div class="input-row border-b-0">
                            <span class="text-[#CEAC93] text-sm mr-4 whitespace-nowrap">사업자 번호</span>
                            <input type="text" 
                                   v-model="form.businessNumber" 
                                   class="form-input" 
                                   placeholder="사업자 등록번호" 
                                   @input="errors.agent = false">
                        </div>
                    </div>
                    <div class="error-msg" v-if="errors.agent">
                        <span>중개사 정보를 모두 입력해주세요.</span>
                    </div>
                </div>

                <div class="flex gap-3 mt-8">
                    <button type="button" 
                            @click="router.back()" 
                            class="btn-submit bg-gray-400 hover:bg-gray-500">
                        취소
                    </button>
                    <button type="submit" class="btn-submit">
                        수정 완료
                    </button>
                </div>

            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getUserInfo, updateUserInfo } from '@/api/myPageApi';

const router = useRouter();

// 폼 데이터 초기화
const form = ref({
    email: '',
    name: '',
    phone: '',
    birth: '',
    role: '', // ROLE_USER or ROLE_AGENT
    // Agent Fields
    ceoName: '',
    realtorAgency: '',
    businessNumber: ''
});

// 에러 상태 (Signup.vue와 동일한 구조)
const errors = ref({
    name: false,
    birth: false,
    phone: false,
    agent: false
});

// 1. 기존 데이터 불러오기
onMounted(async () => {
    try {
        const response = await getUserInfo();
        if (response && response.data) {
            const data = response.data;
            
            // 데이터 매핑
            form.value = {
                email: data.email,
                name: data.name,
                phone: data.phone,
                birth: data.birth || '',
                role: data.role || 'ROLE_USER',
                ceoName: data.ceoName || '',
                realtorAgency: data.realtorAgency || '',
                businessNumber: data.businessNumber || ''
            };
        }
    } catch (error) {
        console.error("회원 정보 로딩 실패:", error);
        alert("회원 정보를 불러오는데 실패했습니다.");
    }
});

// 유효성 검사 함수
const validateForm = () => {
    // 에러 초기화
    Object.keys(errors.value).forEach(key => errors.value[key] = false);
    let isValid = true;

    if (!form.value.name) {
        errors.value.name = true;
        isValid = false;
    }
    if (!form.value.birth) {
        errors.value.birth = true;
        isValid = false;
    }
    if (!form.value.phone) {
        errors.value.phone = true;
        isValid = false;
    }

    // 중개사일 경우 추가 검증
    if (form.value.role === 'ROLE_AGENT') {
        if (!form.value.ceoName || !form.value.realtorAgency || !form.value.businessNumber) {
            errors.value.agent = true;
            isValid = false;
        }
    }

    return isValid;
};

// 2. 수정 요청 핸들러
const handleUpdate = async () => {
    if (!validateForm()) return;

    try {
        // DTO 구조에 맞춰 데이터 전송
        const response = await updateUserInfo({
            name: form.value.name,
            phone: form.value.phone,
            birth: form.value.birth,
            // Agent 정보도 함께 전송 (UserUpdateDto 구조 확인 필요)
            ceoName: form.value.ceoName,
            realtorAgency: form.value.realtorAgency,
            businessNumber: form.value.businessNumber
        });

        // 성공 여부 체크 (ApiResponse 구조에 따라 조정)
        // 보통 200 OK 또는 success 필드 확인
        if (response && (response.status === 200 || response.success === 'SUCCESS')) {
            router.push('/mypage');
        } else {
            alert(response.message || '정보 수정에 실패했습니다.');
        }
    } catch (error) {
        console.error(error);
        alert('서버 오류가 발생했습니다.');
    }
};

const handlePhoneInput = (e) => {
  // 1. 입력된 값에서 숫자가 아닌 것은 모두 제거
  const value = e.target.value.replace(/[^0-9]/g, '');
  
  // 2. form 데이터 업데이트
  form.value.phone = value;
  
  // 3. 에러 메시지 초기화 (기존 로직)
  if (errors.value) {
      errors.value.phone = false;
  }
}
</script>