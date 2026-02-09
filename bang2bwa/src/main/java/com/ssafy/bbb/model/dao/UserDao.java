package com.ssafy.bbb.model.dao;

import java.util.Optional;

import com.ssafy.bbb.model.dto.user.UserDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;
import com.ssafy.bbb.model.enums.Role;

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
	
	// 내 정보 조회
	public Optional<UserInfoDto> findUserInfoById(Long userId);
	
	// 회원 유형(일반회원, 사업자회원) 찾기
	public Role findRoleById(Long userId);
	
	// 내 정보 수정
	public void updateUser(Long userId, UserUpdateDto updateDto);
	// 내 정보 수정 (사업자 회원 추가 수정)
	public void updateAgent(Long agentId, UserUpdateDto updateDto);
	
	// PK 로 password 가져오기
	public String findPasswordById(Long userId);
	// 비밀번호 수정
	public void updatePassword(Long userId, String encodedPassword);
	
	// 회원 탈퇴 (사업자 회원은 on delete cascade 로 DB 단에서 삭제)
	public void deleteUser(Long userId);
	
	public void updatePasswordByEmail(String email, String encodedPassword);
}
