import { ref } from 'vue';
import { logout as logoutApi } from '@/api/userApi';

export const isLogin = ref(false); // 로그인 상태
export const userName = ref(''); // 로그인 유저 이름

// 토큰 디코딩 함수
const parseJwt = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        return null;
    }
};

// 초기화
const initAuth = () => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        isLogin.value = true;
        const payload = parseJwt(token);
        if (payload) userName.value = payload.name;
    }
}
initAuth();

// 로그인 성공 시 호출
export const loginSuccess = (accessToken, refreshToken) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    isLogin.value = true;

    const payload = parseJwt(accessToken);
    if (payload) {
        userName.value = payload.name;
    }
};

// 로그아웃 로직
export const logout = async () => {
    try {
        await logoutApi();
    } catch (error) {
        console.error(error);
    } finally {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.clear();
        
        isLogin.value = false;
        userName.value = '';
    }
};


export const updateState = (accessToken) => {
    const payload = parseJwt(accessToken);

    if(payload) {
        userName.value = payload.name;
    }
};