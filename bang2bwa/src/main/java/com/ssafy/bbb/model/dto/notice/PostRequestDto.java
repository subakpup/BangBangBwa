package com.ssafy.bbb.model.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 게시글 생성/수정 DTO
 */
@Getter
@NoArgsConstructor
public class PostRequestDto {
	private @NonNull String title;
	private @NonNull String content;
	private @NonNull String sidoNm;
	private @NonNull String sggNm;
	
	private Long postId;
	private Long userId;
	
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
