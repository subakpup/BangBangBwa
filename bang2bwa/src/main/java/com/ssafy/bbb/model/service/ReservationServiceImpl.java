package com.ssafy.bbb.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.client.PgClient;
import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.PaymentDao;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dao.ReservationDao;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dao.VirtualBankDao;
import com.ssafy.bbb.model.dto.AcceptRequestDto;
import com.ssafy.bbb.model.dto.LocationDto;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.PaymentDto;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;
import com.ssafy.bbb.model.dto.ReservationResponseDto;
import com.ssafy.bbb.model.dto.VirtualBankDto;
import com.ssafy.bbb.model.dto.pg.PgAuthRequestDto;
import com.ssafy.bbb.model.dto.pg.PgCaptureRequestDto;
import com.ssafy.bbb.model.dto.pg.PgResponseDto;
import com.ssafy.bbb.model.enums.PaymentStatus;
import com.ssafy.bbb.model.enums.PaymentType;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.util.GeometryUtils;
import com.ssafy.bbb.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {
	private final ProductDao productDao;
	private final ReservationDao reservationDao;
	private final PaymentDao paymentDao;
	private final UserDao userDao;
	private final VirtualBankDao virtualBankDao;

	private final PgClient pgClient;
	private final NotificationService notificationService;
	private final RedisUtil redisUtil;
	
	private static final String NOTIFY_PREFIX = "expire:reservation:notify:";
	private static final String PENDING_PREFIX = "expire:reservation:pending:";
	private static final String REPORTED_PREFIX = "expire:reservation:reported:";
	
	private static final long PENDING_EXPIRE = 3600L; // 1시간
	private static final long REPORTED_EXPIRE = 600L; // 10분

	// 예약자 예약 요청
	@Override
	@Transactional
	public void requestReservation(ReservationRequestDto request, Long userId) {
		// 1. 매물 조회 및 잠금(동시성 제어)
		// agent_id, status
		Map<String, Object> productInfo = productDao.selectProductForUpdate(request.getProductId());
		if (productInfo == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 2. 상태 검증
		ReservationStatus status = ReservationStatus.valueOf((String) productInfo.get("status"));
		if (status != ReservationStatus.AVAILABLE) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE);
		}
		
		VirtualBankDto bank = virtualBankDao.findById(request.getBankId());

		// 3. PG 서버 통신: 가승인 요청
		String orderId = "ORD_USER_" + UUID.randomUUID().toString();
		Long amount = 10000L;
		
		String paymentKey = connPgServerPreAuth(orderId, amount, bank.getWebhookUrl());

		Long agentId = (Long) productInfo.get("agentId");
		
		// 4. 예약 정보 저장
		ReservationDto reservation = ReservationDto.builder() //
				.agentId(agentId) // 중개사 pk
				.productId(request.getProductId()) // 매물 pk
				.userId(userId) // 예약자 pk
				.visitDate(request.getVisitDate()) // 예약일
				.message(request.getMessage()) // 예약 메세지
				.status(ReservationStatus.PENDING) // 예약 상태 => pending
				.depositAmount(10000L) // 보증금 가격
				.userPaymentKey(paymentKey) // PG에서 받은 paymentKey
				.build();
		reservationDao.reservationUser(reservation);

		// 5. 결제 이력 저장
		PaymentDto paymentLog = PaymentDto.builder() //
				.pgPaymentKey(paymentKey) // 결제 번호(pg 서버가 준 키)
				.orderId(orderId) // 주문번호(메인 서버의 키)
				.amount(amount) // 금액
				.status(PaymentStatus.AUTHORIZED) // 결제 정보 -> 가승인
				.paymentType(PaymentType.DEPOSIT) // 결제 타입 -> 보증금
				.reservationId(reservation.getReservationId()) // 생성된 예약의 예약번호
				.approvedAt(LocalDateTime.now()) // 결제 시각 저장
				.userId(userId) //
				.build();
		paymentDao.paymentSave(paymentLog);

		// 6. 매물 상태 변경
		productDao.updateProductStatus(request.getProductId(), ReservationStatus.PENDING);

		// 7. 중개인에게 알람 발송
		String agentEmail = userDao.findEmailById(agentId);
		if (agentEmail != null && !agentEmail.isEmpty()) {
			StringBuilder message = new StringBuilder();
			message.append("예약자 ID: ").append(userId).append("\n");
			message.append("매물 ID: ").append(request.getProductId()).append("\n");
			message.append("예약 일시: ").append(request.getVisitDate()).append("\n");
			message.append("예약자의 메세지: \n");
			message.append(request.getMessage());

			notificationService.sendEmail(agentEmail, "[방방봐] 새로운 예약 요청이 도착했습니다!", message.toString());
		}
		
		// 8. redis에 1시간 pending timer 설정
		redisUtil.setDataExpire(PENDING_PREFIX + reservation.getReservationId(), "ON", PENDING_EXPIRE);
	}

	// 예약자 사정으로 인한 예약 취소
	// 2주 전 => 전액 환불
	// 1주 전 => 50% 환불
	// 그 이후 => 30% 환불
	@Override
	@Transactional
	public void cancelReservation(Long reservationId, Long userId) {
		// 1. 예약 정보 조회
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 2. 상태 검증
		if (reservation.getUserId() != userId) {
			throw new CustomException(ErrorCode.RESERVATION_UNAUTORIZATION);
		}
		if (reservation.getStatus() != ReservationStatus.RESERVED
				&& reservation.getStatus() != ReservationStatus.PENDING) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_RESERVED);
		}

		// 3-1. 중개업자 가승인 요청 해제(수수료 0원)
		if (reservation.getAgentPaymentKey() != null && !reservation.getAgentPaymentKey().equals("")) {
			connPgServerCancel(reservation.getAgentPaymentKey());
		}

		// 3-2. 예약일과의 시간을 계산하여 수수료 지불
		long diff = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getVisitDate().toLocalDate());
		if (diff > 14)
			connPgServerCancel(reservation.getUserPaymentKey());
		else {
			long penalty = (diff > 7) ? 5000L : 7000L;
			connPgServerCapture(reservation.getUserPaymentKey(), penalty, PaymentStatus.PAID);
		}

		// 4. 예약 상태 업데이트
		reservationDao.reservationAccept(reservationId, "", ReservationStatus.QUIT);

		// 5. 매물 상태 업데이트
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.AVAILABLE);

		// 6. 예약자에게 알람 발송
		String userEmail = userDao.findEmailById(reservation.getUserId());
		String agentEmail = userDao.findEmailById(reservation.getAgentId());
		if (userEmail != null && !userEmail.isEmpty())
			notificationService.sendEmail(userEmail, "[방방봐] 예약이 취소되었습니다.", "");
		if (agentEmail != null && !agentEmail.isEmpty())
			notificationService.sendEmail(agentEmail, "[방방봐] 사용자의 요청으로 예약이 취소되었습니다.", "");
		
		// 7. pending timer 해제
		redisUtil.deleteData(PENDING_PREFIX + reservationId);
	}

	// 중개업자 예약 거절
	@Override
	@Transactional
	public void rejectReservation(Long reservationId, Long agentId, String cancelReason) {
		// 1. 예약 정보 조회
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 2. 상태 검증
		if (reservation.getAgentId() != agentId) {
			throw new CustomException(ErrorCode.RESERVATION_UNAUTORIZATION);
		}

		if (reservation.getStatus() != ReservationStatus.PENDING) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_PENDING);
		}

		// 3. 예약자 가승인 요청 해제(수수료 0원)
		String userKey = reservation.getUserPaymentKey();
		connPgServerCancel(userKey);

		// 4. 예약 상태 업데이트
		reservationDao.reservationAccept(reservationId, "", ReservationStatus.QUIT);

		// 5. 매물 상태 업데이트
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.AVAILABLE);

		// 6. 예약자에게 알람 발송
		String userEmail = userDao.findEmailById(reservation.getUserId());
		if (userEmail != null && !userEmail.isEmpty()) {
			StringBuilder message = new StringBuilder();
			message.append("취소 사유: ").append(cancelReason).append("\n");

			notificationService.sendEmail(userEmail, "[방방봐] 예약 요청이 거절되었습니다.", message.toString());
		}
		
		// 7. pending timer 해제
		redisUtil.deleteData(PENDING_PREFIX + reservationId);
	}

	// 중개업자 예약 승인
	@Override
	@Transactional
	public void acceptReservation(AcceptRequestDto request, Long agentId) {
		// 1. 예약 정보 조회
		ReservationDto reservation = reservationDao.findById(request.getReservationId());
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 2. 상태 검증
		if (reservation.getAgentId() != agentId) {
			throw new CustomException(ErrorCode.RESERVATION_UNAUTORIZATION);
		}

		if (reservation.getStatus() != ReservationStatus.PENDING) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_PENDING);
		}
		
		VirtualBankDto bank = virtualBankDao.findById(request.getBankId());

		// 3. PG 서버 통신: 가승인 요청
		String orderId = "ORD_AGENT_" + UUID.randomUUID().toString();
		Long amount = 10000L;
		String paymentKey = connPgServerPreAuth(orderId, amount, bank.getWebhookUrl());

		// 4. 예약 테이블 업데이트
		reservationDao.reservationAccept(request.getReservationId(), paymentKey, ReservationStatus.RESERVED);

		// 5. 결제 이력 저장
		PaymentDto paymentLog = PaymentDto.builder() //
				.pgPaymentKey(paymentKey) // 결제 번호(pg 서버가 준 키)
				.orderId(orderId) // 주문번호(메인 서버의 키)
				.amount(amount) // 금액
				.status(PaymentStatus.AUTHORIZED) // 결제 정보 -> 가승인
				.paymentType(PaymentType.DEPOSIT) // 결제 타입 -> 보증금
				.reservationId(request.getReservationId()) // 예약 ID 연결
				.approvedAt(LocalDateTime.now()) // 결제 시각 저장
				.userId(agentId) //
				.build();
		paymentDao.paymentSave(paymentLog);

		// 6. 매물 상태 업데이트
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.RESERVED);

		// 7. 예약자에게 알람 발송
		String userEmail = userDao.findEmailById(reservation.getUserId());
		if (userEmail != null && !userEmail.isEmpty()) {
			StringBuilder message = new StringBuilder();
			message.append("매물 ID: ").append(reservation.getProductId()).append("\n");
			message.append("예약 일시: ").append(reservation.getVisitDate()).append("\n");

			notificationService.sendEmail(userEmail, "[방방봐] 예약 요청이 확정 되었습니다!", message.toString());
		}
		
		String agentEmail = userDao.findEmailById(agentId);
		long visitTimer = Math.abs(Duration.between(LocalDateTime.now(), reservation.getVisitDate()).getSeconds());
		
		// redis에 약속시간에 소멸되는 키를 생성. => 소멸시 이메일을 보낼 것.
		redisUtil.setDataExpire(NOTIFY_PREFIX + request.getReservationId(), "TRUE", visitTimer);
		
		// 7. pending timer 해제
		redisUtil.deleteData(PENDING_PREFIX + request.getReservationId());
	}

	// 만남 성사
	@Override
	@Transactional
	public void confirmMeeting(Long reservationId, Long userId, LocationDto location) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		LocationDto productLocation = productDao.findLocationById(reservation.getProductId());
		double distance = GeometryUtils.calculateDistance(location.getLatitude(), location.getLongitude(),
				productLocation.getLatitude(), productLocation.getLongitude());

		if (distance > 500) {
			throw new CustomException(ErrorCode.FAR_DISTANCE);
		}

		String userType = "";
		if (userId == reservation.getUserId()) {
			userType = "USER";
		} else if (userId == reservation.getAgentId()) {
			userType = "AGENT";
		} else { // userId, agentId와 둘다 다르다면, 예외를 반환
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		// 현재 접근한 유저(예약자 혹은 중개업자)의 confirm 을 'W' -> 'Y' 로 업데이트
		reservationDao.updateConfirmation(userType, reservationId, "Y");

		// 다시 reservation을 조회하여 둘 다 조회했는지 확인.
		ReservationDto updateReservation = reservationDao.findById(reservationId);

		boolean isUserConfirmed = updateReservation.getUserConfirmed().equals("Y");
		boolean isAgentConfirmed = updateReservation.getAgentConfirmed().equals("Y");

		if (isUserConfirmed && isAgentConfirmed) {
			// 둘다 확인하였다면 정산 로직을 실행
			long fee = 1000L; // 수수료

			connPgServerCapture(updateReservation.getUserPaymentKey(), fee, PaymentStatus.PAID); // 예약자 수수료 결제 승인
			connPgServerCapture(updateReservation.getAgentPaymentKey(), fee, PaymentStatus.PAID); // 중개업자 수수료 결제 승인

			// 예약 정상 종료
			reservationDao.updateStatus(reservationId, ReservationStatus.QUIT);
			// 다시 매물을 예약 가능 상태로 변경.
			productDao.updateProductStatus(updateReservation.getProductId(), ReservationStatus.AVAILABLE);
		}
	}

	@Override
	@Transactional
	public void reportNoShow(Long reservationId, Long reporterId, LocationDto location) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 노쇼 신고는 예약 시간이 지나야만 가능함.
		if (reservation.getVisitDate().isAfter(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.FAIL_REPORT);
		}

		LocationDto destination = productDao.findLocationById(reservation.getProductId());
		if (destination == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 신고자와 매물 간의 거리 계산.
		double distance = GeometryUtils.calculateDistance(location.getLatitude(), location.getLongitude(),
				destination.getLatitude(), destination.getLongitude());

		if (distance > 500) { // 예약 매물과의 거리가 500m를 넘어선다면 신고를 기각
			throw new CustomException(ErrorCode.FAR_DISTANCE);
		}

		String userType = "";
		Long offenderId = 0L;
		if (reporterId == reservation.getUserId()) {
			userType = "AGENT";
			offenderId = reservation.getAgentId();
		} else if (reporterId == reservation.getAgentId()) {
			userType = "USER";
			offenderId = reservation.getUserId();
		} else { // userId, agentId와 둘다 다르다면, 예외를 반환
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		// 신고당한 유저의 상태를 'W' => 'N'로 업데이트.
		reservationDao.report(reservationId, userType, ReservationStatus.REPORTED, LocalDateTime.now());

		// 신고 당한 사람에게 메일 전송(이의제기할 수 있는 링크를 같이 제공 => 억울한 신고를 막기 위함)
		String offenderEmail = userDao.findEmailById(offenderId);
		if (offenderEmail != null && !offenderEmail.isEmpty()) {
			StringBuilder message = new StringBuilder();
			message.append("신고가 접수되었습니다.\n");
			// todo: 사이트 링크 제공 할 것(프론트)
			message.append("이의를 제기하시려면\n").append("[todo: siteLink]");
			message.append("\n로 접속하세요.");

			notificationService.sendEmail(offenderEmail, "[방방봐]", message.toString());
		}
		
		// redis에 reporter timer(10분 설정)
		redisUtil.setDataExpire(REPORTED_PREFIX + reservationId, "ON", REPORTED_EXPIRE);
	}

	@Override
	@Transactional
	public void defendReport(Long reservationId, Long userId, LocationDto location) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 신고된 상태가 아니라면 작동 안함.
		if (reservation.getStatus() != ReservationStatus.REPORTED) {
			throw new CustomException(ErrorCode.NOT_REPROTED);
		}

		LocationDto destination = productDao.findLocationById(reservation.getProductId());
		if (destination == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		// 현재 매물과의 거리 계산
		double distance = GeometryUtils.calculateDistance(location.getLatitude(), location.getLongitude(),
				destination.getLatitude(), destination.getLongitude());

		if (distance > 500) { // 매물과의 거리가 500m 이상이면 이의제기가 실패.
			throw new CustomException(ErrorCode.FAR_DISTANCE);
		}

		String userType = "";
		Long reporterId = 0L;
		if (userId == reservation.getUserId()) {
			userType = "USER";
			reporterId = reservation.getAgentId();
		} else if (userId == reservation.getAgentId()) {
			userType = "AGENT";
			reporterId = reservation.getUserId();
		} else { // userId, agentId와 둘다 다르다면, 예외를 반환
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}
		// 신고당한 유저의 상태를 'N' => 'W'로 업데이트.
		reservationDao.updateConfirmation(userType, reservationId, "W");

		// 다시 예약상태를 되돌림.
		reservationDao.updateStatus(reservationId, ReservationStatus.RESERVED);

		// 신고한 사람에게 메일 전송
		String reporterEmail = userDao.findEmailById(reporterId);
		if (reporterEmail != null && !reporterEmail.isEmpty()) {
			notificationService.sendEmail(reporterEmail, "[방방봐]", "상대방이 근처에 있습니다. 잠시만 기다려주세요.");
		}
		
		// reported timer 해제
		redisUtil.deleteData(REPORTED_PREFIX + reservationId);
	}
	
	@Override
	public ReservationResponseDto getReservationDetail(Long reservationId, Long userId) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		
		if(userId != reservation.getUserId() && userId != reservation.getAgentId()) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
		
		return reservationDao.selectReservationDetail(reservationId);
	}
	
	@Override
	public List<MyProductDto> getMyReservationProducts(Long agentId) {
		return reservationDao.findMyReservationProducts(agentId);
	}

	@Override
	@Transactional
	public void processAutoCancel(ReservationDto reservation) {
		String userKey = reservation.getUserPaymentKey();
		connPgServerCancel(userKey);

		reservationDao.reservationAccept(reservation.getReservationId(), "", ReservationStatus.QUIT);
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.AVAILABLE);

		// 6. 예약자에게 알람 발송
		String userEmail = userDao.findEmailById(reservation.getUserId());
		if (userEmail != null && !userEmail.isEmpty()) {
			notificationService.sendEmail(userEmail, "[방방봐] 시간 초과로 예약이 취소되었습니다.", "중개인이 1시간 내에 응답하지 않았습니다.");
		}
	}

	@Override
	@Transactional
	public void processAutoPunishment(ReservationDto reservation) {
		Long offenderId = 0L;

		if (reservation.getAgentConfirmed().equals("N")) {
			offenderId = reservation.getAgentId();
		} else if (reservation.getUserConfirmed().equals("N")) {
			offenderId = reservation.getUserId();
		} else {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		String victimKey = ""; // 피해자
		String offenderKey = ""; // 가해자

		if (offenderId.equals(reservation.getAgentId())) {
			offenderKey = reservation.getAgentPaymentKey();
			victimKey = reservation.getUserPaymentKey();
		} else {
			offenderKey = reservation.getUserPaymentKey();
			victimKey = reservation.getAgentPaymentKey();
		}

		// 1. 가해자 위약금 몰수 (10,000원)
		connPgServerCapture(offenderKey, 10000L, PaymentStatus.PAID);

		// 2. 피해자 환불 (0원)
		connPgServerCancel(victimKey);

		// 3. 상태 변경 (NO_SHOW)
		reservationDao.updateStatus(reservation.getReservationId(), ReservationStatus.NO_SHOW);
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.AVAILABLE);

		// 4. 알림 발송 (가해자에게 통보)
		String offenderEmail = userDao.findEmailById(offenderId);
		notificationService.sendEmail(offenderEmail, "[방방봐] 노쇼 처벌이 집행되었습니다.", "이의제기 시간이 경과하여 보증금이 몰수되었습니다.");
	}
	
	@Override
	public String getReservationMessage(Long reservationId, Long agentId) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		
		if(reservation.getAgentId() != agentId) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
		
		return reservation.getMessage();
	}

	private String connPgServerPreAuth(String orderId, Long amount, String accountToken) {
		// 3-1. 통신
		PgAuthRequestDto pgRequest = PgAuthRequestDto.builder()
											.orderId(orderId) // 주문번호
											.amount(amount) // 보증금
											.type("DEPOSIT") // 부분 결제 요청
											.accountToken(accountToken)
											.build();

		PgResponseDto pgResponse = pgClient.requestPreAuth(pgRequest);

		// 3-2. 통신 결과 확인
		if (!pgResponse.getResultCode().equals("SUCCESS")) {
			throw new CustomException(ErrorCode.PAYMENT_FAIL, pgResponse.getMessage());
		}

		// 3-3. 결제 키 추출
		Map<String, Object> data = (Map<String, Object>) pgResponse.getData();
		return (String) data.get("paymentKey");
	}

	private void connPgServerCapture(String paymentKey, Long fee, PaymentStatus status) {
		PgCaptureRequestDto userCapture = PgCaptureRequestDto.builder().paymentKey(paymentKey).captureAmount(fee)
				.build();

		PgResponseDto userResponse = pgClient.requestCapture(userCapture);
		if (!userResponse.getResultCode().equals("SUCCESS")) {
			throw new CustomException(ErrorCode.QUIT_PAYMENT);
		}

		Map<String, Object> data = ((Map<String, Object>) userResponse.getData());

		Map<String, Object> paymentInfo = new HashMap<>();
		paymentInfo.put("orderId", (String) data.get("orderId"));
		paymentInfo.put("amount", fee);
		paymentInfo.put("approvedAt", (String) data.get("paidAt"));
		paymentInfo.put("status", status);

		log.info("receipt: {}", data);
		log.info("paymentInfo: {}", paymentInfo);

		paymentDao.paymentFee(paymentInfo);
	}

	private void connPgServerCancel(String paymentKey) {
		pgClient.requestCancel(Map.of("paymentKey", paymentKey));

		Map<String, Object> cancleInfo = new HashMap<>();
		cancleInfo.put("orderId", paymentDao.findOrderIdByPaymentKey(paymentKey));
		cancleInfo.put("amount", 0L);
		cancleInfo.put("approvedAt", LocalDateTime.now());
		cancleInfo.put("status", PaymentStatus.CANCELED);

		paymentDao.paymentFee(cancleInfo);
	}
}
