package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.LocationDto;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;

public interface ReservationService {
	public void requestReservation(ReservationRequestDto request, Long userId);

	public void acceptReservation(Long reservationId, Long agentId);

	public void confirmMeeting(Long reservationId, Long requestUserId, LocationDto location);

	public void reportNoShow(Long reservationId, Long reporterId, LocationDto location);

	public void defendReport(Long reservationId, Long userId, LocationDto location);

	public void executePunishment(Long reporterId, ReservationDto reservation);
}
