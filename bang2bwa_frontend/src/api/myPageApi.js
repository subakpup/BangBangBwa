import { api } from "@/api/index";

/**
 * 내 정보 조회
 * GET /my-page
 */
export const getUserInfo = async () => {
    try {
        const response = await api.get('/my-page');
        return response.data; // ApiResponse<UserInfoDto>
    } catch (error) {
        return error;
    }
};

/**
 * 회원 정보 수정 (닉네임, 전화번호 등)
 * PATCH /my-page
 * Body: UserUpdateDto
 */
export const updateUserInfo = async (data) => {
    try {
        const response = await api.patch('/my-page', data);
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 비밀번호 변경
 * PATCH /my-page/change-password
 * Body: PasswordUpdateDto
 */
export const updatePassword = async (data) => {
    try {
        const response = await api.patch('/my-page/change-password', data);
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 회원 탈퇴
 * DELETE /my-page
 */
export const withdrawUser = async () => {
    try {
        // 인터셉터에서 Authorization 헤더를 자동 주입하므로 별도 처리 불필요
        const response = await api.delete('/my-page');
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 내 관심 매물(혹은 내 매물) 조회
 * GET /my-page/products
 */
export const getMyProducts = async () => {
    try {
        const response = await api.get('/my-page/products');
        return response.data; // ApiResponse<List<MyProductDto>>
    } catch (error) {
        return error;
    }
};