package com.ssafy.bbb.model.dao;

import org.apache.ibatis.annotations.Param;

public interface RefreshTokenDao {
	
	// 토큰 저장 (있다면 update, 없다면 insert)
	public void saveToken(@Param("userEmail") String userEmail, @Param("refreshToken") String refreshToken);
	
	// 토큰 조회
	public String getToken(String userEmail);
	
	// 토큰 삭제
	public void deleteToken(String userEmail);
}
