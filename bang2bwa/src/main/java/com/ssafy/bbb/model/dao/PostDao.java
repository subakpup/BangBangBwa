package com.ssafy.bbb.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssafy.bbb.model.dto.notice.PostDetailDto;
import com.ssafy.bbb.model.dto.notice.PostListDto;
import com.ssafy.bbb.model.dto.notice.PostRequestDto;
import com.ssafy.bbb.model.dto.notice.PostSearchDto;

public interface PostDao {
	// 게시글 작성
	Long insert(PostRequestDto request);

	// 게시글 목록 조회
	List<PostListDto> postList(@Param("search") PostSearchDto search
							 , @Param("limit") int limit
							 , @Param("offset") long offset);

	// 게시글 상세 조회
	PostDetailDto postDetail(Long postId);

	// 게시글 수정
	int update(PostRequestDto request);

	// 게시글 삭제
	int delete(Long postId);
	
	// 조회수 증가
	void updateViewCnt(Long postId);
}
