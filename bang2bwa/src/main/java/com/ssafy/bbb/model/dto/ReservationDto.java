package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;

import com.ssafy.bbb.model.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
	private Long reservationId; // 예약 고유 pk (auto generated)
	private Long productId; // 예약된 매물의 id
	private Long userId; // 예약자 id
	private Long agentId; // 중개인 id
	private String userPaymentKey; // 예약자 결제 키
	private String agentPaymentKey; // 중개인 결제 키
	private LocalDateTime visitDate; // 예약일
	private String message; // 예약 메세지
	private ReservationStatus status; // 예약 상태
	private Long depositAmount; // 예약금
}
