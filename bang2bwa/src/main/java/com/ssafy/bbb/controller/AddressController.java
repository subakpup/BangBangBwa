package com.ssafy.bbb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.constant.SggData;

@RestController
@RequestMapping("/address")
public class AddressController {
	
	@GetMapping
	public List<Map<String, Object>> handleRequest(@RequestParam String action
												 , @RequestParam(required = false) Integer sidoCode
												 , @RequestParam(required = false) Integer SggCode) {
		if (action.equals("sido")) {
			return getSidoList();
		}
		
		if (action.equals("sgg")) {
			return getSggList(sidoCode);
		}
		
		return new ArrayList<>();
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
		
		
	}
}
