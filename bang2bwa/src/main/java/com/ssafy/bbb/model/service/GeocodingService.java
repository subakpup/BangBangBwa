package com.ssafy.bbb.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ssafy.bbb.client.KakaoClient;
import com.ssafy.bbb.model.dto.kakao.KakaoGeoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

	private final KakaoClient kakaoClient;

	@Value("${kakao.api.key}")
	private String kakaoKey;

	public double[] getCoordinate(String address) {
		try {
			String authorization = "KakaoAK " + kakaoKey;

			KakaoGeoResponse response = kakaoClient.searchAddress(address, authorization);

			if (response != null && !response.getDocuments().isEmpty()) {
				KakaoGeoResponse.Document doc = response.getDocuments().get(0);

				double longitude = Double.parseDouble(doc.getX());
				double latitude = Double.parseDouble(doc.getY());

				return new double[] { latitude, longitude };
			}
		} catch (Exception e) {
			log.error("주소 변환 실패(카카오 API 통신 오류): {}", e.getMessage());
		}

		return new double[] { 0.0, 0.0 };
	}
}
