package com.ssafy.bbb.util;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ssafy.bbb.model.dao.ReservationDao;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {
	private final ReservationDao reservationDao;
	private final ReservationService reservationService;

	@Scheduled(cron = "0 * * * * *")
	public void scheduleReservationTasks() {
		log.info("scheduler run: cheking");

		// [자동 취소] 1시간이 지나도록 중개업자가 수락하지 않은 예약 자동 취소
		List<ReservationDto> expiredList = reservationDao.findExpiredPendingReservations();
		for (ReservationDto reservation : expiredList) {
			try {
				reservationService.processAutoCancel(reservation);
			} catch (Exception e) {
				log.error("자동 취소 실패 ID: {}", reservation.getReservationId(), e);
			}
		}

		// [노쇼 처벌] 10분 지난 REPORTED 예약 처리
		List<ReservationDto> reportedList = reservationDao.findExpiredReportedReservations();
		for (ReservationDto reservation : reportedList) {
			try {
				reservationService.processAutoPunishment(reservation);
			} catch (Exception e) {
				log.error("자동 처벌 실패 ID: {}", reservation.getReservationId(), e);
			}
		}
	}
}
