import { api } from "@/api/index";

/**
 * 게시글 목록 조회
 * GET /posts
 * Params: { sidoNm, sggNm, keyword, page, size }
 */
export const getPostList = async (params) => {
  try {
    const cleanParams = {};
    if(params.sidoNm) cleanParams.sidoNm = params.sidoNm;
    if(params.sggNm) cleanParams.sggNm = params.sggNm;
    if(params.keyword) cleanParams.keyword = params.keyword;

    cleanParams.page = (params.page || 1) - 1; 
    cleanParams.size = params.size || 10;

    const response = await api.get("/posts", { params: cleanParams });
    return response.data; // ApiResponse<List<PostListDto>>
  } catch (error) {
    return error.response || error;
  }
};

/**
 * 게시글 상세 조회
 * GET /posts/{postId}
 */
export const getPostDetail = async (postId) => {
  try {
    const response = await api.get(`/posts/${postId}`);
    return response.data; // ApiResponse<PostDetailDto>
  } catch (error) {
    return error.response || error;
  }
};

/**
 * 게시글 작성
 * POST /posts
 * Body: { title, content, sidoNm, sggNm }
 */
export const writePost = async (postData) => {
  try {
    const response = await api.post("/posts", postData);
    return response.data;
  } catch (error) {
    return error.response || error;
  }
};

/**
 * 게시글 수정
 * PATCH /posts/{postId}
 * Body: { title, content, sidoNm, sggNm }
 */
export const modifyPost = async (postId, postData) => {
  try {
    const response = await api.patch(`/posts/${postId}`, postData);
    return response.data;
  } catch (error) {
    return error.response || error;
  }
};

/**
 * 게시글 삭제
 * DELETE /posts/{postId}
 */
export const removePost = async (postId) => {
  try {
    const response = await api.delete(`/posts/${postId}`);
    return response.data;
  } catch (error) {
    return error.response || error;
  }
};
