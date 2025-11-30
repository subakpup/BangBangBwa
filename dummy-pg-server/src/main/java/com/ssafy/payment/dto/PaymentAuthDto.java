package com.ssafy.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentAuthDto {
	private String orderId;
	private Long amount;
	private String type;
}
