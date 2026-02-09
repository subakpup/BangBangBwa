<template>
  <div v-show="show" class="fixed inset-0 bg-black/50 z-[9999] flex items-center justify-center p-4 backdrop-blur-sm transition-opacity"
       :class="show ? 'opacity-100' : 'opacity-0 pointer-events-none'">
    
    <div
      class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl overflow-hidden animate-fade-in-up flex flex-col max-h-[90vh]">

      <div class="bg-primary p-6 text-white flex justify-between items-start flex-shrink-0">
        <div>
          <h3 class="text-2xl font-bold flex items-center gap-2">
            <Bot class="w-8 h-8" /> AI 맞춤 추천
          </h3>
          <p class="text-white/80 mt-1 text-sm">원하는 조건을 입력하면 AI가 딱 맞는 집을 찾아드려요!</p>
        </div>
        <button @click="$emit('close')"
          class="text-white/80 hover:text-white hover:bg-white/10 rounded-full p-2 transition">
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
            <button v-for="label in ['월세', '전세', '매매']" :key="label" @click="setTradeType(label)"
              :class="form.tradeType === tradeTypeMap[label] ? 'bg-primary text-white border-primary ring-2 ring-primary ring-offset-2' : 'bg-white text-gray-500 border-gray-200 hover:border-primary hover:text-primary'"
              class="py-3 rounded-xl border font-bold transition duration-200">
              {{ label }}
            </button>
          </div>
        </section>

        <section class="bg-gray-50 p-5 rounded-xl border border-gray-100">
          <label class="block text-sm font-bold text-gray-700 mb-4 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">2</span>
            예산 범위 설정 (단위: 만원)
          </label>

          <div v-if="form.tradeType === 'RENT'" class="space-y-4">
            <div>
              <div class="flex justify-between text-xs text-gray-500 mb-1">
                <span>보증금</span>
              </div>
              <div class="flex items-center gap-2">
                <input v-model="form.deposit.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
                <span>~</span>
                <input v-model="form.deposit.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
              </div>
            </div>
            <div>
              <div class="flex justify-between text-xs text-gray-500 mb-1">
                <span>월세</span>
              </div>
              <div class="flex items-center gap-2">
                <input v-model="form.monthlyRent.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
                <span>~</span>
                <input v-model="form.monthlyRent.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
              </div>
            </div>
          </div>

          <div v-else-if="form.tradeType === 'LEASE'">
            <div class="flex justify-between text-xs text-gray-500 mb-1">
              <span>전세 보증금</span>
            </div>
            <div class="flex items-center gap-2">
              <input v-model="form.deposit.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
              <span>~</span>
              <input v-model="form.deposit.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
            </div>
          </div>

          <div v-else>
            <div class="flex justify-between text-xs text-gray-500 mb-1">
              <span>매매가</span>
            </div>
            <div class="flex items-center gap-2">
              <input v-model="form.dealAmount.min" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최소">
              <span>~</span>
              <input v-model="form.dealAmount.max" type="number" class="w-full p-2 border rounded-md text-sm" placeholder="최대">
            </div>
          </div>
        </section>

        <section>
          <label class="block text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">3</span>
            희망 지역
          </label>
          <div class="relative">
            <input v-model="form.location" type="text" placeholder="예: 강남구, 역삼동 (행정구역명 입력)"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition"
              @keyup.enter="handleSearch">
            <Search class="absolute left-3 top-3.5 w-5 h-5 text-gray-400" />
          </div>
        </section>

        <section>
          <label class="block text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
            <span class="bg-secondary text-white w-5 h-5 rounded-full flex items-center justify-center text-xs">4</span>
            선호 옵션 (중복 선택 가능)
          </label>
          <div class="grid grid-cols-2 md:grid-cols-5 gap-3">
            <button v-for="cat in infraCategories" :key="cat.name" @click="toggleOption(cat.name)"
              :class="form.options.includes(cat.name) ? 'bg-primary/10 border-primary text-primary' : 'bg-white border-gray-200 text-gray-500 hover:bg-gray-50'"
              class="flex flex-col items-center justify-center p-3 rounded-xl border transition duration-200 gap-1 h-20">
              <component :is="cat.icon" class="w-6 h-6"
                :class="form.options.includes(cat.name) ? 'text-primary' : 'text-gray-400'" />
              <span class="text-xs font-medium">{{ cat.name }}</span>
            </button>
          </div>
        </section>
      </div>

      <div class="p-5 border-t border-gray-100 bg-gray-50 flex-shrink-0 flex justify-end gap-3">
        <button @click="$emit('close')"
          class="px-6 py-3 rounded-xl text-gray-500 hover:bg-gray-200 font-bold transition">
          취소
        </button>
        <button @click="handleSearch"
          class="px-8 py-3 bg-gradient-to-r from-primary to-secondary text-white rounded-xl font-bold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition flex items-center gap-2">
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

<script setup>
import { ref, reactive, watch } from 'vue'; // [추가] watch import
import { Bot, X, Search, Sparkles, Loader2 } from 'lucide-vue-next';
import { infraCategories, formatMoney, tradeTypeMap } from '@/utils/productUtil';
import { recommendProducts } from '@/api/productApi';

const props = defineProps(['show']);
const emit = defineEmits(['close', 'search']);

const isAnalyzing = ref(false);

const form = reactive({
  tradeType: 'RENT',
  deposit: { min: null, max: null },
  monthlyRent: { min: null, max: null },
  dealAmount: { min: null, max: null },
  location: '',
  options: [],
});

// [최적화 2] 모달이 열릴 때마다 폼 데이터 초기화 (v-show로 변경했으므로 수동 초기화 필요)
watch(() => props.show, (newVal) => {
  if (newVal) {
    resetForm();
  }
});

const resetForm = () => {
  form.tradeType = 'RENT';
  form.deposit = { min: null, max: null };
  form.monthlyRent = { min: null, max: null };
  form.dealAmount = { min: null, max: null };
  form.location = '';
  form.options = [];
  isAnalyzing.value = false;
};

const setTradeType = (label) => {
  form.tradeType = tradeTypeMap[label];
  // 타입 변경 시 해당 금액 필드만 초기화해도 되지만, 깔끔하게 관련 필드 초기화
  form.deposit = { min: null, max: null };
  form.monthlyRent = { min: null, max: null };
  form.dealAmount = { min: null, max: null };
};

const toggleOption = (name) => {
  if (form.options.includes(name)) {
    form.options = form.options.filter(opt => opt !== name);
  } else {
    form.options.push(name);
  }
};

const handleSearch = async () => {
  if (isAnalyzing.value) return;

  if (!form.location) {
    alert('희망 지역을 입력해주세요!');
    return;
  }

  isAnalyzing.value = true;

  try {
    const payload = {
      tradeType: form.tradeType,
      location: form.location,
      options: form.options,
      deposit: (form.tradeType === 'RENT' || form.tradeType === 'LEASE') ? { min: form.deposit.min, max: form.deposit.max } : null,
      monthlyRent: (form.tradeType === 'RENT') ? { min: form.monthlyRent.min, max: form.monthlyRent.max } : null,
      dealAmount: (form.tradeType === 'SALE') ? { min: form.dealAmount.min, max: form.dealAmount.max } : null
    };

    const response = await recommendProducts(payload);

    if (response.success === "SUCCESS") {
      emit('search', response.data);
      emit('close');
    } else {
      alert(response.message || "데이터를 불러오지 못했습니다.");
    }
  } catch (error) {
    console.error(error);
    alert("분석 중 오류가 발생했습니다.");
  } finally {
    isAnalyzing.value = false;
  }
};
</script>