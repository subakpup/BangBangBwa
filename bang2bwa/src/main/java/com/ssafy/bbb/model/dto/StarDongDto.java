package com.ssafy.bbb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarDongDto {
	private String sggNm; // 시군구 (예: 서울특별시 종로구)
	private String umdNm; // 읍면동 (예: 사직동)
}
