package com.ssafy.bbb.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptRequestDto {
	private Long reservationId;
	private Long bankId;
}
