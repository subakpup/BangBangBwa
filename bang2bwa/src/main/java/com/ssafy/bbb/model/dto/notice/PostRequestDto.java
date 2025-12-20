package com.ssafy.bbb.model.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * 게시글 생성/수정 DTO
 */
@Getter @Setter
@NoArgsConstructor
public class PostRequestDto {
	private @NonNull String title;
	private @NonNull String content;
	private @NonNull String sidoNm;
	private @NonNull String sggNm;
	
	private Long postId;
	private Long userId;
	
}
