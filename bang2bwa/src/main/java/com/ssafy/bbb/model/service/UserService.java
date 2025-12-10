package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;

public interface UserService {
	// 로그인
	public TokenInfo login(LoginRequestDto request);
	
	// 토큰 재발급
	public TokenInfo refresh(TokenInfo oldToken);
	
	// 회원 가입
	public Long signup(SignupRequestDto request);
	
	// 이메일 중복 체크
	public void checkEmailDuplicate(String email);
}
