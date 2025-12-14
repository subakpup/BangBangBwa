<template>
    <div class="detail-container">
        <div class="detail-header">
            <button @click="emit('close')" class="btn-back">
                <ArrowLeft class="w-6 h-6" />
            </button>
        </div>

        <div class="detail-body">
            <div class="hero-img-area">
                <img :src="item.image" class="w-full h-full object-cover" alt="상세 이미지">
                <div class="img-badge">사진 더보기 1/5</div>
            </div>

            <div class="detail-content">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">{{ formatPrice(item) }}</h1>
                    <h2 class="text-lg text-gray-700 font-medium mt-1">{{ item.name }}</h2>
                    <div class="flex items-center gap-1 text-gray-500 text-sm mt-2">
                        <MapPin class="w-4 h-4" />
                        <span>{{ item.jibun }}</span>
                    </div>
                </div>

                <hr class="border-gray-100">

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

                <hr class="border-gray-100">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">상세 설명</h3>
                    <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line">
                        {{ item.desc }}<br>
                        이 매물은 {{ item.jibun }}에 위치한 {{ item.name }}입니다.
                        {{ item.buildYear }}년에 준공되었으며, 주변 인프라가 훌륭합니다.
                    </p>
                </div>

                <hr class="border-gray-100">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">매물 상세 정보</h3>
                    <div class="detail-table">
                        <div class="table-row">
                            <div class="table-head">대지권 면적</div>
                            <div class="table-data">{{ item.landAr ? item.landAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-head">연면적</div>
                            <div class="table-data">{{ item.totalFloorAr ? item.totalFloorAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-head">대지면적</div>
                            <div class="table-data">{{ item.plottageAr ? item.plottageAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-head">법정동</div>
                            <div class="table-data">{{ item.umdNm }}</div>
                        </div>
                    </div>
                </div>

                <div class="agent-card">
                    <div class="agent-avatar"><UserCircle2 class="w-8 h-8" /></div>
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
import { ArrowLeft, MapPin, Building, Ruler, Calendar, Phone, Heart, UserCircle2, Info } from 'lucide-vue-next';
import { formatPrice, typeMap } from '@/utils/productUtil';

const props = defineProps(['item']);
const emit = defineEmits(['close']);

// 평수 계산
const formatPyeong = (m2) => {
    if (!m2) return '-';
    return (m2 / 3.3058).toFixed(1); // 소수점 1자리까지
}

// 층수 표시
const formatFloor = (item) => {
    let text = item.floor;
    if (item.aptDong) text = `${item.aptDong} / ${text}`;
    return text;
}
</script>

<style lang="scss" scoped></style>