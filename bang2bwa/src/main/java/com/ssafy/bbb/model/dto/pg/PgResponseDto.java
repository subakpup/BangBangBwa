package com.ssafy.bbb.model.dto.pg;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgResponseDto {
	private String resultCode; // 응답 코드
	private String message; // 응답 메세지
	private Map<String, Object> data; // 실제 데이터(ex. 영수증)
}
