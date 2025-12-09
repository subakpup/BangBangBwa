package com.ssafy.bbb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenInfo {
	private String grantType; // Bearer
	private String accessToken;
	private String refreshToken;
}
