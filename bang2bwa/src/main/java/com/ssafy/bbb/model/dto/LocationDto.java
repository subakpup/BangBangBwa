package com.ssafy.bbb.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
	@Schema(description = "사용자의 현재 위치(위도)", example = "37.5642135")
	private Double latitude; // 위도
	@Schema(description = "사용자의 현재 위치(경도)", example = "127.0016985")
	private Double longitude; // 경도
}
