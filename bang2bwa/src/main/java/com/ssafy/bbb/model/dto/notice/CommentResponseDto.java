package com.ssafy.bbb.model.dto.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * 댓글 조회 DTO
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
	private @NonNull Long commentId;
	private @NonNull String content;
	
	private LocalDateTime createdAt;
	private LocalDateTime lastModifiedAt;
	private Long postId;
	private Long userId;
	private Long parentId;
	
	private String writerName;
	
	// 대댓글 리스트
	@Builder.Default
	private List<CommentResponseDto> children = new ArrayList<>();
}
