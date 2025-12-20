package com.ssafy.bbb.model.dto.notice;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 게시글 상세 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto {
	private @NonNull Long postId;
	private @NonNull Long userId;
	private @NonNull String name;
	private @NonNull String title;
	private @NonNull String content;
	private @NonNull String sidoNm;
	private @NonNull String sggNm;
	
	private LocalDateTime createdAt;
	private LocalDateTime lastModifiedAt;
	private Integer viewCnt;
	
	// 댓글 리스트
	private List<CommentResponseDto> comments;
	
	public void setComments(List<CommentResponseDto> comments) {
		this.comments = comments;
	}
}
