package com.ssafy.bbb.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.bbb.model.dto.kakao.KakaoGeoResponse;

@FeignClient(name = "kakao-client", url = "https://dapi.kakao.com")
public interface KakaoClient {

	@GetMapping("/v2/local/search/address.json")
	KakaoGeoResponse searchAddress(@RequestParam("query") String address,
			@RequestHeader("Authorization") String apiKey);
}
