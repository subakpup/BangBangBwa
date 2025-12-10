package com.ssafy.bbb.model.dao;

import java.util.Optional;

import com.ssafy.bbb.model.dto.user.UserDto;

public interface UserDao {

	public String findEmailById(Long userId);
	
	// 회원가입 (유저 공통)
	public void save(UserDto userDto);
	
	// 회원가입 (사업자 회원 추가 정보)
	public void saveAgent(Long agentId, String ceoName, String realtorAgency, String businessNumber);
	
	// 로그인
	public Optional<UserDto> findByEmail(String email);
	
	// 이메일 중복 확인
	public int existsByEmail(String email);
}
