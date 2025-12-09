package com.ssafy.bbb.model.dto.kakao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoGeoResponse {
	private List<Document> documents;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Document {
		@JsonProperty("x")
		private String x; // 경도(longitude)

		@JsonProperty("y")
		private String y; // 위도(latitude)
	}
}
