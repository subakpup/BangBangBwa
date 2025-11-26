package com.ssafy.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.payment.dto.PaymentRequestDto;
import com.ssafy.payment.dto.PaymentResponseDto;
import com.ssafy.payment.service.PgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PgController {
	private final PgService pgService;

	// 결제 승인 API
	// POST http://localhost:9090/api/v1/payments/confirm
	@PostMapping("/confirm")
	public ResponseEntity<PaymentResponseDto> confirmPayment(@RequestBody PaymentRequestDto request) {

		PaymentResponseDto response = pgService.processPayment(request);

		// 결과 코드가 FAIL이면 400 Bad Request, 성공이면 200 OK 리턴
		if ("FAIL".equals(response.getResultCode())) {
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}
}
