package com.ssafy.bbb.model.dao;

import org.apache.ibatis.annotations.Param;

import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.enums.ReservationStatus;

public interface ReservationDao {

	public ReservationDto findById(Long reservationId);

	// 예약자가 예약 요청
	public Long reservationUser(ReservationDto reservation);

	// 중개업자가 예약 수락
	public void reservationAccept(@Param("reservationId") Long reservationId,
			@Param("agentPaymentKey") String agentPaymentKey, @Param("status") ReservationStatus status);
}
