package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.PostDao;
import com.ssafy.bbb.model.dto.notice.CommentResponseDto;
import com.ssafy.bbb.model.dto.notice.PostDetailDto;
import com.ssafy.bbb.model.dto.notice.PostListDto;
import com.ssafy.bbb.model.dto.notice.PostRequestDto;
import com.ssafy.bbb.model.dto.notice.PostSearchDto;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostDao postDao;

    @Mock
    private CommentService commentService;

    @Test
    @DisplayName("게시글 작성 성공")
    void writePost_Success() {
        // given
        PostRequestDto request = new PostRequestDto();
        request.setTitle("제목");
        request.setPostId(10L); // MyBatis selectKey 시뮬레이션
        request.setSidoNm("서울특별시");
        request.setSggNm("강남구");

        // when
        Long resultId = postService.writePost(request);

        // then
        verify(postDao).insert(request);
        assertThat(resultId).isEqualTo(10L);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 성공")
    void getPostList_Success() {
        // given
        PostSearchDto search = new PostSearchDto();
        Pageable pageable = PageRequest.of(0, 10);
        List<PostListDto> mockList = List.of(new PostListDto(), new PostListDto());

        given(postDao.postList(any(), anyInt(), anyLong())).willReturn(mockList);

        // when
        List<PostListDto> result = (List<PostListDto>) postService.getPostList(search, pageable);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 실패 (결과 없음)")
    void getPostList_Fail_NotFound() {
        // given
        PostSearchDto search = new PostSearchDto();
        Pageable pageable = PageRequest.of(0, 10);
        
        // 빈 리스트 반환 설정
        given(postDao.postList(any(), anyInt(), anyLong())).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> postService.getPostList(search, pageable))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 성공 (조회수 증가 및 댓글 조회 호출 확인)")
    void getPostDetail_Success() {
        // given
        Long postId = 1L;
        
        PostDetailDto mockPost = PostDetailDto.builder()
                .postId(postId)
                .userId(100L)
                .name("이름")
                .title("상세 제목")
                .content("본문")
                .sidoNm("서울특별시")
                .sggNm("강남구")
                .build();

        given(postDao.postDetail(postId)).willReturn(mockPost);
        given(commentService.getCommentList(postId)).willReturn(List.of(new CommentResponseDto()));

        // when
        PostDetailDto result = postService.getPostDetail(postId);

        // then
        verify(postDao).updateViewCnt(postId); // 조회수 증가 호출 검증
        assertThat(result).isNotNull();
        assertThat(result.getComments()).hasSize(1); // 댓글 세팅 검증
    }

    @Test
    @DisplayName("게시글 상세 조회 - 실패 (존재하지 않는 글)")
    void getPostDetail_Fail_NotFound() {
        // given
        Long postId = 999L;
        given(postDao.postDetail(postId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> postService.getPostDetail(postId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void modifyPost_Success() {
        // given
        Long postId = 1L;
        Long userId = 100L;
        
        PostRequestDto request = new PostRequestDto();
        request.setTitle("제목");
        request.setContent("본문");
        request.setSidoNm("서울특별시");
        request.setSggNm("강남구");
        request.setPostId(postId);
        request.setUserId(userId);

        PostDetailDto existingPost = PostDetailDto.builder()
        		.postId(postId)
                .userId(100L)
                .name("이름")
                .title("상세 제목")
                .content("본문")
                .sidoNm("서울특별시")
                .sggNm("강남구")
                .build();

        given(postDao.postDetail(postId)).willReturn(existingPost);

        // when
        postService.modifyPost(request);

        // then
        verify(postDao).update(request);
    }

    @Test
    @DisplayName("게시글 수정 - 실패 (권한 없음)")
    void modifyPost_Fail_Forbidden() {
        // given
        Long postId = 1L;
        Long ownerId = 100L;
        Long intruderId = 200L; // 다른 사용자

        PostRequestDto request = new PostRequestDto();
        request.setTitle("제목");
        request.setContent("본문");
        request.setSidoNm("서울특별시");
        request.setSggNm("강남구");
        request.setPostId(postId);
        request.setUserId(intruderId);

        PostDetailDto existingPost = PostDetailDto.builder()
        		.postId(postId)
                .userId(ownerId)
                .name("이름")
                .title("상세 제목")
                .content("본문")
                .sidoNm("서울특별시")
                .sggNm("강남구")
                .build();

        given(postDao.postDetail(postId)).willReturn(existingPost);

        // when & then
        assertThatThrownBy(() -> postService.modifyPost(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_USER);
        
        verify(postDao, never()).update(any()); // 업데이트가 실행되지 않았는지 확인
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void removePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 100L;
        
        PostDetailDto existingPost = PostDetailDto.builder()
        		.postId(postId)
                .userId(100L)
                .name("이름")
                .title("상세 제목")
                .content("본문")
                .sidoNm("서울특별시")
                .sggNm("강남구")
                .build();

        given(postDao.postDetail(postId)).willReturn(existingPost);

        // when
        postService.removePost(postId, userId);

        // then
        verify(postDao).delete(postId);
    }
}