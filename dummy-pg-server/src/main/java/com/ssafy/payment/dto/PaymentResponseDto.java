package com.ssafy.payment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {
	private String resultCode; // "SUCCESS", "FAIL"
	private String message; // 성공 메세지 또는 실패 사유
	private Object data; // 넘겨줄 데이터(영수증 역할)

	@Getter
	@Builder
	public static class paymentReceipt {
		private String paymentKey;
		private String orderId;
		private LocalDateTime approvedAt; // 승인 시간
		private String method; // 결제 수단 (CARD, CASH(통장 입금 등))
	}

	public static PaymentResponseDto success(String message, Object data) {
		return PaymentResponseDto.builder().resultCode("SUCCESS").message(message).data(data).build();
	}

	public static PaymentResponseDto fail(String message) {
		return PaymentResponseDto.builder().resultCode("FAIL").message(message).build();
	}
}
