package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.controller.docs.ReservationControllerDocs;
import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dto.LocationDto;
import com.ssafy.bbb.model.dto.RejectReasonDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;
import com.ssafy.bbb.model.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerDocs {
	private final ReservationService reservationService;

	// 사용자 예약 요청
	@PostMapping
	public ApiResponse<String> createReservation(
			@RequestBody ReservationRequestDto request
			, @RequestHeader("user-id") Long userId) {
			
		reservationService.requestReservation(request, userId);

		return ApiResponse.successWithNoContent("예약이 완료되었습니다.");
	}

	// 부동산 업자 예약 승인
	@PostMapping("{reservationId}/accept")
	public ApiResponse<String> acceptReservation(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long userId) {
		
		reservationService.acceptReservation(reservationId, userId);

		return ApiResponse.successWithNoContent("예약을 승인하였습니다.");
	}

	// 부동산 업자 예약 거절
	@PostMapping("{reservationId}/reject")
	public ApiResponse<String> rejectReservation(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long agentId
			, @RequestBody RejectReasonDto rejectResone) {
		
		reservationService.rejectReservation(reservationId, agentId, rejectResone.getRejectReason());

		return ApiResponse.successWithNoContent("예약을 거절하였습니다.");
	}

	// 예약자 예약 취소
	@PostMapping("{reservationId}/cancel")
	public ApiResponse<String> cancelReservation(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long userId) {
		
		reservationService.cancelReservation(reservationId, userId);

		return ApiResponse.successWithNoContent("예약을 취소하였습니다.");
	}

	// 만남 성사
	@PostMapping("{reservationId}/confirm")
	public ApiResponse<String> confirmReservation(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long userId
			, @RequestBody LocationDto curLocation) {
		
		reservationService.confirmMeeting(reservationId, userId, curLocation);

		return ApiResponse.successWithNoContent("예약을 확인하였습니다. 상대방의 동의 후 보증금이 반환됩니다.");
	}

	// 노쇼 신고
	@PostMapping("{reservationId}/noshow")
	public ApiResponse<String> reportNoShow(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long reporterId
			, @RequestBody LocationDto curLocation) {
		
		reservationService.reportNoShow(reservationId, reporterId, curLocation);

		return ApiResponse.successWithNoContent("신고가 접수되었습니다.");
	}

	// 신고 이의 제기
	@PostMapping("{reservationId}/defend")
	public ApiResponse<String> defendNoShow(
			@PathVariable Long reservationId
			, @RequestHeader("user-id") Long userId
			, @RequestBody LocationDto curLocation) {
		
		reservationService.defendReport(reservationId, userId, curLocation);

		return ApiResponse.successWithNoContent("매물 근처임이 확인되었습니다.");
	}
}
