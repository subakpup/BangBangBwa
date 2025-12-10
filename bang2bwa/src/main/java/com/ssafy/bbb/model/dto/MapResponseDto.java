package com.ssafy.bbb.model.dto;

import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.Builder;
import lombok.Getter;

/**
 * 마커 정보를 담을 DTO
 */
@Getter
@Builder
public class MapResponseDto {
	private Long productId; // 매물 ID
	private HouseType houseType; // 매물 타입
	private TradeType tradeType; // 거래 타입
	private Long dealAmount; // 매매가격
	private Long deposit; // 보증금
	private Integer monthlyRent; // 월세
	private Double latitude; // 위도
	private Double longitude; // 경도
}
