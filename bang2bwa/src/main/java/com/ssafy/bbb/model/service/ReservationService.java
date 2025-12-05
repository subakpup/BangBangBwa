package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.ReservationRequestDto;

public interface ReservationService {
	public void requestReservation(ReservationRequestDto request, Long userId);

	public void acceptReservation(Long reservationId, Long agentId);

	public void confirmMeeting(Long reservationId, Long requestUserId);

	public void reportNoShow(Long reservationId, Long reporterId);
}
