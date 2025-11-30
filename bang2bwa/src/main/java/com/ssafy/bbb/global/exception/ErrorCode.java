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

	// 2. 유저 관련 에러
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."), DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
	PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

	// 3. 매물 관련 에러
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 매물입니다."),

	// 4. 파일 관련 에러
	FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생하였습니다."),
	FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생하였습니다.");

	// 필요한 에러 추가..

	private final HttpStatus status;
	private final String message;
}
