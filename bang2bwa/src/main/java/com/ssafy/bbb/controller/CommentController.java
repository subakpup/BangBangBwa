package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.notice.CommentRequestDto;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;
import com.ssafy.bbb.model.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	
	@PostMapping("/posts/{postId}/comments")
	public ApiResponse<Void> writeComment(@PathVariable Long postId
										, @RequestBody CommentRequestDto request
										, @AuthenticationPrincipal CustomUserDetails user) {
		request.setPostId(postId);
		request.setUserId(user.getUserId());
		
		commentService.writeComment(request);
		
		return ApiResponse.successWithNoContent("댓글이 성공적으로 등록되었습니다.");
	}
	
	@GetMapping("/posts/{postId}/comments")
	public ApiResponse<List<CommentResponseDto>> getCommentList(@PathVariable Long postId) {
		List<CommentResponseDto> result = commentService.getCommentList(postId);
		return ApiResponse.success(result, "댓글이 성공적으로 조회되었습니다.");
	}
	
	@PatchMapping("/comments/{commentId}")
	public ApiResponse<Void> modifyComment(@PathVariable Long commentId
										 , @RequestBody CommentRequestDto request
										 , @AuthenticationPrincipal CustomUserDetails user) {
		request.setUserId(user.getUserId());
		request.setCommentId(commentId);
		commentService.modifyComment(request);
		
		return ApiResponse.successWithNoContent("댓글이 성공적으로 수정되었습니다.");
	}
	
	@DeleteMapping("/comments/{commentId}")
	public ApiResponse<Void> removeComment(@PathVariable Long commentId
										 , @AuthenticationPrincipal CustomUserDetails user) {
		commentService.removeComment(commentId, user.getUserId());
		
		return ApiResponse.successWithNoContent("댓글이 삭제되었습니다.");
	}
}
