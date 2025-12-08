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

	// PG사에서 발급하는 고유 키 (UUID 등)
	// 가맹점 서버는 이 key 값을 가지고 주문을 판별함.(외부 노출용 ID)
	@Column(unique = true, nullable = false)
	private String paymentKey; // 거래의 식별자

	@Column(unique = true, nullable = false)
	private String orderId; // 가맹점에서 보낸 주문 번호

	// 가승인 금액과 실제 수수료를 분리해서 저장함.
	@Column(nullable = false)
	private Long originalAmount; // 가승인 금액(보증금 10000원 예상)
	private Long finalAmount; // 실제 수수료(수수료 1000원 예상)

	// DEPOSIT의 경우에만 (가승인 -> 결제) 동작
	@Column(nullable = false)
	private String type; // 결제의 종류("NORMAL": 일반결제, "DEPOSIT": 예약결제)

	// "READY": 대기, "AUTHORIZED": 가승인
	// "PAID": 수수료 정산, "CANCLED": 결제 취소
	@Column(nullable = false)
	private String status; // 결제 상태 (READY -> AUTHORIZED -> PAID or CANCELED)

	private String failReason; // 실패 시 사유 (잔액부족 등)

	private LocalDateTime authorizedAt; // 가승인 일시
	private LocalDateTime paidAt; // 실제 결제 일시

	@Builder
	public Payment(String paymentKey, String orderId, Long amount, String type, String status) {
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.originalAmount = amount;
		this.type = type;
		this.status = status;
		this.authorizedAt = LocalDateTime.now();
	}

	// === 비지니스 로직 === //
	// 부분 결제
	public void paid(Long paidAmount) {
		this.finalAmount = paidAmount;
		this.status = "PAID";
		this.paidAt = LocalDateTime.now();
	}

	// 결제 취소
	public void cancel() {
		this.status = "CANCEL";
		this.finalAmount = 0L;
	}
}