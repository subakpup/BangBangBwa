<template>
    <div class="signup-wrapper">
        <div class="signup-container">
            <h1 class="signup-title">비밀번호 변경</h1>

            <p class="text-sm text-gray-500 mb-6 text-center">
                안전한 정보 보호를 위해 현재 비밀번호를 확인 후<br/>
                새 비밀번호로 변경할 수 있습니다.
            </p>

            <form @submit.prevent="handlePasswordUpdate">
                
                <div class="input-group">
                    <div class="input-row">
                        <span class="text-[#CEAC93] text-sm mr-4 w-24 whitespace-nowrap">현재 비밀번호</span>
                        <input type="password" 
                               v-model="form.currentPassword" 
                               class="form-input" 
                               placeholder="현재 사용 중인 비밀번호" 
                               required />
                    </div>

                    <div class="input-row">
                        <span class="text-[#CEAC93] text-sm mr-4 w-24 whitespace-nowrap">새 비밀번호</span>
                        <input type="password" 
                               v-model="form.newPassword" 
                               class="form-input" 
                               placeholder="변경할 비밀번호" 
                               @input="isMismatch = false"
                               required />
                    </div>

                    <div class="input-row border-b-0">
                        <span class="text-[#CEAC93] text-sm mr-4 w-24 whitespace-nowrap">비밀번호 확인</span>
                        <input type="password" 
                               v-model="form.passwordConfirm" 
                               class="form-input" 
                               placeholder="변경할 비밀번호 재입력" 
                               @input="isMismatch = false"
                               required />
                    </div>
                </div>

                <div class="error-msg" v-if="isMismatch">
                    <span>새 비밀번호가 일치하지 않습니다.</span>
                </div>
                
                <div class="error-msg" v-if="serverErrorMessage">
                    <span>{{ serverErrorMessage }}</span>
                </div>

                <div class="flex gap-3 mt-8">
                    <button type="button" 
                            @click="router.back()" 
                            class="btn-submit bg-gray-400 hover:bg-gray-500">
                        취소
                    </button>
                    <button type="submit" class="btn-submit">
                        변경 하기
                    </button>
                </div>

            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { updatePassword } from '@/api/myPageApi';
import { logout } from '@/stores/auth'; // 변경 후 로그아웃 처리를 위해

const router = useRouter();

// 폼 데이터
const form = ref({
    currentPassword: '',
    newPassword: '',
    passwordConfirm: ''
});

// 상태 변수
const isMismatch = ref(false);       // 새 비밀번호 불일치 여부
const serverErrorMessage = ref('');  // 서버로부터 받은 에러 메시지

const handlePasswordUpdate = async () => {
    // 0. 초기화
    isMismatch.value = false;
    serverErrorMessage.value = '';

    // 1. 클라이언트 유효성 검사
    if (form.value.newPassword !== form.value.passwordConfirm) {
        isMismatch.value = true;
        return;
    }

    if (form.value.currentPassword === form.value.newPassword) {
        alert("현재 비밀번호와 다른 비밀번호로 설정해주세요.");
        return;
    }

    try {
        // 2. API 호출
        // 백엔드 DTO 필드명에 맞춰 객체 구성 (currentPassword, newPassword)
        const response = await updatePassword({
            currentPassword: form.value.currentPassword,
            newPassword: form.value.newPassword
        });

        // 3. 응답 처리
        if (response && (response.status === 200 || response.success === 'SUCCESS')) {
            alert("비밀번호가 성공적으로 변경되었습니다.\n다시 로그인해주세요.");
            
            // 보안상 로그아웃 처리 후 로그인 페이지로 이동
            await logout(); 
            router.push('/login');
        } else {
            // 실패 시 (예: 현재 비밀번호 불일치)
            // 백엔드에서 보내주는 메시지(response.data.message 등)를 보여줌
            serverErrorMessage.value = response.data?.message || "비밀번호 변경에 실패했습니다.";
        }
    } catch (error) {
        console.error(error);
        serverErrorMessage.value = "서버 오류가 발생했습니다.";
    }
};
</script>