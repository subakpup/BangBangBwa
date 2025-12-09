<template>
    <div class="h-full flex flex-col bg-white">

        <!--상단(뒤로 가기 버튼)-->
        <div class="p-4 border-b border-gray-100 flex items-center justify-between bg-white z-10 sticky top-0">
            <button @click="emit('close')" class="hover:bg-gray-100 p-2 rounded-full transition">
                <ArrowLeft class="w-6 h-6 text-gray-700" />
            </button>
        </div>

        <!--스크롤 바 영역(메인)-->
        <div class="flex-1 overflow-y-auto custom-scrollbar">

            <!--사진-->
            <div class="h-72 w-full bg-gray-200 relative">
                <img :src="item.image" class="w-full h-full object-cover" alt="상세 이미지">
                <div
                    class="absolute bottom-4 right-4 bg-black/60 text-white px-3 py-1 rounded-full text-xs backdrop-blur-sm">
                    사진 더보기 1/5
                </div>
            </div>

            <!--본문-->
            <div class="p-6 space-y-8">

                <!--거래 타입/이름/주소-->
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">{{ formatPrice(item) }}</h1>
                    <h2 class="text-lg text-gray-700 font-medium mt-1">{{ item.name }}</h2>
                    <div class="flex items-center gap-1 text-gray-500 text-sm mt-2">
                        <MapPin class="w-4 h-4" />
                        <span>{{ item.jibun }}</span>
                    </div>
                </div>

                <hr class="border-gray-100">

                <!--전용 면적/층/사용승인일/매물유형-->
                <div class="grid grid-cols-2 gap-y-6 gap-x-4">
                    <div class="flex items-start gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg text-gray-600">
                            <Ruler class="w-6 h-6" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">전용면적</p>
                            <p class="font-bold text-gray-800 whitespace-nowrap">
                                {{ item.excluUseAr }}㎡ <span class="text-primary text-sm">({{
                                    formatPyeong(item.excluUseAr) }}평)</span>
                            </p>
                        </div>
                    </div>

                    <div class="flex items-start gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg text-gray-600">
                            <Building class="w-6 h-6" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">동 / 층</p>
                            <p class="font-bold text-gray-800">{{ formatFloor(item) }}</p>
                        </div>
                    </div>

                    <div class="flex items-start gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg text-gray-600">
                            <Calendar class="w-6 h-6" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">사용승인일</p>
                            <p class="font-bold text-gray-800">{{ item.buildYear }}년</p>
                        </div>
                    </div>

                    <div class="flex items-start gap-3">
                        <div class="p-2 bg-gray-100 rounded-lg text-gray-600">
                            <Info class="w-6 h-6" />
                        </div>
                        <div>
                            <p class="text-xs text-gray-500">매물유형</p>
                            <p class="font-bold text-gray-800">{{ typeMap[item.houseType] || item.houseType }}</p>
                        </div>
                    </div>
                </div>

                <hr class="border-gray-100">

                <!--상세 설명-->
                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">상세 설명</h3>
                    <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line">
                        {{ item.desc }}
                        <br>
                        이 매물은 {{ item.jibun }}에 위치한 {{ item.name }}입니다.
                        {{ item.buildYear }}년에 준공되었으며, 주변 인프라가 훌륭합니다.
                        즉시 입주 가능하며, 주차 공간도 확보되어 있습니다.
                    </p>
                </div>

                <hr class="border-gray-100">

                <!--매물 상세 정보(대지권 면적/연면적/대지면적/법정동)-->
                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">매물 상세 정보</h3>
                    <div class="bg-gray-50 rounded-lg border border-gray-100 overflow-hidden text-sm">
                        <div class="grid grid-cols-2 border-b border-gray-200">
                            <div class="p-3 text-gray-500 bg-gray-100 border-r border-gray-200">대지권 면적</div>
                            <div class="p-3 text-gray-800">{{ item.landAr ? item.landAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-2 border-b border-gray-200">
                            <div class="p-3 text-gray-500 bg-gray-100 border-r border-gray-200">연면적</div>
                            <div class="p-3 text-gray-800">{{ item.totalFloorAr ? item.totalFloorAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-2 border-b border-gray-200">
                            <div class="p-3 text-gray-500 bg-gray-100 border-r border-gray-200">대지면적</div>
                            <div class="p-3 text-gray-800">{{ item.plottageAr ? item.plottageAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-2">
                            <div class="p-3 text-gray-500 bg-gray-100 border-r border-gray-200">법정동</div>
                            <div class="p-3 text-gray-800">{{ item.umdNm }}</div>
                        </div>
                    </div>
                </div>

                <!--중개사 정보-->
                <div class="bg-white border border-accent rounded-xl p-4 shadow-sm flex items-center gap-4">
                    <div class="w-12 h-12 bg-secondary/20 rounded-full flex items-center justify-center">
                        <UserCircle2 class="w-8 h-8 text-primary" />
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

        <div class="p-4 border-t border-gray-200 bg-white flex gap-3 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)] z-20">

            <!--찜하기 버튼-->
            <button
                class="flex-1 border border-primary/20 bg-secondary/10 py-3 rounded-xl font-bold text-primary hover:bg-secondary/20 transition flex items-center justify-center gap-2">
                <Heart class="w-5 h-5 fill-primary text-primary" /> 찜하기
            </button>

            <!--문의하기 버튼(나중에 예약하기 버튼으로 변환)-->
            <button
                class="flex-[2] bg-primary text-white py-3 rounded-xl font-bold hover:bg-[#96755e] transition flex items-center justify-center gap-2 shadow-md">
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