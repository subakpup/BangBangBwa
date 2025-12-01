package com.ssafy.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthDto {
	private String orderId;
	private Long amount;
	private String type;

	@Builder
	public PaymentAuthDto(String orderId, Long amount, String type) {
		this.orderId = orderId;
		this.amount = amount;
		this.type = type;
	}
}
