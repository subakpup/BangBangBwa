package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.AcceptRequestDto;
import com.ssafy.bbb.model.dto.LocationDto;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;
import com.ssafy.bbb.model.dto.ReservationResponseDto;

public interface ReservationService {
	public void requestReservation(ReservationRequestDto request, Long userId);

	public void cancelReservation(Long reservationId, Long userId);

	public void acceptReservation(AcceptRequestDto request, Long agentId);

	public void rejectReservation(Long reservationId, Long agentId, String cancelReason);

	public void confirmMeeting(Long reservationId, Long requestUserId, LocationDto location);

	public void reportNoShow(Long reservationId, Long reporterId, LocationDto location);

	public void defendReport(Long reservationId, Long userId, LocationDto location);

	public void processAutoCancel(ReservationDto reservation);

	public void processAutoPunishment(ReservationDto reservation);
	
	public ReservationResponseDto getReservationDetail(Long reservationId);
}
