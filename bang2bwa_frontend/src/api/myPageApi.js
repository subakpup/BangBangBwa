import { api } from "@/api/index";

/**
 * 내 정보 조회
 * GET /mypage
 */
export const getUserInfo = async () => {
    try {
        const response = await api.get('/mypage');
        return response.data; // ApiResponse<UserInfoDto>
    } catch (error) {
        return error;
    }
};

/**
 * 회원 정보 수정 (닉네임, 전화번호 등)
 * PATCH /mypage
 * Body: UserUpdateDto
 */
export const updateUserInfo = async (data) => {
    try {
        const response = await api.patch('/mypage', data);
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 비밀번호 변경
 * PATCH /mypage/change-password
 * Body: PasswordUpdateDto
 */
export const updatePassword = async (data) => {
    try {
        const response = await api.patch('/mypage/change-password', data);
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 회원 탈퇴
 * DELETE /mypage
 */
export const withdrawUser = async () => {
    try {
        // 인터셉터에서 Authorization 헤더를 자동 주입하므로 별도 처리 불필요
        const response = await api.delete('/mypage');
        return response.data;
    } catch (error) {
        return error;
    }
};

/**
 * 내 매물 조회
 * GET /mypage/products
 */
export const getMyProducts = async () => {
    try {
        const response = await api.get('/mypage/products');
        return response.data; // ApiResponse<List<MyProductDto>>
    } catch (error) {
        return error;
    }
};

/**
 * 내 관심 매물 조회
 * GET /wishlist/products
 */
export const getMyWishProductList = async () => {
    try {
        const response = await api.get('/wishlist/products');
        return response.data;
    } catch (error) {
        return error;
    }
}