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
 * [추가] 노쇼 신고 이의 제기 (방어)
 * POST /reservations/{reservationId}/defend
 * Body: { latitude, longitude }
 */
export const defendNoShow = async (reservationId, location) => {
    try {
        const response = await api.post(`/reservations/${reservationId}/defend`, location);
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
 * POST /reservations/accept
 * Body: { reservationId, bankId }
 */
export const acceptReservation = async (reservationId, bankId) => {
    try {
        const response = await api.post('/reservations/accept', {
            reservationId: Number(reservationId), // 숫자로 변환하여 전송 (안전장치)
            bankId: Number(bankId)
        });
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

/**
 * [추가] 예약 상세 정보 조회 (화면 표시용)
 * GET /reservations/{reservationId}
 */
export const getReservationDetail = async (reservationId) => {
    try {
        const response = await api.get(`/reservations/${reservationId}`);
        return response.data;
    } catch (error) {
        return error.response || error;
    }
}

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

// [중개인] 예약이 잡힌 내 매물 조회
export const getMyReservationProducts = async () => {
    try {
        const response = await api.get('/reservations/products');
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};

// [중개인] 예약 거절 (바로 거절 처리)
export const rejectReservation = async (reservationId, reason) => {
    try {
        const response = await api.post(`/reservations/${reservationId}/reject`, {
            reason: reason 
        });
        return response.data;
    } catch (error) {
        return error.response || error;
    }
};



export const getMessage = async (reservationId) => {
    try {
        const response = await api.get(`/reservations/${reservationId}/message`);

        return response.data;
    } catch (error) {
        return error.response.data;
    }
}