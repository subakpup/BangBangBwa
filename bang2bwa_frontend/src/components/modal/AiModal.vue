<script setup>
import { ref, reactive, computed } from 'vue';
import { Bot, X, Search, Check, Sparkles, Loader2, TrainFront, Bus, Store, School, ShoppingCart, Stethoscope, Pill, Shirt, Siren, Trees } from 'lucide-vue-next'

const props = defineProps(['show']);
const emit = defineEmits(['close', 'search']);

const isAnalyzing = ref(false);

const form = reactive({
    type: '월세',
    location: '',
    // 월세 예산
    deposit: { min: 0, max: 5000 }, // 보증금
    monthly: { min: 0, max: 50000}, // 월세
    // 전세/매매 예산
    budget: { min: 0, max: 50000},  // 전체 예산
    options: [],
})

// 옵션 목록
const optionsList = [
  { id: 'subway', label: '지하철', icon: TrainFront },
  { id: 'bus', label: '버스', icon: Bus },
  { id: 'convenience', label: '편의점', icon: Store },
  { id: 'school', label: '학교', icon: School },
  { id: 'mart', label: '대형마트', icon: ShoppingCart },
  { id: 'hospital', label: '병원', icon: Stethoscope },
  { id: 'pharmacy', label: '약국', icon: Pill },
  { id: 'laundry', label: '세탁소', icon: Shirt },
  { id: 'police', label: '치안센터', icon: Siren }, // 파출소
  { id: 'park', label: '공원', icon: Trees },
];

// 옵션 토글 함수
const toggleOption = (id) => {
    if (form.options.includes(id)) {
        form.options = form.options.filter(opt => opt !== id);
    } else {
        form.options.push(id);
    }
};

// 분석 요청
const handleSearch = () => {
    if (isAnalyzing.value) return;

    if (!form.location) {
        alert('희망 지역을 입력해 주세요!');
        return;
    }

    isAnalyzing.value = true;

    setTimeout(() => {
        isAnalyzing.value = false;
        emit('search', {...form });
        emit('close');
    }, 2000);
};
</script>

<template>
  <div v-if="show" class="fixed inset-0 bg-black/50 z-[9999] flex items-center justify-center p-4 backdrop-blur-sm">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden animate-fade-in-up flex flex-col max-h-[90vh]">
      
      <div class="bg-primary p-6 text-white flex justify-between items-start flex-shrink-0">
        <div>
          <h3 class="text-2xl font-bold flex items-center gap-2">
            <Bot class="w-8 h-8" /> AI 맞춤 추천
          </h3>
          <p class="text-white/80 mt-1 text-sm">원하는 조건을 입력하면 AI가 딱 맞는 집을 찾아드려요!</p>
        </div>
        <button @click="$emit('close')" class="text-white/80 hover:text-white hover:bg-white/10 rounded-full p-2 transition">
          <X class="w-6 h-6" />
        </button>
      </div>

      <div class="p-6 overflow-y-auto custom-scrollbar space-y-8 flex-1">
        
        <section>
          <label class="block text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">1</span>
            거래 유형 선택
          </label>
          <div class="grid grid-cols-3 gap-3">
            <button 
              v-for="type in ['월세', '전세', '매매']" 
              :key="type"
              @click="form.type = type"
              :class="form.type === type ? 'bg-primary text-white border-primary ring-2 ring-primary ring-offset-2' : 'bg-white text-gray-500 border-gray-200 hover:border-primary hover:text-primary'"
              class="py-3 rounded-xl border font-bold transition duration-200"
            >
              {{ type }}
            </button>
          </div>
        </section>

        <section class="bg-gray-50 p-5 rounded-xl border border-gray-100">
          <label class="block text-sm font-bold text-gray-700 mb-4 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">2</span>
            예산 범위 설정
          </label>

          <div v-if="form.type === '월세'" class="space-y-4">
            <div>
              <div class="flex justify-between text-xs text-gray-500 mb-1">
                <span>보증금</span>
                <span class="text-primary font-bold">{{ formatMoney(form.deposit.min) }} ~ {{ formatMoney(form.deposit.max) }}</span>
              </div>
              <div class="flex items-center gap-2">
                <input v-model="form.deposit.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
                <span>~</span>
                <input v-model="form.deposit.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
                <span class="text-sm text-gray-500 whitespace-nowrap">만원</span>
              </div>
            </div>
            <div>
              <div class="flex justify-between text-xs text-gray-500 mb-1">
                <span>월세</span>
                <span class="text-primary font-bold">{{ formatMoney(form.monthly.min) }} ~ {{ formatMoney(form.monthly.max) }}</span>
              </div>
              <div class="flex items-center gap-2">
                <input v-model="form.monthly.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
                <span>~</span>
                <input v-model="form.monthly.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
                <span class="text-sm text-gray-500 whitespace-nowrap">만원</span>
              </div>
            </div>
          </div>

          <div v-else>
            <div class="flex justify-between text-xs text-gray-500 mb-1">
              <span>총 예산</span>
              <span class="text-primary font-bold">{{ formatMoney(form.budget.min) }} ~ {{ formatMoney(form.budget.max) }}</span>
            </div>
            <div class="flex items-center gap-2">
              <input v-model="form.budget.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
              <span>~</span>
              <input v-model="form.budget.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
              <span class="text-sm text-gray-500 whitespace-nowrap">만원</span>
            </div>
          </div>
        </section>

        <section>
          <label class="block text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">3</span>
            희망 지역
          </label>
          <div class="relative">
            <input 
              v-model="form.location"
              type="text" 
              placeholder="예: 강남구 역삼동, 삼성역 인근" 
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition"
            >
            <Search class="absolute left-3 top-3.5 w-5 h-5 text-gray-400" />
          </div>
        </section>

        <section>
          <label class="block text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">4</span>
            선호 옵션 (중복 선택 가능)
          </label>
          <div class="grid grid-cols-2 md:grid-cols-5 gap-3">
            <button 
              v-for="opt in optionsList" 
              :key="opt.id"
              @click="toggleOption(opt.id)"
              :class="form.options.includes(opt.id) ? 'bg-primary/10 border-primary text-primary' : 'bg-white border-gray-200 text-gray-500 hover:bg-gray-50'"
              class="flex flex-col items-center justify-center p-3 rounded-xl border transition duration-200 gap-1 h-20"
            >
              <component :is="opt.icon" class="w-6 h-6" :class="form.options.includes(opt.id) ? 'text-primary' : 'text-gray-400'" />
              <span class="text-xs font-medium">{{ opt.label }}</span>
            </button>
          </div>
        </section>
      </div>

      <div class="p-5 border-t border-gray-100 bg-gray-50 flex-shrink-0 flex justify-end gap-3">
        <button @click="$emit('close')" class="px-6 py-3 rounded-xl text-gray-500 hover:bg-gray-200 font-bold transition">
          취소
        </button>
        <button 
          @click="handleSearch"
          class="px-8 py-3 bg-gradient-to-r from-primary to-secondary text-white rounded-xl font-bold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition flex items-center gap-2"
        >
          <span v-if="!isAnalyzing" class="flex items-center gap-2">
            <Sparkles class="w-5 h-5" /> AI 분석 시작
          </span>
          <span v-else class="flex items-center gap-2">
            <Loader2 class="w-5 h-5 animate-spin" /> 분석 중...
          </span>
        </button>
      </div>

    </div>
  </div>
</template>