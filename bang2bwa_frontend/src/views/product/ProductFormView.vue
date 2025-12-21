<template>
    <div class="regist-wrapper custom-scrollbar">
        <div class="regist-card animate-fade-in-up">

            <div class="regist-header">
                <h2 class="regist-title">{{ isEditMode ? '매물 정보 수정' : '매물 등록' }}</h2>
            </div>

            <form @submit.prevent="submitForm">

                <section class="regist-section">
                    <h3 class="section-title">기본 정보</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label class="form-label">매물 종류 <span class="required-mark">*</span></label>
                            <select v-model="product.houseType" class="regist-input" required>
                                <option v-for="(val, key) in typeMap" :key="key" :value="key">{{ val }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">거래 유형 <span class="required-mark">*</span></label>
                            <select v-model="product.tradeType" class="regist-input" required>
                                <option v-for="(val, key) in tradeTypeMap" :key="val" :value="val">{{ key }}</option>
                            </select>
                        </div>
                    </div>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">가격 정보</h3>
                    <div class="highlight-box">

                        <div v-if="product.tradeType === 'SALE'" class="form-group">
                            <label class="form-label">매매가 (만원) <span class="required-mark">*</span></label>
                            <input type="number" v-model="product.dealAmount" placeholder="예: 35000"
                                class="regist-input bg-white" />
                        </div>

                        <div v-if="product.tradeType === 'LEASE'" class="form-group">
                            <label class="form-label">전세 보증금 (만원) <span class="required-mark">*</span></label>
                            <input type="number" v-model="product.deposit" placeholder="예: 20000"
                                class="regist-input bg-white" />
                        </div>

                        <div v-if="product.tradeType === 'RENT'" class="form-grid">
                            <div class="form-group">
                                <label class="form-label">보증금 (만원) <span class="required-mark">*</span></label>
                                <input type="number" v-model="product.deposit" placeholder="예: 1000"
                                    class="regist-input bg-white" />
                            </div>
                            <div class="form-group">
                                <label class="form-label">월세 (만원) <span class="required-mark">*</span></label>
                                <input type="number" v-model="product.monthlyRent" placeholder="예: 50"
                                    class="regist-input bg-white" />
                            </div>
                        </div>

                    </div>
                </section>

                <section class="regist-section">
                    <div class="flex justify-between items-end mb-4">
                        <h3 class="section-title mb-0">위치 정보</h3>
                        <button type="button" @click="openAddressSearch"
                            class="btn-base text-xs py-1.5 px-3 bg-gray-600 hover:bg-gray-700">
                            주소 검색
                        </button>
                    </div>

                    <div class="form-grid-3 mb-4">
                        <div class="form-group">
                            <label class="form-label">시/군/구</label>
                            <input type="text" v-model="product.sggNm" readonly class="regist-input readonly"
                                placeholder="주소 검색" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">법정동</label>
                            <input type="text" v-model="product.umdNm" readonly class="regist-input readonly" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">지번</label>
                            <input type="text" v-model="product.jibun" readonly class="regist-input readonly" />
                        </div>
                    </div>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">건물 상세</h3>
                    <div class="form-grid mb-4">
                        <div class="form-group">
                            <label class="form-label">건물명</label>
                            <input type="text" v-model="product.name" placeholder="예: 방방타워" class="regist-input" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">동</label>
                            <input type="text" v-model="product.aptDong" placeholder="예: 101동" class="regist-input" />
                        </div>
                    </div>
                    <div class="form-grid">
                        <div class="form-group">
                            <label class="form-label">층수</label>
                            <input type="text" v-model="product.floor" placeholder="예: 5" class="regist-input" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">건축년도</label>
                            <input type="number" v-model="product.buildYear" placeholder="예: 2024"
                                class="regist-input" />
                        </div>
                    </div>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">면적 정보 (m²)</h3>
                    <div class="form-grid-4">
                        <div class="form-group">
                            <label class="form-label">전용면적</label>
                            <input type="number" step="0.01" v-model="product.excluUseAr" class="regist-input" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">대지권면적</label>
                            <input type="number" step="0.01" v-model="product.landAr" class="regist-input" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">연면적</label>
                            <input type="number" step="0.01" v-model="product.totalFloorAr" class="regist-input" />
                        </div>
                        <div class="form-group">
                            <label class="form-label">대지면적</label>
                            <input type="number" step="0.01" v-model="product.plottageAr" class="regist-input" />
                        </div>
                    </div>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">상세 설명</h3>
                    <textarea v-model="product.desc" class="regist-textarea"
                        placeholder="매물의 특징을 자세히 적어주세요."></textarea>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">사진 등록</h3>

                    <div class="upload-area group" @click="$refs.fileInput.click()">
                        <input type="file" multiple @change="handleFileUpload" accept="image/*" class="hidden"
                            ref="fileInput" />
                        <div class="upload-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                                stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round"
                                    d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
                            </svg>
                        </div>
                        <p class="upload-text">클릭하여 사진을 추가하세요 (여러 장 선택 가능)</p>
                        <p class="text-xs text-[#AE8B72] mt-1 font-bold" v-if="selectedFiles.length > 0">
                            현재 {{ selectedFiles.length }}개의 새 파일 선택됨
                        </p>
                    </div>

                    <div v-if="product.images && product.images.length > 0" class="preview-grid">
                        <div v-for="(img, index) in product.images" :key="img.imageId || index" class="preview-item group">
                            <img :src="img.url" alt="매물 사진" class="preview-img" />
                            <button type="button" class="btn-img-delete"
                                @click="removeExistingImage(img.imageId, index)">
                                ✕
                            </button>
                        </div>
                    </div>
                </section>

                <div class="form-actions">
                    <button type="button" @click="$router.go(-1)" class="btn-cancel">취소</button>
                    <button type="submit" class="btn-save">
                        {{ isEditMode ? '수정 완료' : '매물 등록하기' }}
                    </button>
                </div>

            </form>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createProduct, findById, updateProduct } from '@/api/productApi';
import { tradeTypeMap, typeMap } from '@/utils/productUtil';

const route = useRoute();
const router = useRouter();

// 상태 변수
const isEditMode = ref(false);
const productId = ref(null);
const selectedFiles = ref([]);

// DTO 매핑
const product = ref({
    // --- 식별자 ---
    productId: null,
    agentId: null, // 로그인한 유저 정보에서 가져오거나, 조회 시 받아옴

    // --- 위치 정보 ---
    sggNm: '',      // 시군구
    umdNm: '',      // 법정동
    jibun: '',      // 지번
    latitude: 0.0,  // 위도
    longitude: 0.0, // 경도

    // --- 매물 기본 정보 ---
    name: '',       // 건물 이름
    aptDong: '',    // 동
    floor: '',      // 층 (String 타입임에 주의)
    buildYear: null,// 건축 년도

    // --- 면적 정보 (Double) ---
    excluUseAr: null,   // 전용 면적
    landAr: null,       // 대지권 면적
    totalFloorAr: null, // 연면적
    plottageAr: null,   // 대지면적

    // --- 거래 및 금액 정보 ---
    houseType: '',      // 'APARTMENT', 'OPIC', 'VILLA' 등 (Enum -> String)
    tradeType: '',      // 'DEAL'(매매), 'JEONSE'(전세), 'MONTHLY'(월세) (Enum -> String)
    status: 'AVAILABLE',// 'AVAILABLE', 'RESERVED', 'COMPLETED' (Enum -> String)

    dealAmount: 0,      // 매매가
    deposit: 0,         // 보증금
    monthlyRent: 0,     // 월세

    // --- 상세 설명 ---
    desc: '',           // 상세 내용 (변수명 desc 주의)

    // --- 이미지 처리 ---
    images: [],         // 조회 시 받아온 기존 이미지 리스트 (ProductImageDto 형태)
    deleteImageIds: []  // **수정 시 삭제할 이미지 ID들을 담을 리스트**
});

// 파일 선택 핸들러
const handleFileUpload = (event) => {
    selectedFiles.value = Array.from(event.target.files);
};

// 이미지 삭제 핸들러
const removeExistingImage = (imageId, index) => {
    product.value.images.splice(index, 1);
    if (imageId) {
        product.value.deleteImageIds.push(imageId);
    }
};

// 폼 제출 핸들러
const submitForm = async () => {
    try {
        let response;

        if (isEditMode.value) {
            response = await updateProduct(productId.value, product.value, selectedFiles.value);
        } else {
            response = await createProduct(product.value, selectedFiles.value);
        }

        if (response && response.message) {
            alert(response.message);
        }

        router.push({ name: 'ProductManage' }); // 관리 페이지로 이동
    } catch (error) {
        console.error(error);
    }
};

// 백엔드 API 호출 후 실행되는 함수
const loadProductData = async (id) => {
    try {
        const data = await findById(id);

        product.value = {
            productId: data.productId,
            agentId: data.agentId,

            // 주소 및 위치
            sggNm: data.sggNm,
            umdNm: data.umdNm,
            jibun: data.jibun,
            latitude: data.latitude,
            longitude: data.longitude,

            // 건물 정보
            name: data.name,
            aptDong: data.aptDong,
            floor: data.floor,
            buildYear: data.buildYear,

            // 면적
            excluUseAr: data.excluUseAr,
            landAr: data.landAr,
            totalFloorAr: data.totalFloorAr,
            plottageAr: data.plottageAr,

            // 타입 및 상태 (Enum은 문자열로 매핑됨)
            houseType: data.houseType,
            tradeType: data.tradeType,
            status: data.status,

            // 가격
            dealAmount: data.dealAmount,
            deposit: data.deposit,
            monthlyRent: data.monthlyRent,

            // 설명
            desc: data.desc,

            // 이미지
            // 백엔드에서 보내준 images 리스트를 그대로 매핑
            images: data.images || [],

            // 삭제할 이미지 ID 리스트는 초기화 (사용자가 삭제 버튼 누를 때 추가됨)
            deleteImageIds: []
        };

    } catch (error) {
        console.error("데이터 로드 실패", error);
    }
};

// 페이지 로드 시 수정 모드인지 확인
onMounted(async () => {
    if (route.params.id) {
        isEditMode.value = true;
        productId.value = route.params.id;
        await loadProductData(productId.value);
    }
});

</script>