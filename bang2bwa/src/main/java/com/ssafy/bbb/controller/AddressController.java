package com.ssafy.bbb.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.constant.SggData;
import com.ssafy.bbb.global.response.ApiResponse;

@RestController
@RequestMapping("/address")
public class AddressController {
	
	@GetMapping
	public ApiResponse<List<Map<String, Object>>> handleRequest(@RequestParam String action
												 , @RequestParam(required = false) String sidoCode
												 , @RequestParam(required = false) String SggCode) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		if (action.equals("sido")) {
			result = getSidoList();
		}
		
		if (action.equals("sgg")) {
			result = getSggList(sidoCode);
		}
		
		return ApiResponse.success(result, "행정구역 조회에 성공하였습니다.");
	}

	private List<Map<String, Object>> getSidoList() {
		List<Map<String, Object>> list = new ArrayList<>();
		
		for (String key : SggData.sggMap.keySet()) {
			if (key.endsWith("000")) {
				Map<String, Object> map = new HashMap<>();
				map.put("sidoCode", key);
				map.put("sidoName", SggData.sggMap.get(key));
				list.add(map);
			}
		}
		
		return list;
	}
	
	private List<Map<String, Object>> getSggList(String sidoCode) {
		List<Map<String, Object>> list = new ArrayList<>();
		
		if (sidoCode == null || sidoCode.length() < 2) return list;
		
		String prefix = sidoCode.substring(0, 2);
		String sidoFullName = SggData.sggMap.get(sidoCode);
		
		for (String key : SggData.sggMap.keySet()) {
			if (key.startsWith(prefix) && !key.equals(sidoCode)) {
				String fullName = SggData.sggMap.get(key);
				
				String gugunName = fullName;
				if (sidoFullName != null) {
					gugunName = fullName.replace(sidoFullName + " ", "");
				}
				
				Map<String, Object> map = new HashMap<>();
				map.put("gugunCode", key);
				map.put("gugunName", gugunName);
				list.add(map);
			}
		}
		
		list.sort(Comparator.comparing(m -> (String) m.get("gugunName")));
		return list;
	}
}
