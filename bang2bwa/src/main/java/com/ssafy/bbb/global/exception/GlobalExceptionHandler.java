package com.ssafy.bbb.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ssafy.bbb.global.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 1. 우리가 직접 정의한 비지니스 예외 처리 (예시: throw new
	 * CustomException(ErrorCode.USER_NOT_FOUND);)
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
		log.error("CustomException Occured: {}", e.getErrorCode().getMessage());

		// ApiResponse.error()를 사용하여 통일된 포맷 반환
		return ResponseEntity.status(e.getErrorCode().getStatus())
				.body(ApiResponse.error(e.getErrorCode().getMessage()));
	}

	/**
	 * 2. 예상치 못한 시스템 예외 처리 (NullPointerException, SQLException 등) "서버 내부 에러"로 일반화시켜서
	 * 내보냄. (상세 로그는 서버에만 남기도록. (보안))
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Unhandled Exception Occurred: {}", e); // 스택 트레이스 로그 남기기

		return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
				.body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
	}
}
