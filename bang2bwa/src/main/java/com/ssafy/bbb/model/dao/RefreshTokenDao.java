package com.ssafy.bbb.model.dao;

import org.apache.ibatis.annotations.Param;

public interface RefreshTokenDao {
	
	// 저장 (있다면 update, 있다면 insert)
	public void saveToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);
	
	// 조회
	public String getToken(Long userId);
	
	// 삭제
	public void deleteToken(Long userId);
}
