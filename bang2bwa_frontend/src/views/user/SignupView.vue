<template>
    <div class="signup-wrapper">
        <div class="signup-container">
            <h1 class="signup-title">회원가입</h1>

            <form @submit.prevent>

                <div class="input-group">
                    <div class="input-row">
                        <input type="email" v-model="form.email" class="form-input" placeholder="이메일 (아이디)"
                            :disabled="emailStep === 2" required />
                    </div>

                    <div v-if="emailStep >= 1" class="input-row" style="border-top: 1px solid #eee;">
                        <input type="text" v-model="verificationCode" class="form-input" placeholder="인증번호 6자리"
                            :disabled="emailStep === 2" />
                    </div>

                    <div class="input-row">
                        <input type="password" v-model="form.password" class="form-input" placeholder="비밀번호" required />
                    </div>
                    <div class="input-row">
                        <input type="password" v-model="form.passwordConfirm" class="form-input" placeholder="비밀번호 확인"
                            required />
                    </div>
                </div>

                <div class="error-msg" v-if="isEmailDuplicate">
                    <span>이미 가입된 이메일입니다. 다른 이메일을 사용해주세요.</span>
                </div>
                <div class="error-msg" v-else-if="form.password && form.passwordConfirm && !isPasswordMatch">
                    <span>비밀번호가 일치하지 않습니다.</span>
                </div>
                <div class="error-msg" v-if="isCodeMismatch">
                    <span>인증번호가 올바르지 않습니다.</span>
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
                        <input type="tel" v-model="form.phone" class="form-input" placeholder="휴대전화번호 (- 없이 입력)"
                            required />
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
                    <button @click.prevent="handleAuthStep" class="btn-submit">
                        {{ buttonText }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { sendEmailVerification, signup, verifyEmail, checkEmail } from '@/api/userApi';

const router = useRouter();

const verificationCode = ref(''); // 입력한 인증번호
const emailStep = ref(0);         // 0: 전송전, 1: 전송됨(입력대기), 2: 인증완료
const isEmailDuplicate = ref(false); // 이메일 중복 여부
const isCodeMismatch = ref(false); // 인증번호 불일치 여부

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

// 버튼 텍스트 동적 변경
const buttonText = computed(() => {
    if (emailStep.value === 0) return "인증하기";
    if (emailStep.value === 1) return "인증확인";
    return "가입하기";
});

// 버튼 클릭 시 단계별 로직
const handleAuthStep = async () => {
    // 1. 인증번호 발송
    if (emailStep.value === 0) {
        if (!form.value.email) {
            alert("이메일을 입력해주세요.");
            return;
        }

        try {
            // 이메일 중복 체크
            const duplicate = await checkEmail(form.value.email);
            
            if (duplicate && duplicate.success === 'SUCCESS') {
                isEmailDuplicate.value = false;
            } else {
                isEmailDuplicate.value = true;
                return;
            }

            // 이메일 전송 API 호출
            const response = await sendEmailVerification(form.value.email);

            if (!(response instanceof Error)) {
                alert("인증번호가 발송되었습니다. 메일을 확인해주세요!");
                emailStep.value = 1;
            } else {
                alert("이메일 주소를 확인해주세요.");
            }
        } catch (error) {
            console.error(error);
        }
    }

    // 2. 인증번호 검증
    else if (emailStep.value == 1) {
        if (!verificationCode.value) {
            alert("인증번호를 입력해주세요.");
            return;
        }

        try {
            const response = await verifyEmail(form.value.email, verificationCode.value);

            // TODO: 응답 확인 필요!!
            if (response === "인증 성공" || response.success || !(response instanceof Error)) {
                alert("이메일 인증이 완료되었습니다!");
                emailStep.value = 2;
                isCodeMismatch.value = false;
            } else {
                isCodeMismatch.value = true;
            }
        } catch (error) {
            isCodeMismatch.value = true;
            console.log(error);
        }
    }

    else if (emailStep.value === 2) {
        handleSignup();
    }
};

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

// 이메일이 바뀌면 모든 상태 초기화
watch(() => form.value.email, () => {
    // 중복 에러 메시지 종료
    isEmailDuplicate.value = false;

    // 인증번호 입력 단계였다면, 다시 인증하기 단계로 되돌림
    if (emailStep.value === 1) {
        emailStep.value = 0;
        verificationCode.value = '';
        isCodeMismatch.value = false;
    }
});

</script>