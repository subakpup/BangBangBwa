package com.ssafy.bbb.model.dto.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 가승인 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgAuthRequestDto {
	private String orderId; // 주문 번호
	private Long amount; // 금액
	private String type; // 결제 타입(DEPOSIT; 부분 결제만 사용)
}
