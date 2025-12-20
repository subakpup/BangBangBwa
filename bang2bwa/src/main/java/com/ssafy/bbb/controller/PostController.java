package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.notice.PostDetailDto;
import com.ssafy.bbb.model.dto.notice.PostListDto;
import com.ssafy.bbb.model.dto.notice.PostRequestDto;
import com.ssafy.bbb.model.dto.notice.PostSearchDto;
import com.ssafy.bbb.model.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	
	@PostMapping
	public ApiResponse<Long> writePost(@RequestBody PostRequestDto request
									 , @AuthenticationPrincipal CustomUserDetails user) {
		request.setUserId(user.getUserId());
		Long postId = postService.writePost(request);
		
		return ApiResponse.success(postId, "게시글이 성공적으로 등록되었습니다.");
	}
	
	@GetMapping
	public ApiResponse<List<PostListDto>> getPostList(@ModelAttribute PostSearchDto search
													, @PageableDefault(size = 10) Pageable pageable) {
		
		List<PostListDto> result = postService.getPostList(search, pageable);
		
		return ApiResponse.success(result, "게시글 조회에 성공하였습니다.");
	}
	
	@GetMapping("/{postId}")
	public ApiResponse<PostDetailDto> getPostDetail(@PathVariable Long postId) {
		PostDetailDto result = postService.getPostDetail(postId);
		
		return ApiResponse.success(result);
	}
	
	@PatchMapping("/{postId}")
	public ApiResponse<Void> modifyPost(@PathVariable Long postId
									  , @RequestBody PostRequestDto request
									  , @AuthenticationPrincipal CustomUserDetails user) {
		
		request.setPostId(postId);
		request.setUserId(user.getUserId());
		
		postService.modifyPost(request);
		
		return ApiResponse.successWithNoContent("게시글이 성공적으로 수정되었습니다.");
	}
	
	@DeleteMapping("/{postId}")
	public ApiResponse<Void> removePost(@PathVariable Long postId
									  , @AuthenticationPrincipal CustomUserDetails user) {
		postService.removePost(postId, user.getUserId());
		
		return ApiResponse.successWithNoContent("게시글이 성공적으로 삭제되었습니다.");
	}
}
