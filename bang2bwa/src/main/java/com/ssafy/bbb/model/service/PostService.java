package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ssafy.bbb.model.dto.notice.PostDetailDto;
import com.ssafy.bbb.model.dto.notice.PostListDto;
import com.ssafy.bbb.model.dto.notice.PostRequestDto;
import com.ssafy.bbb.model.dto.notice.PostSearchDto;

public interface PostService {
	// 게시글 작성
	public Long writePost(PostRequestDto request);
	
	// 게시글 목록 조회
	public List<PostListDto> getPostList(PostSearchDto search, Pageable pageable);
	
	// 게시글 상세 조회
	public PostDetailDto getPostDetail(Long postId);
	
	// 게시글 수정
	public void modifyPost(PostRequestDto request);
	
	// 게시글 삭제
	public void removePost(Long postId, Long userId);
}
