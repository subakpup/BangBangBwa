package com.ssafy.bbb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
	private Double latitude; // 위도
	private Double longitude; // 경도
}
