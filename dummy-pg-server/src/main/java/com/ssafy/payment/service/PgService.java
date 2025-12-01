package com.ssafy.payment.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1444633595275645071/5bm4E8wHhtwWNJOJXenct68G3JTfKQc0oOzuqrW0rse6W8Ia7i9_Rilrdai46jXvm5i0";

	// 1. 가승인 (Pre-Auth): 한도만 잡음(실제 결제 X)
	@Transactional
	public PaymentResponseDto requestPreAuth(PaymentAuthDto request) {
		// 가짜 키 생성
		String paymentKey = "dummy_" + UUID.randomUUID().toString().substring(0, 8);

		Payment payment = Payment.builder().paymentKey(paymentKey) // 결제 key
				.orderId(request.getOrderId()) // orderId
				.amount(request.getAmount()) // 10,000원
				.type(request.getType()) // DEPOSIT
				.status("AUTHORIZED") // 상태: 가승인(돈 안 나감)
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

		try {
			sendNotification(payment);
		} catch (Exception e) {
			log.error("사용자 알림 실패: {}", e.getMessage());
		}

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

	private void sendNotification(Payment payment) throws Exception {
		long refundAmount = payment.getOriginalAmount() - payment.getFinalAmount(); // 환불 금액

		StringBuilder sb = new StringBuilder();
		sb.append("💳 **[결제 승인 알림]**\n");
		sb.append("> **거래유형:** 보증금 정산 (수수료 결제)\n");
		sb.append("> **주문번호:** `").append(payment.getOrderId()).append("`\n");
		sb.append("> **결제금액(수수료):** `").append(String.format("%,d", payment.getFinalAmount())).append("원`\n");
		sb.append("> **환불금액(반환):** `").append(String.format("%,d", refundAmount)).append("원`\n");

		sb.append("> **상태:** 정상 승인 완료 ✅");

		String content = sb.toString();

		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("content", content);

		// json 문자열 생성
		String jsonBody = objectMapper.writeValueAsString(bodyMap);

		// HTTP 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

		// 요청 전송
		restTemplate.postForObject(DISCORD_WEBHOOK_URL, entity, String.class);
	}
}
