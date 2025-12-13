package com.ssafy.bbb.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 1. 공통 에러
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

	// 2. 유저, 토큰 관련 에러
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
	INVALID_EMAIL(HttpStatus.CONFLICT, "인증을 하지 않았거나, 인증시간이 끝난 이메일입니다. 다시 인증해주세요."),
	LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "이메일/비밀번호가 일치하지 않습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료 되었습니다. 다시 로그인 해주세요."),
	PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다. 다시 시도해주세요."),
	INVALID_CODE(HttpStatus.CONFLICT, "유효하지 않은 코드입니다."),
	EXPIRED_CODE(HttpStatus.CONFLICT, "코드가 만료되었습니다. 다시 인증해주세요."),

	// 3. 매물 관련 에러
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 매물입니다."),
	PRODUCT_NOT_AVAILABLE(HttpStatus.CONFLICT, "이미 예약 진행 중이거나 거래 완료된 매물입니다."),
	ALERDAY_BOOKMARK(HttpStatus.CONFLICT, "이미 찜한 매물/동네 입니다."),

	// 4. 파일 관련 에러
	FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생하였습니다."),
	FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생하였습니다."),

	// 5. 결제, 예약 관련 에러
	RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약정보입니다."),
	RESERVATION_UNAUTORIZATION(HttpStatus.FORBIDDEN, "자신의 예약에 대해서만 접근 가능합니다."),
	RESERVATION_NOT_PENDING(HttpStatus.CONFLICT, "예약 대기중인 매물이 아닙니다."),
	RESERVATION_NOT_RESERVED(HttpStatus.CONFLICT, "예약중인 매물이 아닙니다."),
	FAIL_REPORT(HttpStatus.BAD_REQUEST, "노쇼 신고는 예약 일정 이후에만 가능합니다."),
	NOT_REPROTED(HttpStatus.BAD_REQUEST, "신고된 상태가 아닙니다!"),
	FAR_DISTANCE(HttpStatus.BAD_REQUEST, "예약 매물과의 거리가 너무 멉니다."),
	QUIT_PAYMENT(HttpStatus.CONFLICT, "이미 정산된 거래입니다."),
	PAYMENT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "결제가 실패하였습니다.");

	// 필요한 에러 추가..

	private final HttpStatus status;
	private final String message;
}
