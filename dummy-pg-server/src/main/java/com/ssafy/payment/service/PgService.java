package com.ssafy.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.payment.domain.Payment;
import com.ssafy.payment.dto.PaymentAuthDto;
import com.ssafy.payment.dto.PaymentCaptureDto;
import com.ssafy.payment.dto.PaymentResponseDto;
import com.ssafy.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PgService {
	private final PaymentRepository paymentRepository;

	// 1. 가승인 (Pre-Auth): 한도만 잡음(실제 결제 X)
	@Transactional
	public PaymentResponseDto requestPreAuth(PaymentAuthDto request) {
		// 가짜 키 생성
		String paymentKey = "dummy_" + UUID.randomUUID().toString().substring(0, 8);

		Payment payment = Payment.builder().paymentKey(paymentKey) // 결제 key
				.orderId(request.getOrderId()) // 10,000원
				.amount(request.getAmount()) // DEPOSIT
				.type("AUTHORIZED") // 상태: 가승인(돈 안 나감)
				.build();

		paymentRepository.save(payment);
		log.info("가승인 완료: key={}, Amount={}", paymentKey, request.getAmount());

		return PaymentResponseDto.success("가승인 성공", payment);
	}

	// 2. 부분 결제: 묶은 한도 중 일부만 수수료로 결제, 나머지는 환불
	@Transactional
	public PaymentResponseDto capturePayment(PaymentCaptureDto request) {

		Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

		if (!"AUTHORIZED".equals(payment.getStatus())) {
			return PaymentResponseDto.fail("가승인 상태의 결제만 매입할 수 있습니다.");
		}

		if (request.getCaptureAmount() > payment.getOriginalAmount()) {
			return PaymentResponseDto.fail("수수료가 원금보다 크면 안됩니다.");
		}

		payment.paid(request.getCaptureAmount());

		return PaymentResponseDto.success("부분 매입 성공(나머지 금액 자동 반환)", payment);
	}

	@Transactional
	public PaymentResponseDto cancelPayment(String paymentKey) {
		Payment payment = paymentRepository.findByPaymentKey(paymentKey)
				.orElseThrow(() -> new IllegalArgumentException("결제 정보 없음"));

		if (!"AUTHORIZED".equals(payment.getStatus())) {
			return PaymentResponseDto.fail("가승인 상태만 취소 가능합니다.");
		}

		payment.cancel();
		return PaymentResponseDto.success("가승인이 취소되었습니다. (수수료 0원)", null);
	}

}
