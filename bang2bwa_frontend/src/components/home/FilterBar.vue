<template>
    <div class="filter-bar">
        <div class="search-wrapper">
            <input v-model="searchQuery" @keyup.enter="emitChange" type="text" placeholder="지역, 지하철, 학교 검색" class="search-input">
            <button @click="emitChange" class="search-btn" title="검색">
                <Search class="w-4 h-4" />
            </button>
        </div>

        <div class="filter-divider"></div>

        <select v-model="tradeType" @change="emitChange" class="filter-select">
            <option value="전체">전체</option>
            <option value="월세">월세</option>
            <option value="전세">전세</option>
            <option value="매매">매매</option>
        </select>

        <div class="relative">
            <button @click="toggleAreaDropdown" class="dropdown-btn">
                <span>{{ currentAreaLabel }}</span>
                <ChevronDown class="w-4 h-4 text-gray-400 ml-2" :class="{ 'rotate-180': isAreaOpen }" />
            </button>

            <div v-if="isAreaOpen" class="dropdown-box">
                <div class="flex justify-between items-center mb-2">
                    <span class="text-xs font-bold text-gray-600">면적 범위 선택</span>
                    <span class="text-xs text-[#AE8B72] font-bold">{{ currentAreaLabel }}</span>
                </div>

                <input type="range" v-model="areaValue" @change="emitChange" min="0" max="4" step="1" class="range-slider">

                <div class="flex justify-between text-[10px] text-gray-400 mt-1">
                    <span>최소</span>
                    <span>최대</span>
                </div>
            </div>
            <div v-if="isAreaOpen" @click="isAreaOpen = false" class="fixed inset-0 z-40 cursor-default"></div>
        </div>

        <select v-model="floorOption" @change="emitChange" class="filter-select">
            <option value="전체">전체</option>
            <option value="1층">1층</option>
            <option value="2층">2층</option>
            <option value="3층 이상">3층 이상</option>
            <option value="옥탑">옥탑</option>
            <option value="지하">지하</option>
        </select>

        <button @click="$emit('open-ai')" class="btn-ai">
            <Sparkles class="w-4 h-4" /> AI 맞춤 추천
        </button>
    </div>
</template>

<script setup>

import { ref, computed } from 'vue';
import { Search, Sparkles, ChevronDown } from 'lucide-vue-next';

// HomeView에게 변경된 값을 알리기 위한 이벤트
const emit = defineEmits(['filter-change', 'open-ai']);

// 상태 변수
const searchQuery = ref('');    // 검색어
const tradeType = ref('전체'); // 월세/전세/매매
const houseType = ref('');   // 매물 종류
const floorOption = ref('전체'); // 층수

// 전용면적
const isAreaOpen = ref(false);  // 드롭다운 열림/닫힘 상태
const areaValue = ref(0);       // 슬라이더 값

// 슬라이더 값에 따른 텍스트 매핑
const areaLabels = [
    '전체 면적',
    '33㎡ 이하 (약 10평)',
    '33㎡ ~ 66㎡ (10~20평)',
    '66㎡ ~ 99㎡ (20~30평)',
    '99㎡ 이상 (30평대~)'
];

// 선택된 라벨
const currentAreaLabel = computed(() => areaLabels[areaValue.value]);

const toggleAreaDropdown = () => {
    isAreaOpen.value = !isAreaOpen.value;
};

// 통합 변경
const emitChange = () => {
    emit('filter-change', {
        keyword: searchQuery.value,
        houseType: houseType.value, // 아파트, 오피스텔, 원룸
        tradeType: tradeType.value, // 매매, 전세, 월세
        floor: floorOption.value, // 층
        excluUseAr: currentAreaLabel.value, // 전용 면적
    });
};

const resetFilter = () => {
    searchQuery.value = '';
    tradeType.value = '전체';
    houseType.value = '전체';
    floorOption.value = '전체';
    areaValue.value = 0; // 슬라이더도 0(전체)으로
    isAreaOpen.value = false; // 열려있다면 닫기
}

defineExpose({
    resetFilter
});

</script>