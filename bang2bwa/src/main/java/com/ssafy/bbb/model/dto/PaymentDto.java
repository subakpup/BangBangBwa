package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;

import com.ssafy.bbb.model.enums.PaymentStatus;
import com.ssafy.bbb.model.enums.PaymentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
	private Long paymentId;
	private String pgPaymentKey; // pg사 결제 키
	private String orderId; // 메인서버의 주문 번호
	private Long amount;
	private PaymentStatus status;
	private PaymentType paymentType;
	private LocalDateTime approvedAt;

	private Long reservationId;
	private Long userId;
}
