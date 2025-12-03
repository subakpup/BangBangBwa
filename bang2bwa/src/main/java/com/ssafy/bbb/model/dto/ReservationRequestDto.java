package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;

// 프론트에서 사용자가 "직접 선택"하는 예약 데이터
public class ReservationRequestDto {
	private Long productId;
	private LocalDateTime visitDate;
	private String message;
}
