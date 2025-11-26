package com.ssafy.payment.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 내부 관리용 ID (PK)

	@Column(nullable = false)
	private String paymentKey; // PG사에서 발급하는 고유 키 (UUID 등)

	@Column(nullable = false)
	private String orderId; // 가맹점(부동산 서버)에서 보낸 주문 번호

	@Column(nullable = false)
	private Long amount; // 결제 금액

	@Column(nullable = false)
	private String status; // 결제 상태 (SUCCESS, FAIL, CANCEL)

	private String failReason; // 실패 시 사유 (잔액부족 등)

	private LocalDateTime createdAt; // 결제 승인 일시

	@Builder // 객체 생성을 쉽게 하기 위해 빌더 패턴 사용
	public Payment(String paymentKey, String orderId, Long amount, String status, String failReason) {
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.amount = amount;
		this.status = status;
		this.failReason = failReason;
		this.createdAt = LocalDateTime.now(); // 생성 시 현재 시간 자동 저장
	}
}