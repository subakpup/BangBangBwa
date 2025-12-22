<template>
    <div class="regist-wrapper custom-scrollbar">
        <div class="regist-card animate-fade-in-up">

            <div class="regist-header">
                <h2 class="regist-title">{{ isEditMode ? '매물 정보 수정' : '새 매물 등록' }}</h2>
                <p class="text-gray-400 text-sm mt-2">매물의 상세 정보를 입력하여 고객에게 어필해보세요.</p>
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
                    <h3 class="section-title">가격 정보 (단위: 만원)</h3>
                    <div class="highlight-box">

                        <div v-if="product.tradeType === 'SALE'" class="form-group">
                            <label class="form-label">매매가 <span class="required-mark">*</span></label>
                            <input type="number" v-model="product.dealAmount" placeholder="예: 35000 (3억 5천)"
                                class="regist-input bg-white" />
                        </div>

                        <div v-if="product.tradeType === 'LEASE'" class="form-group">
                            <label class="form-label">전세 보증금 <span class="required-mark">*</span></label>
                            <input type="number" v-model="product.deposit" placeholder="예: 20000 (2억)"
                                class="regist-input bg-white" />
                        </div>

                        <div v-if="product.tradeType === 'RENT'" class="form-grid">
                            <div class="form-group">
                                <label class="form-label">보증금 <span class="required-mark">*</span></label>
                                <input type="number" v-model="product.deposit" placeholder="예: 1000"
                                    class="regist-input bg-white" />
                            </div>
                            <div class="form-group">
                                <label class="form-label">월세 <span class="required-mark">*</span></label>
                                <input type="number" v-model="product.monthlyRent" placeholder="예: 50"
                                    class="regist-input bg-white" />
                            </div>
                        </div>

                    </div>
                </section>

                <section class="regist-section">
                    <h3 class="section-title">위치 정보</h3>

                    <div class="highlight-box bg-white">
                        <div class="form-grid-3 mb-4">

                            <div class="form-group">
                                <label class="form-label">시 / 도 <span class="required-mark">*</span></label>
                                <select v-model="selectedSidoCode" class="regist-input cursor-pointer">
                                    <option value="" disabled>선택</option>
                                    <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
                                        {{ sido.sidoName }}
                                    </option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">시 / 군 / 구 <span class="required-mark">*</span></label>
                                <select v-model="selectedSggCode" class="regist-input cursor-pointer"
                                    :disabled="!selectedSidoCode">
                                    <option value="" disabled>선택</option>
                                    <option v-for="sgg in sggList" :key="sgg.gugunCode" :value="sgg.gugunCode">
                                        {{ sgg.gugunName }}
                                    </option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label">법정동 (읍/면/동) <span class="required-mark">*</span></label>
                                <input type="text" v-model="selectedDongName" placeholder="예: 역삼동" class="regist-input"
                                    @blur="updateLocationInfo" />
                            </div>
                        </div>

                        <div class="form-grid">
                            <div class="form-group">
                                <label class="form-label">상세 번지 <span class="required-mark">*</span></label>
                                <input type="text" v-model="detailJibun" placeholder="예: 123-45 (상세주소)"
                                    class="regist-input" @blur="updateLocationInfo" />
                            </div>

                            <div class="form-group">
                                <label class="form-label">좌표 (자동생성)</label>
                                <div class="flex gap-2">
                                    <input type="text" :value="product.latitude" readonly
                                        class="regist-input readonly text-xs" placeholder="위도" />
                                    <input type="text" :value="product.longitude" readonly
                                        class="regist-input readonly text-xs" placeholder="경도" />
                                </div>
                            </div>
                        </div>
                        <p class="text-xs text-gray-400 mt-2">* 상세 주소까지 모두 입력하면 좌표가 자동으로 생성됩니다.</p>
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
                        <div v-for="(img, index) in product.images" :key="img.imageId || index"
                            class="preview-item group">
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
import { onMounted, ref, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createProduct, findById, updateProduct } from '@/api/productApi';
import { getSidoList, getSggList } from '@/api/addressApi';
import { typeMap, tradeTypeMap } from '@/utils/productUtil';

const route = useRoute();
const router = useRouter();
const fileInput = ref(null);

// --- 상태 변수 ---
const isEditMode = ref(false);
const productId = ref(null);
const selectedFiles = ref([]);

// 주소 관련 상태
const sidoList = ref([]);
const sggList = ref([]);
const selectedSidoCode = ref("");
const selectedSggCode = ref("");
const selectedDongName = ref("");
const detailJibun = ref("");

// DTO 매핑
const product = ref({
    productId: null,
    agentId: null,
    
    // 주소 정보
    sggNm: '', // "서울특별시 강남구"
    umdNm: '', // "역삼동"
    jibun: '', // "123-45"
    latitude: 0.0, 
    longitude: 0.0,
    
    // 상세 정보
    name: '', aptDong: '', floor: '', buildYear: null,
    excluUseAr: null, landAr: null, totalFloorAr: null, plottageAr: null,
    
    // 기본 정보
    houseType: 'APART', 
    tradeType: 'SALE', 
    status: 'AVAILABLE',
    
    // 가격
    dealAmount: 0, 
    deposit: 0, 
    monthlyRent: 0,
    
    desc: '',
    images: [], 
    deleteImageIds: []
});

// --- 비즈니스 로직 ---

const handleFileUpload = (event) => {
    selectedFiles.value = Array.from(event.target.files);
};

const removeExistingImage = (imageId, index) => {
    product.value.images.splice(index, 1);
    if (imageId) product.value.deleteImageIds.push(imageId);
};

const submitForm = async () => {
    // 1. 필수 값 검증
    if (!product.value.houseType || !product.value.tradeType) {
        alert("매물 종류와 거래 유형은 필수 입력 항목입니다.");
        return;
    }
    
    // 주소 필수값 검증
    if (!product.value.sggNm || !product.value.umdNm || !product.value.jibun) {
        alert("주소 정보를 모두 입력해주세요 (시/도, 시/군/구, 법정동, 상세주소).");
        return;
    }

    // 좌표 생성 확인
    if (!product.value.latitude || !product.value.longitude) {
        alert("좌표 생성을 위해 주소를 다시 확인해주세요.");
        return;
    }

    // 2. 가격 단위 변환 (만원 -> 원)
    const submissionData = { ...product.value };
    
    if (submissionData.dealAmount) submissionData.dealAmount *= 10000;
    if (submissionData.deposit) submissionData.deposit *= 10000;
    if (submissionData.monthlyRent) submissionData.monthlyRent *= 10000;

    try {
        let response;
        if (isEditMode.value) {
            response = await updateProduct(productId.value, submissionData, selectedFiles.value);
        } else {
            response = await createProduct(submissionData, selectedFiles.value);
        }

        if (response && response.message) alert(response.message);

        router.push({ name: 'productManage' });
    } catch (error) {
        const errorMsg = error.response?.data?.message || '처리 중 오류가 발생했습니다.';
        alert(errorMsg);
    }
};

// --- 주소 및 좌표 로직 ---

const fetchSidoList = async () => {
    try {
        sidoList.value = await getSidoList();
    } catch (error) {
        console.error("시도 로드 실패", error);
    }
};

watch(selectedSidoCode, async (newVal) => {
    if (!newVal) return;
    try {
        sggList.value = await getSggList(newVal);
    } catch (error) {
        console.error("구군 로드 실패", error);
    }
});

watch([selectedSggCode, selectedDongName, detailJibun], () => {
    if (selectedSidoCode.value && selectedSggCode.value) {
        updateLocationInfo();
    }
});

const updateLocationInfo = () => {
    const sido = sidoList.value.find(item => item.sidoCode === selectedSidoCode.value);
    const sgg = sggList.value.find(item => item.gugunCode === selectedSggCode.value);

    if (!sido || !sgg) return;

    const sidoName = sido.sidoName;
    const sggName = sgg.gugunName;
    const dongName = selectedDongName.value;
    const detail = detailJibun.value;

    // DTO 저장 포맷: sggNm = "시도 + 공백 + 구군"
    product.value.sggNm = `${sidoName} ${sggName}`;
    product.value.umdNm = dongName;
    product.value.jibun = detail;

    // 좌표 변환용 전체 주소
    const fullAddressForGeo = `${sidoName} ${sggName} ${dongName} ${detail}`.trim();

    if (window.kakao && window.kakao.maps) {
        const geocoder = new kakao.maps.services.Geocoder();
        geocoder.addressSearch(fullAddressForGeo, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                product.value.latitude = parseFloat(result[0].y);
                product.value.longitude = parseFloat(result[0].x);
            }
        });
    }
};

// --- 수정 데이터 로드 ---
const loadProductData = async (id) => {
    try {
        const data = await findById(id);

        product.value = {
            ...product.value,
            ...data,
            images: data.images || [],
            deleteImageIds: []
        };

        // 가격 단위 변환 (원 -> 만원)
        if (product.value.dealAmount) product.value.dealAmount /= 10000;
        if (product.value.deposit) product.value.deposit /= 10000;
        if (product.value.monthlyRent) product.value.monthlyRent /= 10000;

        // 주소 UI 복구
        if (data.sggNm) {
            const sggNmParts = data.sggNm.split(" "); 
            
            if (sggNmParts.length >= 2) {
                const targetSidoName = sggNmParts[0];
                const targetSggName = sggNmParts[1];

                const foundSido = sidoList.value.find(s => s.sidoName === targetSidoName);
                if (foundSido) {
                    selectedSidoCode.value = foundSido.sidoCode;
                    await nextTick();

                    const loadedSggList = await getSggList(foundSido.sidoCode);
                    sggList.value = loadedSggList;

                    const foundSgg = loadedSggList.find(s => s.gugunName === targetSggName);
                    if (foundSgg) {
                        selectedSggCode.value = foundSgg.gugunCode;
                    }
                }
            }
        }
        
        if (data.umdNm) selectedDongName.value = data.umdNm;
        if (data.jibun) detailJibun.value = data.jibun;

    } catch (error) {
        console.error("데이터 로드 실패", error);
        alert("데이터를 불러올 수 없습니다.");
        router.push({ name: 'ProductManage' });
    }
};

onMounted(async () => {
    await fetchSidoList();

    if (route.params.id) {
        isEditMode.value = true;
        productId.value = route.params.id;
        await loadProductData(productId.value);
    }
});
</script>