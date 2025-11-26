package com.ssafy.bbb.model.dto;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductDto {
	private @NonNull Long productId; // pk

	private @NonNull String jibun; // 지번
	private @NonNull String houseType; // 매물 종류
	private @NonNull String tradeType; // 거래 종류

	private String name; // 건물 이름
	private Integer buildYear; // 건축 년도
	private Double excluUseAr; // 전용 면적

	private String aptDong; // 동 이름
	private Integer floor; // 층

	private Double landAr; // 대지권 면적
	private Double totalFloorAr; // 연면적
	private Double plottageAr; // 대지면적

	private Long dealAmount; // 매매가격
	private Long deposit; // 보증금
	private Integer monthlyRent; // 월세

	private String sggCd; // 시군구 코드 (fk)
	private String umdNm; // 동코드 (fk)
	private Long agentId; // 공인중개사 (fk)
}
