import { api } from '@/api/index';

/**
 * 매물 검색 API
 * @param {Object} params - 검색 조건들이 담긴 객체
 * @returns - 결과 데이터
 */
export const searchProducts = async (params) => {
    try {
        const response = await api.post('/products/search', params);
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 매물 상세 조회
 * GET /products/{productId}
 */
export const getProductDetail = async (productId) => {
  try {
    const response = await api.get(`/products/${productId}`);
    return response.data;
  } catch (error) {
    return error.response || error;
  }
};

// [중개인] 내가 등록한 전체 매물 조회
export const getMyProductList = async () => {
    try {
        const response = await api.get('/products');
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

export const deleteProduct = async (productId) => {
    try {
        const response = await api.delete(`/products/${productId}`);
        return response.data;
    } catch (error) {
        return error.response || error;
 * AI 맞춤 추천 API
 * @param {Object} params - AI 분석 요청 조건
 * @returns - AI 분석 결과 리스트
 */
export const recommendProducts = async (params) => {
    try {
        // 엔드포인트: /api/ai/recommend
        const response = await api.post('/api/ai/recommend', params);
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * 매물 생성 API
 * @param {ProductDto} productDto 
 * @param {List<MultipartFile>} images 
 * @returns - 응답 결과
 */
export const createProduct = async (productDto, images) => {
    try {
        const formData = new FormData();

        // 1. DTO 객체를 JSON 문자열로 변환 후 Blob으로 감싸서 추가
        const jsonBlob = new Blob([JSON.stringify(productDto)], { type: "application/json" });
        formData.append("product", jsonBlob);

        // 2. 이미지 파일 리스트 추가
        if (images && images.length > 0) {
            images.forEach((image) => {
                formData.append("images", image);
            });
        }

        // 3. Content-Type을 multipart/form-data로 덮어씌워 요청
        const response = await api.post('/products', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });

        return response;
    } catch (error) {
        return error;
    }
};

/**
 * 매물 수정 API
 * @param {Long} productId 
 * @param {ProductDto} productDto 
 * @param {List<MultipartFile>} images 
 * @returns - 응답 결과
 */
export const updateProduct = async (productId, productDto, images) => {
    try {
        const formData = new FormData();

        const jsonBlob = new Blob([JSON.stringify(productDto)], { type: "application/json" });
        formData.append("product", jsonBlob);

        if (images && images.length > 0) {
            images.forEach((image) => {
                formData.append("images", image)
            });
        }

        const response = await api.put(`/products/${productId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });

        return response;
    } catch (error) {
        return error;
    }
};

/**
 * 매물 단건 조회 API
 * @param {Long} productId 
 * @returns 
 */
export const findById = async (productId) => {
    try {
        const response = await api.get(`/products/${productId}`);
        return response.data.data;
    } catch (error) {
        return error;
    }
};