package com.ssafy.bbb.model.dao;

import java.util.List;

import com.ssafy.bbb.model.dto.VirtualBankDto;

public interface VirtualBankDao {
	VirtualBankDto findById(Long bankId);
	
	List<VirtualBankDto> findAll();
}
