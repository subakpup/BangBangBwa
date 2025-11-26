package com.ssafy.bbb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class ApiConfig {

	@Value("${api.public-data.encoding-key}")
	private String encodingKey;

	@Value("${api.public-data.decoding-key}")
	private String decodingKey;

	// 상황에 맞는 키를 꺼내주는 헬퍼 메서드
	public String getKey(boolean isEncodingNeeded) {
		return isEncodingNeeded ? encodingKey : decodingKey;
	}
}