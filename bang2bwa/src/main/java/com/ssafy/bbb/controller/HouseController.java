package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.service.HouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

	private final HouseService houseService;
	
	@GetMapping("/load")
	public ApiResponse<?> loadData(@RequestParam("code") String lawdCd, @RequestParam("date") String dealYmd) {
		houseService.fetchAndSaveHouseData(lawdCd, dealYmd);
		
		return ApiResponse.successWithNoContent("데이터 로딩에 성공했습니다.");
	}
}
