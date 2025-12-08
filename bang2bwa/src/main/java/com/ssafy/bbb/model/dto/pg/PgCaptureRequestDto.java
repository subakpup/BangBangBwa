package com.ssafy.bbb.model.dto.pg;

import lombok.Builder;
import lombok.Getter;

// 부분 결제 요청 DTO
@Getter
@Builder
public class PgCaptureRequestDto {
	private String paymentKey;
	private Long captureAmount;
}
