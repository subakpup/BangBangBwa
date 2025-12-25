<template>
    <div class="login-wrapper">
        <div class="login-container">
            <h1 class="signup-title">비밀번호 찾기</h1>
            
            <form v-if="!isSent" @submit.prevent="handleSendEmail">
                <p class="text-sm text-gray-600 mb-4 text-center">
                    가입하신 이메일을 입력하시면<br>비밀번호 재설정 링크를 보내드립니다.
                </p>
                <div class="input-group">
                    <div class="input-row">
                        <input type="email" 
                               v-model="email" 
                               class="form-input" 
                               placeholder="가입한 이메일 입력" 
                               required />
                    </div>
                </div>

                <div class="mg-top-lg">
                    <button type="submit" class="btn-submit" :disabled="isLoading">
                        {{ isLoading ? '전송 중...' : '인증 메일 전송' }}
                    </button>
                </div>
            </form>

            <div v-else class="text-center py-8">
                <div class="mb-4 text-4xl">📧</div>
                <h3 class="font-bold text-lg mb-2">메일 전송 완료</h3>
                <p class="text-gray-600 text-sm mb-6">
                    <strong>{{ email }}</strong> 으로<br>
                    비밀번호 재설정 링크를 보냈습니다.<br>
                    메일함을 확인해주세요.
                </p>
                <button @click="$router.push('/login')" class="btn-submit">
                    로그인으로 돌아가기
                </button>
            </div>

            <div class="auth-links mt-4" v-if="!isSent">
                <RouterLink to="/login">로그인으로 돌아가기</RouterLink>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { api } from '@/api'; // axios 인스턴스

const email = ref('');
const isLoading = ref(false);
const isSent = ref(false);

const handleSendEmail = async () => {
    isLoading.value = true;
    try {
        // 백엔드 API 호출: /auth/reset-password-request
        const response = await api.post('/auth/reset-password-request', { email: email.value });
        
        if (response.data.success === 'SUCCESS') {
            isSent.value = true;
        } else {
            alert(response.data.message || '존재하지 않는 이메일입니다.');
        }
    } catch (error) {
        alert('메일 전송 중 오류가 발생했습니다.');
    } finally {
        isLoading.value = false;
    }
};
</script>