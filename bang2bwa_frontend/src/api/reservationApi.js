import { api } from '@/api/index';

/**
 * 예약 요청 (결제 완료 후 호출됨)
 * POST /reservations
 * Body: { propertyId, reservationTime, message, ... }
 */
export const requestReservation = async (data) => {
    try {
        const response = await api.post('/reservations', data);
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

/**
 * 만남 성사 (위치 인증 포함)
 * POST /reservations/{reservationId}/confirm
 * Body: { latitude, longitude }
 */
export const confirmReservation = async (reservationId, location) => {
    try {
        const response = await api.post(`/reservations/${reservationId}/confirm`, location);
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

/**
 * 노쇼 신고 (위치 인증 포함)
 * POST /reservations/{reservationId}/noshow
 * Body: { latitude, longitude }
 */
export const reportNoShow = async (reservationId, location) => {
    try {
        const response = await api.post(`/reservations/${reservationId}/noshow`, location);
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

/**
 * 예약 취소
 * POST /reservations/{reservationId}/cancel
 */
export const cancelReservation = async (reservationId) => {
    try {
        const response = await api.post(`/reservations/${reservationId}/cancel`);
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

/**
 * [중개인] 예약 승인 및 결제
 * POST /reservations/{reservationId}/accept
 * Body: { reservationId, bankId }
 */
export const acceptReservation = async (data) => {
    try {
        // Controller URL: /reservations/{reservationId}/accept
        // DTO: { reservationId, bankId }
        const response = await api.post(`/reservations/${data.reservationId}/accept`, {
            reservationId: data.reservationId,
            bankId: data.bankId
        });
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};


/**
 * 결제 가능 은행(카드사) 목록 조회
 * GET /api/banks
 */
export const getBankList = async () => {
    try {
        const response = await api.get('/api/banks');
        return response.data; // ApiResponse<List<VirtualBankDto>>
    } catch (error) {
        return error.response || error;
    }
};