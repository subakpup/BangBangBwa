<template>
    <div class="signup-wrapper">
        <div class="signup-container">
            <h1 class="signup-title">회원가입</h1>

            <form @submit.prevent="handleSignup">
                
                <div class="input-group">
                    <div class="input-row">
                        <input type="email" v-model="form.email" class="form-input" placeholder="이메일 (아이디)" required />
                    </div>
                    <div class="input-row">
                        <input type="password" v-model="form.password" class="form-input" placeholder="비밀번호" required />
                    </div>
                    <div class="input-row">
                        <input type="password" v-model="form.passwordConfirm" class="form-input" placeholder="비밀번호 확인" required />
                    </div>
                </div>
                <div class="error-msg" v-if="form.password && form.passwordConfirm && !isPasswordMatch">
                    <span>비밀번호가 일치하지 않습니다.</span>
                </div>

                <div class="input-group mg-top">
                    <div class="input-row">
                        <input type="text" v-model="form.name" class="form-input" placeholder="이름" required />
                    </div>
                    <div class="input-row">
                         <span class="text-[#CEAC93] text-sm mr-4 whitespace-nowrap">생년월일</span>
                        <input type="date" v-model="form.birth" class="form-input date-input" required />
                    </div>
                </div>

                <div class="input-group mg-top">
                     <div class="input-row">
                        <input type="tel" v-model="form.phone" class="form-input" placeholder="전화번호 (- 없이 입력)" required />
                    </div>
                </div>

                <div class="role-group">
                    <label class="role-label">
                        <input type="radio" v-model="form.role" value="ROLE_USER" class="role-input hidden">
                        <span class="role-text">일반 회원</span>
                    </label>
                    <label class="role-label">
                        <input type="radio" v-model="form.role" value="ROLE_AGENT" class="role-input hidden">
                        <span class="role-text">공인중개사</span>
                    </label>
                </div>

                <div v-if="form.role === 'ROLE_AGENT'" class="mg-top">
                    <h3 class="text-[#AE8B72] text-base mb-2 pl-1">중개사 정보 입력</h3>
                    <div class="input-group">
                        <div class="input-row">
                            <input type="text" v-model="form.ceoName" class="form-input" placeholder="대표자명">
                        </div>
                        <div class="input-row">
                            <input type="text" v-model="form.realtorAgency" class="form-input" placeholder="부동산 상호명">
                        </div>
                        <div class="input-row">
                            <input type="text" v-model="form.businessNumber" class="form-input" placeholder="사업자 등록번호">
                        </div>
                    </div>
                </div>
                
                <div class="mg-top-lg">
                    <button type="submit" class="btn-submit">가입하기</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { signup } from '@/api/userApi';

const router = useRouter();

// 폼 데이터
const form = ref({
    email: '',
    password: '',
    passwordConfirm: '',
    name: '',
    phone: '',
    birth: '',
    role: 'ROLE_USER',
    // === Agent 전용 필드 === //
    ceoName: '',
    realtorAgency: '',
    businessNumber: ''
});

// 비밀번호 일치 여부 확인
const isPasswordMatch = computed(() => {
    return form.value.password && form.value.password === form.value.passwordConfirm;
});

// 회원가입 요청
const handleSignup = async () => {
    if (!isPasswordMatch.value) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    try {
        const response = await signup(form.value);

        if (response instanceof Error || response.code === 'ERROR') {
            alert("회원가입에 실패하였습니다.");
        } else {
            alert("회원가입이 완료되었습니다!");
            router.push({ name: 'home' });
        }
    } catch (error) {
        console.error(error);
    }
};

</script>