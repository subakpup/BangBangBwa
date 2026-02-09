package com.ssafy.bbb.model.dto.notice;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 게시글 목록 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListDto {
	private @NonNull Long postId;
	private @NonNull String name;
	private @NonNull String title;
	private @NonNull String sidoNm;
	private @NonNull String sggNm;
	
	private Integer viewCnt;
	private LocalDateTime createdAt;
}
