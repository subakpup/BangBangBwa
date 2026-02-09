package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.PostDao;
import com.ssafy.bbb.model.dto.PageResponse;
import com.ssafy.bbb.model.dto.notice.PostDetailDto;
import com.ssafy.bbb.model.dto.notice.PostListDto;
import com.ssafy.bbb.model.dto.notice.PostRequestDto;
import com.ssafy.bbb.model.dto.notice.PostSearchDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final PostDao postDao;
	private final CommentService commentService;
	
	/**
	 * 게시글 작성
	 */
	@Override
	@Transactional
	public Long writePost(PostRequestDto request) {
		postDao.insert(request);
		
		return request.getPostId();
	}

	/**
	 * 게시글 목록 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public PageResponse<PostListDto> getPostList(PostSearchDto search, Pageable pageable) {
		int limit = pageable.getPageSize();
		long offset = pageable.getOffset();
		
		List<PostListDto> list = postDao.postList(search, limit, offset);
		int totalCount = postDao.postCount(search);
		
		int totalPage = (int)Math.ceil((double)totalCount/limit);
		
		if (list.isEmpty()) {
			throw new CustomException(ErrorCode.POST_NOT_FOUND);
		}
		
		return PageResponse.<PostListDto>builder()
							.content(list)
							.totalPage(totalPage)
							.totalCount(totalCount)
							.currentPage(pageable.getPageNumber() + 1)
							.size(limit)
							.build();
	}

	/**
	 * 게시글 상세 조회
	 */
	@Override
	@Transactional
	public PostDetailDto getPostDetail(Long postId) {
		postDao.updateViewCnt(postId);
		PostDetailDto postDetail = postDao.postDetail(postId);
		
		if (postDetail == null) {
			throw new CustomException(ErrorCode.POST_NOT_FOUND);
		}
		
		postDetail.setComments(commentService.getCommentList(postId));
		return postDetail;
	}

	/**
	 * 게시글 수정
	 */
	@Override
	@Transactional
	public void modifyPost(PostRequestDto request) {
		validatePost(request.getPostId(), request.getUserId());
		
		postDao.update(request);
	}

	/**
	 * 게시글 삭제
	 */
	@Override
	@Transactional
	public void removePost(Long postId, Long userId) {
		validatePost(postId, userId);
		
		postDao.delete(postId);
	}
	
	/**
	 * 게시글 유효성 검증
	 * @param postId 게시물 아이디
	 * @param userId 유저 아이디
	 */
	private void validatePost(Long postId, Long userId) {
		PostDetailDto post = postDao.postDetail(postId);
		
		// 존재하지 않는 경우
		if (post == null) {
			throw new CustomException(ErrorCode.POST_NOT_FOUND);
		}
		
		// 작성자가 아닌 경우 접근 차단
		if (!post.getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
	}

}
