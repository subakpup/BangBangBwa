package com.ssafy.bbb.model.dto.notice;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 검색 DTO
 */
@Getter @Setter
public class PostSearchDto {
	private String sidoNm;
	private String sggNm;
	private String keyword;
}
