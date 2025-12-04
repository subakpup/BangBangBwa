package com.ssafy.bbb.model.dao;

import com.ssafy.bbb.model.dto.ReservationDto;

public interface ReservationDao {
	// 예약자가 예약 요청
	public Long reservationUser(ReservationDto reservation);
}
