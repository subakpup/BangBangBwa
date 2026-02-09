package com.ssafy.bbb.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.CommentDao;
import com.ssafy.bbb.model.dto.notice.CommentRequestDto;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
	
	private final CommentDao commentDao;
	
	/**
	 * 댓글 작성
	 */
	@Override
	@Transactional
	public void writeComment(CommentRequestDto request) {
		commentDao.insert(request);
	}

	/**
	 * 댓글 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CommentResponseDto> getCommentList(Long postId) {
		List<CommentResponseDto> list = commentDao.commentList(postId);
		return convertToHierarchical(list);
	}

	/**
	 * 댓글 리스트를 계층형 구조로 변환
	 * @param list Flat한 댓글 리스트
	 * @return 계층형 구조
	 */
	private List<CommentResponseDto> convertToHierarchical(List<CommentResponseDto> list) {
		List<CommentResponseDto> rootComments = new ArrayList<>();
		Map<Long, CommentResponseDto> map = new HashMap<>();
		
		// 모든 댓글을 Map에 저장
		for (CommentResponseDto dto : list) {
			map.put(dto.getCommentId(), dto);
		}
		
		// 부모-자식 연결
		for (CommentResponseDto dto : list) {
			Long parentId = dto.getParentId();
			
			if (parentId == null) {
				// 부모가 없으면 최상위 댓글
				rootComments.add(dto);
			} else {
				// 부모가 있으면 부모를 찾아서 자식 리스트에 추가
				CommentResponseDto parent = map.get(parentId);
				if (parent != null) {
					parent.getChildren().add(dto);
				} else {
					rootComments.add(dto);
				}
			}
		}
		
		return rootComments;
	}

	/**
	 * 댓글 수정
	 */
	@Override
	@Transactional
	public void modifyComment(CommentRequestDto request) {
		validateComment(request.getCommentId(), request.getUserId());
		
		commentDao.update(request);
	}

	/**
	 * 댓글 삭제
	 */
	@Override
	@Transactional
	public void removeComment(Long commentId, Long userId) {
		validateComment(commentId, userId);
		
		commentDao.delete(commentId);
	}
	
	/**
	 * 댓글 유효성 검증
	 * @param commentId 댓글 아이디
	 * @param userId 유저 아이디
	 */
	private void validateComment(Long commentId, Long userId) {
		CommentResponseDto comment = commentDao.findById(commentId);
		
		// 존재하지 않는 경우
		if (comment == null) {
			throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
		}
		
		// 작성자가 아닌 경우 접근 차단
		if (!comment.getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
	}

}
