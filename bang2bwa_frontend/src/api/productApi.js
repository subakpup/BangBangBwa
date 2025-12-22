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