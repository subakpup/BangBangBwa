package com.ssafy.bbb.model.dao;

import com.ssafy.bbb.model.dto.HouseDealDto;
import com.ssafy.bbb.model.dto.HouseInfoDto;

public interface HouseDao {
	// 아파트 정보 조회
	public HouseInfoDto selectHouseInfo(HouseInfoDto houseInfoDto);

	// 아파트 정보 등록
	public Integer insertHouseInfo(HouseInfoDto houseInfoDto);
	
	// 거래 정보 등록
	public Integer insertHouseDeal(HouseDealDto houseDealDto);
}
