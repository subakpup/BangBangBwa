package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.HouseDealDto;
import com.ssafy.bbb.model.dto.HouseInfoDto;

public interface HouseService {
	// 외부 데이터를 파싱해서 한 번에 등록하는 메서드
	void registHouseDeal(HouseInfoDto infoDto, HouseDealDto dealDto);
	
	// API 호출 및 데이터 저장
	public void fetchAndSaveHouseData(String lawdCd, String dealYmd);
}
