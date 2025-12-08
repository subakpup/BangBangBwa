package com.ssafy.bbb.model.dao;

import java.time.LocalDateTime;
import java.util.List;

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

	// 유저의 상태를 변경 => Y: 정상적으로 나옴, N: 노쇼 신고가 접수됨.
	public void updateConfirmation(@Param("type") String type, @Param("reservationId") Long reservationId,
			@Param("status") String status);

	public void updateStatus(@Param("reservationId") Long reservationId, @Param("status") ReservationStatus status);

	public void report(@Param("reservationId") Long reservationId, @Param("type") String userType,
			@Param("status") ReservationStatus status, @Param("reportedAt") LocalDateTime reportedAt);

	public List<ReservationDto> findExpiredPendingReservations();

	public List<ReservationDto> findExpiredReportedReservations();
}
