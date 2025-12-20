package com.ssafy.bbb.util;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.ssafy.bbb.model.dao.ReservationDao;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
	private final ReservationService reservationService;
	private final ReservationDao reservationDao;
	
	private static final String PENDING_PREFIX = "expire:reservation:pending:";
	private static final String REPORTED_PREFIX = "expire:reservation:reported:";
	
	public RedisExpirationListener(RedisMessageListenerContainer container
								, ReservationService reservationService
								, ReservationDao reservationDao) {
		super(container);
		this.reservationService = reservationService;
		this.reservationDao = reservationDao;
	}
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();
		log.info("Redis Expire Event Call: {}", expiredKey);
		
		try {
			if(expiredKey.startsWith(PENDING_PREFIX)) {
				handlePendingExpiration(expiredKey);
			} else if(expiredKey.startsWith(REPORTED_PREFIX)) {
				handleReportedExpiration(expiredKey);
			}
		} catch(Exception e) {
			log.error("Redis 만료 이벤트 중 에러 발생: {} {}", expiredKey, e);
		}
	}
	
	private void handlePendingExpiration(String expiredKey) {
		Long reservationId = Long.parseLong(expiredKey.replace(PENDING_PREFIX, ""));
		
		ReservationDto reservation = reservationDao.findById(reservationId);
		
		if(reservation != null && reservation.getStatus() == ReservationStatus.PENDING) {
			reservationService.processAutoCancel(reservation);
		}
	}
	
	private void handleReportedExpiration(String expiredKey) {
		Long reservationId = Long.parseLong(expiredKey.replace(REPORTED_PREFIX, ""));
		
		ReservationDto reservation = reservationDao.findById(reservationId);
		
		if(reservation != null && reservation.getStatus() == ReservationStatus.REPORTED) {
			reservationService.processAutoPunishment(reservation);
		}
	}
}
