package com.ssafy.bbb.model.service;

import java.util.List;

import com.ssafy.bbb.model.dto.notice.CommentRequestDto;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;

public interface CommentService {
	// 댓글 작성
	void writeComment(CommentRequestDto request);
	
	// 댓글 조회
	List<CommentResponseDto> getCommentList(Long postId);
	
	// 댓글 수정
	void modifyComment(CommentRequestDto request);
	
	// 댓글 삭제
	void removeComment(Long commentId, Long userId);
	
}
