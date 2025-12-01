package com.ssafy.payment.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.payment.dto.PaymentAuthDto;
import com.ssafy.payment.dto.PaymentCaptureDto;
import com.ssafy.payment.dto.PaymentResponseDto;
import com.ssafy.payment.service.PgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PgController {
	private final PgService pgService;

	// 1. [예약 신청/수락 시] 가승인 요청
	@PostMapping("/pre-auth")
	public ResponseEntity<PaymentResponseDto> preAuth(@RequestBody PaymentAuthDto request) {
		return ResponseEntity.ok(pgService.requestPreAuth(request));
	}

	// 2. [만남 완료 시] 부분 결제 (수수료 결제)
	@PostMapping("/capture")
	public ResponseEntity<PaymentResponseDto> capture(@RequestBody PaymentCaptureDto request) {
		PaymentResponseDto response = pgService.capturePayment(request);

		if ("FAIL".equals(response.getResultCode())) {
			return ResponseEntity.badRequest().body(response); // 400 Error
		}

		return ResponseEntity.ok(response);
	}

	// 3. [예약 거절 시] 가승인 취소
	@PostMapping("/cancel")
	public ResponseEntity<PaymentResponseDto> cancel(@RequestBody Map<String, String> request) {
		return ResponseEntity.ok(pgService.cancelPayment(request.get("paymentKey")));
	}
}
