package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 프론트에서 사용자가 "직접 선택"하는 예약 데이터
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {
	@Schema(description = "예약할 매물의 ID", example = "14")
	private Long productId;
	@Schema(description = "방문 예약 일시", example = "2025-12-26T14:00:00")
	private LocalDateTime visitDate;
	@Schema(description = "중개업자에게 전달할 메세지 (optional)", example = "오후 2시에 방문하겠습니다.")
	private String message;
	@Schema(description = "사용자가 결제한 계좌")
	private Long bankId;
}
