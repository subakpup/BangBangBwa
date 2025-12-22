package com.ssafy.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCaptureDto {
	private String paymentKey;
	private Long captureAmount;
}
