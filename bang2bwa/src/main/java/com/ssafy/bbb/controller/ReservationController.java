package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dto.ReservationRequestDto;
import com.ssafy.bbb.model.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	@PostMapping
	public ApiResponse<String> createReservation(@RequestBody ReservationRequestDto request,
			@RequestHeader("user-id") Long userId) { // 임시로 userId header에서 가져오기 => 로그인 구현 후 변경.
		reservationService.requestReservation(request, userId);

		return ApiResponse.successWithNoContent("예약이 완료되었습니다.");
	}
}
