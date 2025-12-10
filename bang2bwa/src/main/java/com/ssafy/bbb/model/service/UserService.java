package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;

public interface UserService {
	// 로그인
	public TokenInfo login(LoginRequestDto request);
	
	// 로그아웃
	public void logout(String email);
	
	// 토큰 재발급
	public TokenInfo refresh(TokenInfo oldToken);
	
	// 회원 가입
	public Long signup(SignupRequestDto request);
	
	// 이메일 중복 체크
	public void checkEmailDuplicate(String email);
	
	// 내 정보 조회
	public UserInfoDto getUserInfo(Long userId);
	
	// 내 정보 수정
	public void updateUserInfo(Long userId, UserUpdateDto request);
	
	// 비밀번호 수정
	public void updatePassword(Long userId, PasswordUpdateDto request);
	
	// 회원 탈퇴
	public void withdraw(Long userId, String email);
}
