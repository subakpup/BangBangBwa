package com.ssafy.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCaptureDto {
	private String paymentKey;
	private Long captureAmount;
}
