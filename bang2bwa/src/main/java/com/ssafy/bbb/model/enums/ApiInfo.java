package com.ssafy.bbb.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiInfo {

	private final String baseUrl;
	private final boolean useEncodingKey;

}
