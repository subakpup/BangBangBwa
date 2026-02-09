<template>
  <header class="header-container">
    <div class="flex items-center gap-8">
        <RouterLink to="/" class="header-logo">
            <Home class="w-8 h-8" /> 방방봐
        </RouterLink>
        
        <nav class="header-nav">
            <RouterLink :to="{ path: '/', query: { type: 'APART' }}" class="nav-link">아파트</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'ONEROOM' }}" class="nav-link">원룸</RouterLink>
            <RouterLink :to="{ path: '/', query: { type: 'OFFICETEL' }}" class="nav-link">오피스텔</RouterLink>
            
            <RouterLink to="/board" class="nav-link">게시판</RouterLink>
        </nav>
    </div>

    <div class="header-auth">
      <template v-if="!isLogin">
        <RouterLink to="/login" class="btn-login">로그인</RouterLink>
        <RouterLink to="/signup" class="btn-signup">회원가입</RouterLink>
      </template>

      <template v-else>
        <div class="relative">
          <button @click="toggleDropdown" class="header-user-btn">
            <div class="header-user-avatar">
              <User class="w-5 h-5"/>
            </div>
            <span class="text-sm font-bold text-gray-700">{{ userName }}</span>
            <ChevronDown class="w-4 h-4 text-gray-400 transition-transform duration-200" 
                        :class="{ 'rotate-180': isDropdownOpen }" />
          </button>

          <div v-if="isDropdownOpen" class="header-dropdown-menu">
            <RouterLink to="/mypage" class="dropdown-item" @click="closeDropdown">
              <User class="w-4 h-4" />
              마이페이지
            </RouterLink>

            <RouterLink to="/mypage/wishlist" class="dropdown-item" @click="closeDropdown">
              <Heart class="w-4 h-4" />
              찜 목록
            </RouterLink>

            <router-link 
              v-if="userRole === 'ROLE_AGENT'" 
              :to="{ name: 'my-product-list' }"
              class="dropdown-item"
            >
            <Home class="w-4 h-4" />
              내 매물 관리
            </router-link>

            <div class="dropdown-divider"></div>

            <button @click="handleLogout" class="dropdown-item text-red-500 hover:bg-red-50">
              <LogOut class="w-4 h-4" />
              로그아웃
            </button>
          </div>

          <div v-if="isDropdownOpen" 
              @click="closeDropdown" 
              class="dropdown-overlay">
          </div>
        </div>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router';
import { Home, User, ChevronDown, Heart, LogOut } from 'lucide-vue-next';
import { isLogin, logout, userName } from '@/stores/auth';

const userRole = ref(null)
const isLoggedIn = ref(false)

const router = useRouter();

// 드롭다운 상태 관리
const isDropdownOpen = ref(false);

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// 드롭다운 닫기
const closeDropdown = () => {
  isDropdownOpen.value = false;
};

// 로그아웃 핸들러
const handleLogout = async () => {
  closeDropdown();
  await logout();
  router.push('/');
};

const parseJwt = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}

onMounted(() => {
  const token = localStorage.getItem('accessToken');
  
  if (token) {
    const payload = parseJwt(token);
    if (payload) {
      isLoggedIn.value = true;
      
      userRole.value = payload.auth; 
    }
  }
})
</script>