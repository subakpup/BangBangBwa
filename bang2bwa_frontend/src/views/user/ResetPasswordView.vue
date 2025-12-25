<template>
    <div class="login-wrapper">
        <div class="login-container">
            <h1 class="signup-title">비밀번호 재설정</h1>

            <form @submit.prevent="handleResetPassword">
                <div class="input-group">
                    <div class="input-row">
                        <input type="password" 
                               v-model="password" 
                               class="form-input" 
                               placeholder="새 비밀번호" 
                               required />
                    </div>
                    <div class="input-row">
                        <input type="password" 
                               v-model="passwordConfirm" 
                               class="form-input" 
                               placeholder="새 비밀번호 확인" 
                               required />
                    </div>
                </div>
                
                <p v-if="errorMsg" class="text-red-500 text-xs mt-2 text-center">{{ errorMsg }}</p>

                <div class="mg-top-lg">
                    <button type="submit" class="btn-submit">
                        비밀번호 변경하기
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from '@/api';

const route = useRoute();
const router = useRouter();

const password = ref('');
const passwordConfirm = ref('');
const errorMsg = ref('');
const token = ref('');

onMounted(() => {
    // 1. URL 쿼리 파라미터에서 token 추출
    token.value = route.query.token;

    // 2. 토큰이 없으면 잘못된 접근 처리 (강제 이동)
    if (!token.value) {
        alert("잘못된 접근입니다. 이메일 링크를 통해 접속해주세요.");
        router.replace('/login');
    }
});

const handleResetPassword = async () => {
    errorMsg.value = '';

    if (password.value !== passwordConfirm.value) {
        errorMsg.value = '비밀번호가 일치하지 않습니다.';
        return;
    }

    try {
        // 백엔드로 토큰과 새 비밀번호 전송
        const response = await api.post('/auth/reset-password', {
            token: token.value, // 토큰 필수!
            newPassword: password.value
        });

        if (response.data.success === 'SUCCESS') {
            alert('비밀번호가 변경되었습니다. 새 비밀번호로 로그인해주세요.');
            router.replace('/login');
        } else {
            // 토큰 만료 등의 이유
            alert(response.data.message || '링크가 만료되었거나 유효하지 않습니다.');
            router.replace('/find-password');
        }
    } catch (error) {
        alert('오류가 발생했습니다.');
    }
};
</script>