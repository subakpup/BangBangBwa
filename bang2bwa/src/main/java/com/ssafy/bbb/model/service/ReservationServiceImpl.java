package com.ssafy.bbb.model.service;

import java.time.LocalDateTime;
import java.util.HashMap;
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
import com.ssafy.bbb.model.dto.PaymentDto;
import com.ssafy.bbb.model.dto.ReservationDto;
import com.ssafy.bbb.model.dto.ReservationRequestDto;
import com.ssafy.bbb.model.dto.pg.PgAuthRequestDto;
import com.ssafy.bbb.model.dto.pg.PgCaptureRequestDto;
import com.ssafy.bbb.model.dto.pg.PgResponseDto;
import com.ssafy.bbb.model.enums.PaymentStatus;
import com.ssafy.bbb.model.enums.PaymentType;
import com.ssafy.bbb.model.enums.ReservationStatus;

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
	private final PgClient pgClient;

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

		// 3. PG 서버 통신: 가승인 요청
		String orderId = "ORD_USER_" + UUID.randomUUID().toString();
		Long amount = 10000L;
		String paymentKey = connPgServerPreAuth(orderId, amount);

		// 4. 예약 정보 저장
		ReservationDto reservation = ReservationDto.builder() //
				.agentId((Long) productInfo.get("agentId")) // 중개사 pk
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

		// 8. 매물 상태 변경
		productDao.updateProductStatus(request.getProductId(), ReservationStatus.PENDING);
	}

	// 중개업자 예약 승인
	@Override
	@Transactional
	public void acceptReservation(Long reservationId, Long agentId) {
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

		// 3. PG 서버 통신: 가승인 요청
		String orderId = "ORD_AGENT_" + UUID.randomUUID().toString();
		Long amount = 10000L;
		String paymentKey = connPgServerPreAuth(orderId, amount);

		// 4. 예약 테이블 업데이트
		reservationDao.reservationAccept(reservationId, paymentKey, ReservationStatus.RESERVED);

		// 5. 결제 이력 저장
		PaymentDto paymentLog = PaymentDto.builder() //
				.pgPaymentKey(paymentKey) // 결제 번호(pg 서버가 준 키)
				.orderId(orderId) // 주문번호(메인 서버의 키)
				.amount(amount) // 금액
				.status(PaymentStatus.AUTHORIZED) // 결제 정보 -> 가승인
				.paymentType(PaymentType.DEPOSIT) // 결제 타입 -> 보증금
				.reservationId(reservationId) // 예약 ID 연결
				.approvedAt(LocalDateTime.now()) // 결제 시각 저장
				.userId(agentId) //
				.build();
		paymentDao.paymentSave(paymentLog);

		// 6. 매물 상태 업데이트
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.RESERVED);
	}

	// 만남 성사
	@Override
	@Transactional
	public void confirmMeeting(Long reservationId, Long userId) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
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
		reservationDao.updateConfirmation(userType, reservationId);

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
	public void reportNoShow(Long reservationId, Long reporterId) {
		ReservationDto reservation = reservationDao.findById(reservationId);
		if (reservation == null) {
			throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 노쇼 신고는 예약 시간이 지나야만 가능함.
		if (reservation.getVisitDate().isAfter(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.FAIL_REPORT);
		}

		// 피해자
		String victimType = "";
		String victimKey = "";
		// 가해자
		String offenderType = "";
		String offenderKey = "";

		if (reporterId == reservation.getUserId()) { // 중개인이 노쇼한 경우
			victimType = "USER";
			victimKey = reservation.getUserPaymentKey();
			offenderType = "AGNENT";
			offenderKey = reservation.getAgentPaymentKey();
		} else if (reporterId == reservation.getAgentId()) { // 예약자가 노쇼한 경우
			victimType = "AGNENT";
			victimKey = reservation.getAgentPaymentKey();
			offenderType = "USER";
			offenderKey = reservation.getUserPaymentKey();
		} else { // userId, agentId와 둘다 다르다면, 예외를 반환
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		// 가해자 위약금 몰수
		connPgServerCapture(offenderKey, 10000L, PaymentStatus.PAID);

		// 피해자는 수수료 없이 전액 반환.
		pgClient.requestCancel(Map.of("paymentKey", victimKey));
		Map<String, Object> cancleInfo = new HashMap<>();
		cancleInfo.put("orderId", paymentDao.findOrderIdByPaymentKey(victimKey));
		cancleInfo.put("amount", 0L);
		cancleInfo.put("approvedAt", LocalDateTime.now());
		cancleInfo.put("status", PaymentStatus.CANCELED);
		paymentDao.paymentFee(cancleInfo);

		// 노쇼로 인한 예약 종료
		reservationDao.updateStatus(reservationId, ReservationStatus.NO_SHOW);
		// 다시 매물을 예약 가능 상태로 변경.
		productDao.updateProductStatus(reservation.getProductId(), ReservationStatus.AVAILABLE);
	}

	private String connPgServerPreAuth(String orderId, Long amount) {
		// 3-1. 통신
		PgAuthRequestDto pgRequest = PgAuthRequestDto.builder() //
				.orderId(orderId) // 주문번호
				.amount(amount) // 보증금
				.type("DEPOSIT") // 부분 결제 요청
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
}
