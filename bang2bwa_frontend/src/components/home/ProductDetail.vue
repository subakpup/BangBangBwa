<template>
    <div class="detail-container">
        <div class="detail-header">
            <button @click="emit('close')" class="btn-back">
                <ArrowLeft class="w-6 h-6" />
            </button>
        </div>

        <div class="detail-body">
            <div class="hero-img-area">
                <img :src="(item.images && item.images.length > 0) ? item.images[0].url : '/no-image.png'" 
                     class="w-full h-full object-cover" 
                     alt="상세 이미지">
                <div class="img-badge">사진 더보기</div>
            </div>

            <div class="detail-content">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">{{ formatPrice(item) }}</h1>
                    <h2 class="text-lg text-gray-700 font-medium mt-1">{{ item.name }}</h2>
                    <div class="flex items-center gap-1 text-gray-500 text-sm mt-2">
                        <MapPin class="w-4 h-4" />
                        <span>{{ fullAddress }}</span>
                    </div>
                </div>

                <hr class="border-gray-100 my-6">

                <div class="info-grid">
                    <div class="info-item">
                        <div class="icon-box"><Ruler class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">전용면적</p>
                            <p class="info-value">
                                {{ item.excluUseAr }}㎡ <span class="text-[#AE8B72] text-sm">({{ formatPyeong(item.excluUseAr) }}평)</span>
                            </p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Building class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">동 / 층</p>
                            <p class="info-value">{{ formatFloor(item) }}</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Calendar class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">사용승인일</p>
                            <p class="info-value">{{ item.buildYear }}년</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Info class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">매물유형</p>
                            <p class="info-value">{{ typeMap[item.houseType] || item.houseType }}</p>
                        </div>
                    </div>
                </div>

                <hr class="border-gray-100 my-6">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">상세 설명</h3>
                    <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line min-h-[100px]">
                        {{ item.desc ? item.desc : '상세 설명이 없습니다.' }}
                    </p>
                </div>

                <hr class="border-gray-100 my-6">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">매물 상세 정보</h3>
                    
                    <div class="w-full border-t border-gray-200 text-sm">
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">대지권 면적</div>
                            <div class="p-3 text-gray-900">{{ item.landAr ? item.landAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">연면적</div>
                            <div class="p-3 text-gray-900">{{ item.totalFloorAr ? item.totalFloorAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">대지면적</div>
                            <div class="p-3 text-gray-900">{{ item.plottageAr ? item.plottageAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">법정동</div>
                            <div class="p-3 text-gray-900">{{ item.umdNm || '-' }}</div>
                        </div>
                    </div>
                </div>

                <div class="h-8"></div>

                <div class="agent-card bg-white border border-[#CEAC93] rounded-lg p-4 flex items-center gap-4 shadow-sm">
                    <div class="w-12 h-12 bg-[#FFFBE8] rounded-full flex items-center justify-center text-[#AE8B72]">
                        <UserCircle2 class="w-8 h-8" />
                    </div>
                    <div>
                        <p class="text-xs text-gray-500">담당 공인중개사</p>
                        <p class="font-bold text-gray-900 text-lg">김싸피 중개사</p>
                        <p class="text-xs text-gray-400">대박 공인중개사 사무소</p>
                    </div>
                </div>
                <div class="h-4"></div>
            </div>
        </div>

        <div class="detail-footer">
            <button class="btn-like">
                <Heart class="w-5 h-5 fill-[#AE8B72] text-[#AE8B72]" /> 찜하기
            </button>
            <button class="btn-contact">
                <Phone class="w-5 h-5" /> 문의하기
            </button>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { ArrowLeft, MapPin, Building, Ruler, Calendar, Phone, Heart, UserCircle2, Info } from 'lucide-vue-next';
import { formatPrice, typeMap } from '@/utils/productUtil';

const props = defineProps(['item']);
const emit = defineEmits(['close']);

// [추가] 전체 주소 계산 (sggNm + " " + umdNm + " " + jibun)
const fullAddress = computed(() => {
    if (!props.item) return '';
    const sgg = props.item.sggNm || '';
    const umd = props.item.umdNm || '';
    const jibun = props.item.jibun || '';
    
    // 공백 중복 방지 및 trim 처리
    return `${sgg} ${umd} ${jibun}`.replace(/\s+/g, ' ').trim();
});

// 평수 계산
const formatPyeong = (m2) => {
    if (!m2) return '-';
    return (m2 / 3.3058).toFixed(1);
}

// 층수 표시
const formatFloor = (item) => {
    let text = item.floor ? `${item.floor}` : '-';
    if (item.aptDong) text = `${item.aptDong} / ${text}`;
    return text;
}
</script>

<style scoped>
/* Scoped CSS가 필요하다면 여기에 작성하지만, 
   현재는 Tailwind Utility Class로 Template 내에서 직접 해결하여 깨짐을 방지했습니다. */

.detail-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: white;
}

.detail-header {
    padding: 1rem;
    position: sticky;
    top: 0;
    background: white;
    z-index: 10;
    border-bottom: 1px solid #f3f4f6;
}

.detail-body {
    flex: 1;
    overflow-y: auto;
}

.hero-img-area {
    height: 18rem;
    width: 100%;
    background-color: #e5e7eb;
    position: relative;
}

.img-badge {
    position: absolute;
    bottom: 1rem;
    right: 1rem;
    background-color: rgba(0,0,0,0.6);
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 9999px;
    font-size: 0.75rem;
}

.detail-content {
    padding: 1.5rem;
}

.info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
}

.info-item {
    display: flex;
    align-items: start;
    gap: 0.75rem;
}

.icon-box {
    padding: 0.5rem;
    background-color: #f3f4f6;
    border-radius: 0.5rem;
    color: #4b5563;
}

.info-label {
    font-size: 0.75rem;
    color: #6b7280;
}

.info-value {
    font-weight: 700;
    color: #1f2937;
    white-space: nowrap;
}

.detail-footer {
    padding: 1rem;
    border-top: 1px solid #e5e7eb;
    background-color: white;
    display: flex;
    gap: 0.75rem;
    box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.05);
}

.btn-like {
    flex: 1;
    border: 1px solid rgba(174, 139, 114, 0.2);
    background-color: rgba(206, 172, 147, 0.1);
    padding: 0.75rem;
    border-radius: 0.75rem;
    font-weight: 700;
    color: #AE8B72;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: background-color 0.2s;
}

.btn-contact {
    flex: 2;
    background-color: #AE8B72;
    color: white;
    padding: 0.75rem;
    border-radius: 0.75rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: background-color 0.2s;
}
.btn-contact:hover {
    background-color: #8c6b54;
}
</style>