<template>
    <div class="h-full flex flex-col items-center justify-center p-6 bg-white overflow-y-auto custom-scrollbar">

        <div class="text-center mb-10">
            <div
                class="w-20 h-20 bg-[#FFFBE8] rounded-full flex items-center justify-center mx-auto mb-4 animate-float border border-[#FFFBE8]">
                <Map class="w-10 h-10 text-[#AE8B72]" />
            </div>
            <h2 class="text-2xl font-bold text-gray-900 mb-2">어떤 집을 찾고 계신가요?</h2>
            <p class="text-gray-500 text-sm">지도를 움직이거나 아래 키워드로<br>원하는 매물을 빠르게 찾아보세요.</p>
        </div>

        <div class="w-full max-w-xs grid grid-cols-3 gap-3 mb-10">
            <button v-for="cat in categories" :key="cat.code" @click="goToCategory(cat.code)" class="flex flex-col items-center justify-center p-4 rounded-xl border border-gray-100 bg-gray-50 
               hover:bg-[#FFFBE8] hover:border-[#CEAC93] hover:shadow-md transition-all duration-300 group">
                <component :is="cat.icon"
                    class="w-6 h-6 mb-2 text-gray-400 group-hover:text-[#AE8B72] transition-colors" />
                <span class="text-sm font-semibold text-gray-600 group-hover:text-[#8c6b54]">{{ cat.name }}</span>
            </button>
        </div>

        <div @click="emit('open-ai')"
            class="w-full max-w-xs bg-gradient-to-r from-[#AE8B72] to-[#967660] rounded-xl p-5 shadow-lg text-white relative overflow-hidden group cursor-pointer hover:scale-[1.02] transition-transform duration-300">

            <Sparkles
                class="absolute top-2 right-2 w-12 h-12 text-white/20 group-hover:rotate-12 transition-transform duration-500" />

            <div class="relative z-10">
                <div class="flex items-center gap-2 mb-2">
                    <Bot class="w-5 h-5 text-yellow-300" />
                    <span class="font-bold text-yellow-300 text-xs tracking-wider">AI BETA</span>
                </div>
                <h3 class="font-bold text-lg leading-tight mb-1">딱 맞는 집을<br>AI가 찾아드려요!</h3>
                <p class="text-xs text-[#FFFBE8] opacity-80">내 취향과 조건만 알려주세요.</p>
            </div>
        </div>
        
        <div class="mt-12 flex items-center gap-2 text-xs text-gray-400 bg-gray-50 px-4 py-2 rounded-full">
            <MousePointerClick class="w-3 h-3" />
            <span>지도에서 건물을 클릭하면 상세 정보를 볼 수 있어요.</span>
        </div>

    </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import {
    Map, Home, Building2, Hotel,
    Sparkles, Bot, MousePointerClick
} from 'lucide-vue-next';

const emit = defineEmits(['open-ai']);

const router = useRouter();

// 카테고리 데이터 (아파트, 원룸, 오피스텔 3종)
const categories = [
    { name: '아파트', code: 'APART', icon: Hotel },
    { name: '원룸', code: 'ONE_ROOM', icon: Home },
    { name: '오피스텔', code: 'OFFICETEL', icon: Building2 },
];

// 페이지 이동
const goToCategory = (code) => {
    router.push({
        query: {
            type: code
        }
    });
};
</script>

<style scoped>
@keyframes float {

    0%,
    100% {
        transform: translateY(0);
    }

    50% {
        transform: translateY(-10px);
    }
}

.animate-float {
    animation: float 3s ease-in-out infinite;
}

.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: #e5e7eb;
    border-radius: 999px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background-color: transparent;
}
</style>