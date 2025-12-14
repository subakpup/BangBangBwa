<template>
    <div class="signup-wrapper">
        <div class="signup-container">
            <h1 class="signup-title">회원가입</h1>

            <form @submit.prevent>
                
                <div class="input-group">
                    <div class="input-row">
                        <input type="email" 
                               v-model="form.email" 
                               class="form-input" 
                               placeholder="이메일 (아이디)" 
                               :disabled="emailStep === 2"
                               @input="errors.email = false" 
                               required />
                    </div>
                    
                    <div v-if="emailStep >= 1" class="input-row" style="border-top: 1px solid #eee;">
                        <input type="text" 
                               v-model="verificationCode" 
                               class="form-input" 
                               placeholder="인증번호 6자리" 
                               :disabled="emailStep === 2"
                               @input="errors.code = false" />
                    </div>

                    <div class="input-row">
                        <input type="password" 
                               v-model="form.password" 
                               class="form-input" 
                               placeholder="비밀번호" 
                               @input="errors.password = false"
                               required />
                    </div>
                    <div class="input-row">
                        <input type="password" 
                               v-model="form.passwordConfirm" 
                               class="form-input" 
                               placeholder="비밀번호 확인" 
                               @input="errors.passwordConfirm = false"
                               required />
                    </div>
                </div>

                <div class="error-msg" v-if="errors.email">
                    <span>이메일을 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="isEmailDuplicate">
                    <span>이미 가입된 이메일입니다. 다른 이메일을 사용해주세요.</span>
                </div>

                <div class="error-msg" v-if="errors.code">
                    <span>인증번호를 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="isCodeMismatch">
                    <span>인증번호가 올바르지 않습니다.</span>
                </div>

                <div class="error-msg" v-if="errors.password">
                    <span>비밀번호를 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="!errors.password && form.password && form.passwordConfirm && !isPasswordMatch">
                    <span>비밀번호가 일치하지 않습니다.</span>
                </div>


                <div class="input-group mg-top">
                    <div class="input-row">
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
                </div>
                <div class="error-msg" v-if="errors.name">
                    <span>이름을 입력해주세요.</span>
                </div>
                <div class="error-msg" v-if="errors.birth">
                    <span>생년월일을 입력해주세요.</span>
                </div>


                <div class="input-group mg-top">
                     <div class="input-row">
                        <input type="tel" 
                               v-model="form.phone" 
                               class="form-input" 
                               placeholder="휴대전화번호 (- 없이 입력)" 
                               @input="errors.phone = false"
                               required />
                    </div>
                </div>
                <div class="error-msg" v-if="errors.phone">
                    <span>휴대전화번호를 입력해주세요.</span>
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
                            <input type="text" v-model="form.ceoName" class="form-input" placeholder="대표자명" @input="errors.agent = false">
                        </div>
                        <div class="input-row">
                            <input type="text" v-model="form.realtorAgency" class="form-input" placeholder="부동산 상호명" @input="errors.agent = false">
                        </div>
                        <div class="input-row">
                            <input type="text" v-model="form.businessNumber" class="form-input" placeholder="사업자 등록번호" @input="errors.agent = false">
                        </div>
                    </div>
                    <div class="error-msg" v-if="errors.agent">
                        <span>중개사 정보를 모두 입력해주세요.</span>
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

const STEP = {
    START: 0,   // 인증 시작
    VERIFY: 1,  // 인증번호 입력
    DONE: 2     // 인증 완료
}

const verificationCode = ref(''); // 입력한 인증번호
const emailStep = ref(0);         // 0: 전송전, 1: 전송됨(입력대기), 2: 인증완료
const isEmailDuplicate = ref(STEP.START); // 이메일 중복 여부
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

// 에러 상태
const errors = ref({
    email: false,
    code: false,
    password: false,
    passwordConfirm: false,
    name: false,
    birth: false,
    phone: false,
    agent: false
});

// 비밀번호 일치 여부 확인
const isPasswordMatch = computed(() => {
    return form.value.password && form.value.password === form.value.passwordConfirm;
});

// 버튼 텍스트 동적 변경
const buttonText = computed(() => {
    switch (emailStep.value) {
        case STEP.START: return "인증하기";
        case STEP.VERIFY: return "인증확인";
        default: return "가입하기";
    }
});

// 이메일이 바뀌면 모든 상태 초기화
watch(() => form.value.email, () => {
    // 중복 에러 메시지 종료
    isEmailDuplicate.value = false;
    errors.value.email = false;

    // 인증번호 입력 단계였다면, 다시 인증하기 단계로 되돌림
    if (emailStep.value === STEP.VERIFY) {
        emailStep.value = STEP.START;
        verificationCode.value = '';
        isCodeMismatch.value = false;
    }
});

// 유효성 검사
const validateForm = () => {
    Object.keys(errors.value).forEach(key => errors.value[key] = false);
    let isValid = true;

    const requiredFields = ['email', 'password', 'name', 'birth', 'phone'];

    requiredFields.forEach(field => {
        if (!form.value[field]) {
            errors.value[field] = true;
            isValid = false;
        }
    });

    // 비밀번호 확인
    if (!form.value.passwordConfirm) {
        errors.value.passwordConfirm = true;

        if (!form.value.password) errors.value.password = true;
        isValid = false;
    }

    // 중개사
    if (form.value.role === 'ROLE_AGENT') {
        if (!form.value.ceoName || !form.value.realtorAgency || !form.value.businessNumber) {
            errors.value.agent = true;
            isValid = false;
        }
    }

    return isValid;
}
// 2. 이메일 인증 요청 로직 (Step 0)
const processEmailRequest = async () => {
    if (!validateForm()) return; // 전체 폼 검사 (미리 채워둔 정보들도 체크)

    try {
        // 중복 체크
        const checkResponse = await checkEmail(form.value.email);
        if (!checkResponse || checkResponse.success !== 'SUCCESS') {
            isEmailDuplicate.value = true;
            return;
        }
        isEmailDuplicate.value = false;

        // 메일 발송
        const mailResponse = await sendEmailVerification(form.value.email);
        if (!(mailResponse instanceof Error)) {
            alert("인증번호가 발송되었습니다. 메일을 확인해주세요!");
            emailStep.value = STEP.VERIFY;
        } else {
            alert("이메일 전송에 실패했습니다.");
        }
    } catch (error) {
        console.error(error);
        alert("서버 오류가 발생했습니다.");
    }
};

// 3. 인증번호 확인 로직 (Step 1)
const processCodeVerification = async () => {
    if (!verificationCode.value) {
        errors.value.code = true;
        return;
    }

    try {
        const response = await verifyEmail(form.value.email, verificationCode.value);
        if (response === "인증 성공" || response.success || !(response instanceof Error)) {
            alert("이메일 인증이 완료되었습니다!");
            emailStep.value = STEP.DONE;
            isCodeMismatch.value = false;
        } else {
            isCodeMismatch.value = true;
        }
    } catch (error) {
        isCodeMismatch.value = true;
        console.error(error);
    }
};

// 4. 최종 회원가입 요청 로직 (Step 2)
const processSignup = async () => {
    if (!validateForm()) return;

    if (!isPasswordMatch.value) return; // 비밀번호 불일치는 템플릿에서 메시지 처리됨

    try {
        const response = await signup(form.value);
        if (response instanceof Error || response.code === 'ERROR') {
            alert(response.message || "회원가입에 실패하였습니다.");
        } else {
            alert("가입을 환영합니다, 주인님! 🎉");
            router.push({ name: 'home' });
        }
    } catch (error) {
        console.error(error);
    }
};

// === Main Handler ===
// 버튼 클릭 시 현재 단계에 맞는 함수를 호출 (Dispatcher 패턴)
const handleAuthStep = () => {
    switch (emailStep.value) {
        case STEP.START:
            processEmailRequest();
            break;
        case STEP.VERIFY:
            processCodeVerification();
            break;
        case STEP.DONE:
            processSignup();
            break;
    }
};
</script>