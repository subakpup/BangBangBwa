package com.ssafy.bbb.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseDealDto {
	private @NonNull Long houseDealId; // 거래 ID(PK)
	private @NonNull Long houseInfoId; // 매물 ID(FK)
	private @NonNull Long dealAmount; // 거래 가격
	
	private Long monthlyRent; // 월세
	private LocalDate dealDate; // 거래일자
	private String floor; // 층
	private Double excluUseAr; // 전용 면적
}
