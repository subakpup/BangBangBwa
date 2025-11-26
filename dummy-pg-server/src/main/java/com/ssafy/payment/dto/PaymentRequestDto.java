package com.ssafy.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestDto {
	private String paymentKey; // PG사 결제 고유 키
	private String orderId; // 주문 ID
	private Long amount; // 결제 금액
}
