package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.CommentDao;
import com.ssafy.bbb.model.dto.notice.CommentRequestDto;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentDao commentDao;

    @Test
    @DisplayName("댓글 목록 조회 - 계층형 구조 변환 테스트")
    void getCommentList_Hierarchical_Check() {
        // given
        Long postId = 1L;
        
        // 테스트 데이터 준비: 부모 1개, 자식 2개
        CommentResponseDto parent = new CommentResponseDto();
        parent.setCommentId(1L);
        parent.setParentId(null);
        parent.setContent("부모 댓글");
        parent.setChildren(new ArrayList<>()); // 초기화 중요

        CommentResponseDto child1 = new CommentResponseDto();
        child1.setCommentId(2L);
        child1.setParentId(1L);
        child1.setContent("대댓글 1");

        CommentResponseDto child2 = new CommentResponseDto();
        child2.setCommentId(3L);
        child2.setParentId(1L);
        child2.setContent("대댓글 2");
        
        List<CommentResponseDto> flatList = List.of(parent, child1, child2);

        given(commentDao.commentList(postId)).willReturn(flatList);

        // when
        List<CommentResponseDto> result = commentService.getCommentList(postId);

        // then
        // 1. 최상위 댓글은 1개여야 함 (parent)
        assertThat(result).hasSize(1);
        
        // 2. 그 최상위 댓글의 자식은 2개여야 함 (child1, child2)
        assertThat(result.get(0).getChildren()).hasSize(2);
        
        // 3. 내용 검증
        assertThat(result.get(0).getChildren().get(0).getContent()).isEqualTo("대댓글 1");
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void modifyComment_Success() {
        // given
        Long commentId = 1L;
        Long userId = 10L;
        
        CommentRequestDto request = new CommentRequestDto();
        request.setCommentId(commentId);
        request.setUserId(userId);
        
        CommentResponseDto existing = new CommentResponseDto();
        existing.setUserId(userId);
        
        given(commentDao.findById(commentId)).willReturn(existing);

        // when
        commentService.modifyComment(request);

        // then
        verify(commentDao).update(request);
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 (작성자 불일치)")
    void removeComment_Fail_Forbidden() {
        // given
        Long commentId = 1L;
        Long ownerId = 10L;
        Long otherId = 20L;
        
        CommentResponseDto existing = new CommentResponseDto();
        existing.setUserId(ownerId);
        
        given(commentDao.findById(commentId)).willReturn(existing);

        // when & then
        assertThatThrownBy(() -> commentService.removeComment(commentId, otherId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_USER);
    }
}