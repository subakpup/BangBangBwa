package com.ssafy.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCaptureDto {
	private String paymentKey;
	private Long captureAmount;

	@Builder
	public PaymentCaptureDto(String paymentKey, Long captureAmount) {
		this.paymentKey = paymentKey;
		this.captureAmount = captureAmount;
	}
}
