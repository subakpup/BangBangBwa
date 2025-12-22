package com.ssafy.bbb.model.dto;

import java.util.List;

import com.ssafy.bbb.model.enums.TradeType;

import lombok.Data;

@Data
public class AiSearchDto {
	
	@Data
	public static class Budget {
		private Integer min; // 최소
		private Integer max; // 최대
	}
	
	private TradeType tradeType;	// 거래 유형(월세, 전세, 매매)
	
	// 월세, 전세
	private Budget deposit;	// 보증금
	private Budget monthly;	// 월세
	
	// 매매
	private Budget budget;	// 총 예산
	
	private String location; // 희망 지역
	private List<String> options; // 선호 옵션
}
