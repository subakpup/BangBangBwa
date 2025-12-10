package com.ssafy.bbb.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RejectReasonDto {
	@Schema(description = "예약 거절 사유", example = "해당 일정엔 예약이 어렵습니다.")
	String rejectReason;
}
