package com.ssafy.bbb.util;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.ssafy.bbb.model.dao.ReservationDao;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.service.NotificationService;
import com.ssafy.bbb.model.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {
	private final ReservationService reservationService;
	private final ReservationDao reservationDao;
	
	private final NotificationService notificationService;
	private final UserDao userDao;
	
	private static final String PENDING_PREFIX = "expire:reservation:pending:";
	private static final String REPORTED_PREFIX = "expire:reservation:reported:";
	
	private static final String NOTIFY_PREFIX = "expire:reservation:notify:";
	
	public RedisExpirationListener(RedisMessageListenerContainer container
								, ReservationService reservationService
								, ReservationDao reservationDao
								, NotificationService notificationService
								, UserDao userDao) {
		super(container);
		this.reservationService = reservationService;
		this.reservationDao = reservationDao;
		this.notificationService = notificationService;
		this.userDao = userDao;
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
			} else if(expiredKey.startsWith(NOTIFY_PREFIX)) {
				handleNotifyExpiration(expiredKey);
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
	
	private void handleNotifyExpiration(String expiredKey) {
		Long reservationId = Long.parseLong(expiredKey.replace(NOTIFY_PREFIX, ""));
		
		ReservationDto reservation = reservationDao.findById(reservationId);

		if(reservation == null) return;
		
		StringBuilder htmlText = new StringBuilder();
		
		htmlText.append("<!DOCTYPE html>");
		htmlText.append("<html lang=\"ko\">");
		htmlText.append("<p> 예약 일정은 순조롭게 진행되었나요? </p>");
		htmlText.append("<p> 보증금을 돌려받으시려면 인증을 해주셔야해요! </p>");
		htmlText.append("<a href='http://localhost:5173/reservation/action/").append(reservationId).append("'> 예약 인증 하러 가기 </a>");
		htmlText.append("</html>");
		
		String userEmail = userDao.findEmailById(reservation.getUserId());
		String agentEmail = userDao.findEmailById(reservation.getAgentId());
		
		notificationService.sendHtmlEmail(userEmail, "[방방봐] 예약하신 시간이에요!", htmlText.toString());
		notificationService.sendHtmlEmail(agentEmail, "[방방봐] 예약하신 시간이에요!", htmlText.toString());
	}
}
