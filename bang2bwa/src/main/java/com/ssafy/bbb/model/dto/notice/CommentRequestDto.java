package com.ssafy.bbb.model.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * 댓글 작성 DTO
 */
@Getter @Setter
@NoArgsConstructor
public class CommentRequestDto {
	private @NonNull String content;
	private Long parentId;
	
	private Long commentId;
	private Long postId;
	private Long userId;
	
}
