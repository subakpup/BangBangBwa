package com.ssafy.bbb.model.enums;

// 매물의 예약 상태를 알려주는 enum class 입니다.
public enum ReservationStatus {
	SOLD_OUT, // 계약 확정
	QUIT, // 예약 종료
	NO_SHOW, // 노쇼로 인한 예약 종료
	RESERVED, // 예약 확정
	PENDING, // 예약 대기 중
	AVAILABLE // 예약 가능
}
