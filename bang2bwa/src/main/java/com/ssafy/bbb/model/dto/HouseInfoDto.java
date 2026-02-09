package com.ssafy.bbb.model.dto;

import com.ssafy.bbb.model.enums.HouseType;

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
public class HouseInfoDto {
	private @NonNull Long houseInfoId; // 매물 아이디(PK)
	private @NonNull HouseType houseType; // 매물 종류
	
	private String sggNm; // 시군구
	private String umdNm; // 법정동
	private String jibun; // 지번
	
	private Integer buildYear; // 건축년도
	private String buildName; // 매물 이름
	
	private Double plottageAr; // 대지면적
	private Double totalFloorAr; // 연면적
	private Double landAr; // 대지권 면적
}
