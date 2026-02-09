import { api } from "@/api";

// 1. 댓글 작성 (POST /posts/{postId}/comments)
export const writeComment = async (postId, content, parentId = null) => {
  try {
    // DTO 구조에 맞춰서 content만 보내면, Controller에서 User를 주입해줍니다.
    const response = await api.post(`/posts/${postId}/comments`, { content, parentId });
    return response.data;
  } catch (error) {
    console.error("댓글 작성 실패", error);
    return null;
  }
};

// 2. 댓글 목록 조회 (GET /posts/{postId}/comments)
export const getCommentList = async (postId) => {
  try {
    const response = await api.get(`/posts/${postId}/comments`);
    // ApiResponse.data에 List<CommentResponseDto>가 들어있습니다.
    return response.data;
  } catch (error) {
    console.error("댓글 조회 실패", error);
    return { data: [] }; // 에러 시 빈 배열 반환 구조 맞춤
  }
};

// 3. 댓글 삭제 (DELETE /comments/{commentId})
export const removeComment = async (commentId) => {
  try {
    const response = await api.delete(`/comments/${commentId}`);
    return response.data;
  } catch (error) {
    console.error("댓글 삭제 실패", error);
    return null;
  }
};

// 댓글 수정 (PATCH)
export const modifyComment = async (commentId, content) => {
    try {
        const response = await api.patch(`/comments/${commentId}`, { content });
        return response.data;
    } catch (error) {
        console.error("댓글 수정 실패", error);
        return null;
    }
};
