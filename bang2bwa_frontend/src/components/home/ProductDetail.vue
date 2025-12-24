<template>
    <div class="detail-container">
        <div class="detail-header">
            <button @click="emit('close')" class="btn-back">
                <ArrowLeft class="w-6 h-6" />
            </button>
        </div>

        <div class="detail-body">
            <div class="hero-img-area cursor-pointer group" @click="openGallery(0)">
                <img :src="getProductMainImage(product)"
                    class="w-full h-full object-cover transition-transform group-hover:scale-105 duration-700"
                    alt="상세 이미지">

                <div class="img-badge group-hover:bg-black/80 transition-colors">
                    <Images class="w-3 h-3 inline mr-1" />
                    사진 더보기 {{ product.images ? product.images.length : 0 }}장
                </div>
            </div>

            <div class="detail-content">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">{{ formatPrice(product) }}</h1>
                    <h2 class="text-lg text-gray-700 font-medium mt-1">{{ product.name }}</h2>
                    <div class="flex items-center gap-1 text-gray-500 text-sm mt-2">
                        <MapPin class="w-4 h-4" />
                        <span>{{ formatAddress(product) }}</span>
                    </div>
                </div>

                <hr class="border-gray-100 my-6">

                <div class="info-grid">
                    <div class="info-item">
                        <div class="icon-box"><Ruler class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">전용면적</p>
                            <p class="info-value">
                                {{ product.excluUseAr }}㎡ <span class="text-[#AE8B72] text-sm">({{ formatPyeong(product.excluUseAr) }}평)</span>
                            </p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Building class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">동 / 층</p>
                            <p class="info-value">{{ formatFloor(product) }}</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Calendar class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">사용승인일</p>
                            <p class="info-value">{{ product.buildYear }}년</p>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="icon-box"><Info class="w-6 h-6" /></div>
                        <div>
                            <p class="info-label">매물유형</p>
                            <p class="info-value">{{ typeMap[product.houseType] || product.houseType }}</p>
                        </div>
                    </div>
                </div>

                <hr class="border-gray-100 my-6">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">상세 설명</h3>
                    <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line min-h-[100px]">
                        {{ product.desc ? product.desc : '상세 설명이 없습니다.' }}
                    </p>
                </div>

                <hr class="border-gray-100 my-6">

                <div>
                    <h3 class="font-bold text-lg text-gray-900 mb-3">매물 상세 정보</h3>
                    <div class="w-full border-t border-gray-200 text-sm">
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">대지권 면적</div>
                            <div class="p-3 text-gray-900">{{ product.landAr ? product.landAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">연면적</div>
                            <div class="p-3 text-gray-900">{{ product.totalFloorAr ? product.totalFloorAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">대지면적</div>
                            <div class="p-3 text-gray-900">{{ product.plottageAr ? product.plottageAr + '㎡' : '-' }}</div>
                        </div>
                        <div class="grid grid-cols-[100px_1fr] border-b border-gray-100">
                            <div class="bg-gray-50 p-3 text-gray-500 font-medium flex items-center">법정동</div>
                            <div class="p-3 text-gray-900">{{ product.umdNm || '-' }}</div>
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
                        <p class="font-bold text-gray-900 text-lg">
                            {{ product.agentName || '공인중개사' }}
                        </p>
                        <p class="text-xs text-gray-400">
                            {{ product.agentOfficeName || '사무소 정보 없음' }}
                        </p>
                    </div>
                </div>
                <div class="h-4"></div>
            </div>
        </div>

        <div class="detail-footer">
            <button class="btn-like" @click="addWish">
                <Heart class="w-5 h-5 fill-[#AE8B72] text-[#AE8B72]" /> 찜하기
            </button>
            <button class="btn-contact">
                <Phone class="w-5 h-5" /> 문의하기
            </button>
        </div>

        <div v-if="isGalleryOpen" class="gallery-overlay" @click.self="closeGallery">
            <button class="gallery-close" @click="closeGallery">
                <X class="w-8 h-8 text-white" />
            </button>

            <button class="gallery-nav left" @click.stop="prevImage" v-if="hasMultipleImages">
                <ChevronLeft class="w-10 h-10 text-white" />
            </button>

            <div class="gallery-content">
                <img :src="getCurrentImageUrl" class="gallery-img" alt="갤러리 이미지" />
                <div class="gallery-counter">
                    {{ currentImageIndex + 1 }} / {{ product.images.length }}
                </div>
            </div>

            <button class="gallery-nav right" @click.stop="nextImage" v-if="hasMultipleImages">
                <ChevronRight class="w-10 h-10 text-white" />
            </button>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue';
import {
    ArrowLeft, MapPin, Building, Ruler, Calendar, Phone, Heart, UserCircle2, Info,
    Images, X, ChevronLeft, ChevronRight
} from 'lucide-vue-next';

// [수정] 유틸 함수들 import
import { formatPrice, typeMap, getProductMainImage, formatAddress } from '@/utils/productUtil';
import { api } from '@/api/index';
import { findById, addWishList, removeWishList } from '@/api/productApi';

// [중요] 기본 이미지 import (assets 폴더에 해당 파일이 있어야 합니다!)
import defaultImg from '@/assets/no-image.jpg';

const props = defineProps(['item']);
const emit = defineEmits(['close']);

const product = ref({ ...props.item }); 

// 상세 정보 다시 로드 (이미지 리스트 확보용)
onMounted(async () => {
    if (props.item && props.item.productId) {
        try {
            const data = await findById(props.item.productId);
            if (data) {
                product.value = data;
            }
        } catch (error) {
            console.error("[ERROR] 상세 정보 조회 실패:", error);
        }
    }
});

// --- 갤러리 로직 ---
const isGalleryOpen = ref(false);
const currentImageIndex = ref(0);

const hasMultipleImages = computed(() => {
    return product.value.images && product.value.images.length > 1;
});

// [수정] 갤러리 내부용 이미지 URL 생성 함수
const getGalleryImageUrl = (img) => {
    if (!img) return defaultImg; // import한 변수 사용

    if (img.url && img.url.startsWith('http')) return img.url;

    const path = img.savePath || img.url;
    if (path) {
        const baseUrl = api.defaults.baseURL || 'http://localhost:8080';
        // WebConfig와 일치하는 경로 사용 (/images/)
        return `${baseUrl}/images/${path}`;
    }
    return defaultImg; // import한 변수 사용
};

// 현재 갤러리 이미지 URL
const getCurrentImageUrl = computed(() => {
    if (!product.value.images || product.value.images.length === 0) return defaultImg;
    return getGalleryImageUrl(product.value.images[currentImageIndex.value]);
});

const openGallery = (index = 0) => {
    if (!product.value.images || product.value.images.length === 0) return;
    currentImageIndex.value = index;
    isGalleryOpen.value = true;
    document.body.style.overflow = 'hidden';
};

const closeGallery = () => {
    isGalleryOpen.value = false;
    document.body.style.overflow = '';
};

const prevImage = () => {
    if (currentImageIndex.value > 0) {
        currentImageIndex.value--;
    } else {
        currentImageIndex.value = product.value.images.length - 1;
    }
};

const nextImage = () => {
    if (currentImageIndex.value < product.value.images.length - 1) {
        currentImageIndex.value++;
    } else {
        currentImageIndex.value = 0;
    }
};

const handleKeydown = (e) => {
    if (!isGalleryOpen.value) return;
    if (e.key === 'Escape') closeGallery();
    if (e.key === 'ArrowLeft') prevImage();
    if (e.key === 'ArrowRight') nextImage();
};

onMounted(() => window.addEventListener('keydown', handleKeydown));
onUnmounted(() => window.removeEventListener('keydown', handleKeydown));

const formatPyeong = (m2) => m2 ? (m2 / 3.3058).toFixed(1) : '-';
const formatFloor = (p) => {
    let text = p.floor ? `${p.floor}` : '-';
    if (p.aptDong) text = `${p.aptDong} / ${text}`;
    return text;
};

const addWish = async () => {
    let response = await addWishList(props.item.productId);
    
    alert(response.message);
};

</script>

<style scoped>
/* 스타일은 기존 유지 */
.detail-container { height: 100%; display: flex; flex-direction: column; background-color: white; }
.detail-header { padding: 1rem; position: sticky; top: 0; background: white; z-index: 10; border-bottom: 1px solid #f3f4f6; }
.detail-body { flex: 1; overflow-y: auto; }
.hero-img-area { height: 18rem; width: 100%; background-color: #e5e7eb; position: relative; overflow: hidden; }
.img-badge { position: absolute; bottom: 1rem; right: 1rem; background-color: rgba(0, 0, 0, 0.6); color: white; padding: 0.35rem 0.85rem; border-radius: 9999px; font-size: 0.8rem; font-weight: 500; backdrop-filter: blur(4px); display: flex; align-items: center; }
.detail-content { padding: 1.5rem; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.5rem; }
.info-item { display: flex; align-items: start; gap: 0.75rem; }
.icon-box { padding: 0.5rem; background-color: #f3f4f6; border-radius: 0.5rem; color: #4b5563; }
.info-label { font-size: 0.75rem; color: #6b7280; }
.info-value { font-weight: 700; color: #1f2937; white-space: nowrap; }
.detail-footer { padding: 1rem; border-top: 1px solid #e5e7eb; background-color: white; display: flex; gap: 0.75rem; box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.05); }
.btn-like { flex: 1; border: 1px solid rgba(174, 139, 114, 0.2); background-color: rgba(206, 172, 147, 0.1); padding: 0.75rem; border-radius: 0.75rem; font-weight: 700; color: #AE8B72; display: flex; align-items: center; justify-content: center; gap: 0.5rem; transition: background-color 0.2s; }
.btn-contact { flex: 2; background-color: #AE8B72; color: white; padding: 0.75rem; border-radius: 0.75rem; font-weight: 700; display: flex; align-items: center; justify-content: center; gap: 0.5rem; transition: background-color 0.2s; }
.btn-contact:hover { background-color: #8c6b54; }
.gallery-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.95); z-index: 9999; display: flex; align-items: center; justify-content: center; animation: fadeIn 0.2s ease-out; }
.gallery-content { position: relative; max-width: 90%; max-height: 85%; }
.gallery-img { max-width: 100%; max-height: 80vh; object-fit: contain; border-radius: 4px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5); }
.gallery-close { position: absolute; top: 2rem; right: 2rem; background: none; border: none; cursor: pointer; padding: 0.5rem; z-index: 100; }
.gallery-nav { position: absolute; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; padding: 1rem; transition: transform 0.2s; }
.gallery-nav:hover { transform: translateY(-50%) scale(1.1); }
.gallery-nav.left { left: 1rem; }
.gallery-nav.right { right: 1rem; }
.gallery-counter { text-align: center; color: white; margin-top: 1rem; font-size: 1.1rem; font-weight: 500; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>