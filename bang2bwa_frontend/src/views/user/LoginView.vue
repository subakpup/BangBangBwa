<template>
    <div class="login-wrapper">
        <div class="login-container">
            <h1 class="signup-title">로그인</h1>

            <form @submit.prevent="handleLogin">
                <div class="input-group">
                    <div class="input-row">
                        <input type="email" 
                                v-model="form.email" 
                                class="form-input" 
                                placeholder="이메일 (아이디)" 
                                required />
                    </div>
                    <div class="input-row">
                        <input type="password" 
                                v-model="form.password" 
                                class="form-input" 
                                placeholder="비밀번호" 
                                required />
                    </div>
                </div>

                <div class="error-msg" v-if="errorMessage">
                    <span>{{ errorMessage }}</span>
                </div>
                
                <div class="mg-top-lg">
                    <button type="submit" class="btn-submit">
                        로그인
                    </button>
                </div>
            </form>

            <div class="auth-links">
                <RouterLink to="/find-password">
                    비밀번호 찾기
                </RouterLink>

                <span>|</span>

                <RouterLink to="/signup">
                    회원가입
                </RouterLink>
            </div>
        </div>

    </div>
</template>

<script setup>
import { login } from '@/api/userApi';
import { loginSuccess } from '@/stores/auth';
import { ref } from 'vue';
import { useRouter, RouterLink } from 'vue-router';

const router = useRouter();

// 폼 데이터
const form = ref({
    email: '',
    password: ''
});

// 에러 메시지 상태
const errorMessage = ref('');

// 로그인 핸들러
const handleLogin = async () => {
    // 초기화
    errorMessage.value = '';

    // 빈 칸 체크
    if (!form.value.email || !form.value.password) {
        errorMessage.value = '아이디와 비밀번호를 모두 입려해주세요.';
        return;
    }

    try {
        // 로그인 API 호출
        const response = await login(form.value);

        // 응답 처리
        if (response && response.success == 'SUCCESS') {
            const { accessToken, refreshToken } = response.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);

            loginSuccess(accessToken, refreshToken);

            router.push({ name: 'home' });
        } else {
            errorMessage.value = response.message;
        }
    } catch (error) {
        errorMessage.value = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
    }
};

</script>
