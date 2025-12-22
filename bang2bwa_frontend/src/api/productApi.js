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