package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.PathVariable;
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

	// 사용자 예약 요청
	@PostMapping
	public ApiResponse<String> createReservation(@RequestBody ReservationRequestDto request,
			@RequestHeader("user-id") Long userId) { // 임시로 userId header에서 가져오기 => 로그인 구현 후 변경.
		reservationService.requestReservation(request, userId);

		return ApiResponse.successWithNoContent("예약이 완료되었습니다.");
	}

	// 부동산 업자 예약 승인
	@PostMapping("{reservationId}/accept")
	public ApiResponse<String> acceptReservation(@PathVariable Long reservationId,
			@RequestHeader("user-id") Long userId) { // 임시로 userId header에서 가져오기 => 로그인 구현 후 변경.
		reservationService.acceptReservation(reservationId, userId);

		return ApiResponse.successWithNoContent("예약을 승인하였습니다.");
	}

	@PostMapping("{reservationId}/confirm")
	public ApiResponse<String> confirmReservation(@PathVariable Long reservationId,
			@RequestHeader("user-id") Long userId) { // 임시로 userId header에서 가져오기 => 로그인 구현 후 변경.
		reservationService.confirmMeeting(reservationId, userId);

		return ApiResponse.successWithNoContent("예약을 확인하였습니다. 상대방의 동의 후 보증금이 반환됩니다.");
	}
}
