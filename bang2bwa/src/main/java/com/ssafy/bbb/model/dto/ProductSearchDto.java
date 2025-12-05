package com.ssafy.bbb.model.dto;

import lombok.Data;

@Data
public class ProductSearchDto {
	private String keyword; // 검색어
	private String houseType; // 매물 종류
	private String tradeType; // 거래 종류
	private String excluUseAr; // 전용 면적
	private String floor; // 층
}
