package com.ssafy.payment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ssafy.payment.domain.Payment;
import com.ssafy.payment.dto.PaymentRequestDto;
import com.ssafy.payment.dto.PaymentResponseDto;
import com.ssafy.payment.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PgService {
	private final PaymentRepository paymentRepository;

	@Transactional
	public PaymentResponseDto processPayment(PaymentRequestDto request) {
		log.info("[PG] 결제 요청 수신 - orderId: {}, 금액: {}", request.getOrderId(), request.getAmount());

		// 1. [실패 시나리오] 주문번호에 "FAIL"이 포함되어 있으면 강제 실패 처리
		// (메인 서버의 에러 처리 로직 테스트용)
		if (request.getOrderId().contains("FAIL")) {
			savePaymentLog(request, "FAIL", "테스트용 강제 실패");
			return PaymentResponseDto.fail("결제가 거절되었습니다. (테스트)");
		}

		// 2. [실패 시나리오] 금액의 끝자리가 999원이면 '잔액 부족' 처리
		// 예: 10,999원 결제 시 실패
		if (request.getAmount() % 1000 == 999) {
			savePaymentLog(request, "FAIL", "잔액 부족");
			return PaymentResponseDto.fail("잔액이 부족합니다.");
		}

		// 3. [성공 시나리오] 그 외에는 정상 승인
		savePaymentLog(request, "SUCCESS", null);

		PaymentResponseDto.paymentReceipt receipt = PaymentResponseDto.paymentReceipt.builder()
				.paymentKey(request.getPaymentKey()).orderId(request.getOrderId())
				.approvedAt(LocalDateTime.now().toString()).method("CARD") // 더미니까 카드로 고정
				.build();

		return PaymentResponseDto.success("정상 승인되었습니다.", receipt);
	}

	// 결제 이력 DB 저장 (성공이든 실패든 기록은 남겨야 함)
	private void savePaymentLog(PaymentRequestDto request, String status, String failReason) {
		Payment payment = Payment.builder().paymentKey(request.getPaymentKey()).orderId(request.getOrderId())
				.amount(request.getAmount()).status(status).failReason(failReason).build();

		paymentRepository.save(payment);
	}
}
