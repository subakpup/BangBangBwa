package com.ssafy.bbb.model.dao;

import java.util.List;

import com.ssafy.bbb.model.dto.notice.CommentRequestDto;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;

public interface CommentDao {
	// 댓글 작성
	Long insert(CommentRequestDto request);
	
	// 댓글 조회
	List<CommentResponseDto> commentList(Long postId);
	
	// 댓글 수정
	int update(CommentRequestDto request);
	
	// 댓글 삭제
	int delete(Long commentId);
	
	// 댓글 단건 조회
	CommentResponseDto findById(Long commentId);
}
