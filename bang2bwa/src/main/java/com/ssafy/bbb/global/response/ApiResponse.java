package com.ssafy.bbb.global.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

//생성자 private, static 메서드를 통해 생성하도록 유도 (Factory Method)
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	// 1. 응답 상태 (성공 여부, "SUCCESS", "FAIL")
	private final String success;

	// 2. 응답 메시지 (클라이언트에게 보여줄 알림 문구)
	private final String message;

	// 3. 실제 데이터 (제네릭으로 유연하게 처리)
	private final T data;

	// --- 성공 응답 생성 메서드 ---
	// 데이터가 있는 성공 응답 (예: 조회, 등록 성공)
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>("SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
	}

	// 메시지를 커스텀하는 성공 응답
	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>("SUCCESS", message, data);
	}

	// 데이터 없이 메세지만 주는 성공 응답 (예: 삭제 성공)
	public static <T> ApiResponse<T> successWithNoContent(String message) {
		return new ApiResponse<>("SUCCESS", message, null);
	}

	// --- 실패 응답 생성 메서드 ---
	// 기본 실패 응답
	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>("FAIL", message, null);
	}

	// 데이터도 같이 보내는 실패 응답
	public static <T> ApiResponse<T> error(T data, String message) {
		return new ApiResponse<>("FAIL", message, data);
	}
}
