package com.ssafy.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAuthDto {
	private String orderId;
	private Long amount;
	private String type;
	private String accountToken; // 사용자의 계좌 정보
}
