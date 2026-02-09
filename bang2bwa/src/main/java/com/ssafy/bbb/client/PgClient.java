package com.ssafy.bbb.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ssafy.bbb.model.dto.pg.PgAuthRequestDto;
import com.ssafy.bbb.model.dto.pg.PgCaptureRequestDto;
import com.ssafy.bbb.model.dto.pg.PgResponseDto;

@FeignClient(name = "pg-client", url = "${pg.url}")
public interface PgClient {

	// 1. 가승인 요청
	@PostMapping("/api/v1/payments/pre-auth")
	PgResponseDto requestPreAuth(@RequestBody PgAuthRequestDto request);

	// 2. 부분 결제 요청
	@PostMapping("/api/v1/payments/capture")
	PgResponseDto requestCapture(@RequestBody PgCaptureRequestDto request);

	// 3. 가승인 취소 요청
	@PostMapping("/api/v1/payments/cancel")
	PgResponseDto requestCancel(@RequestBody Map<String, String> request);
}
