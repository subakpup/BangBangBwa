package com.ssafy.bbb.model.dao;

import java.util.List;

import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.StarDongDto;

public interface StarDao {
	// === 관심 매물 ===
	// 중복 체크
	int existsStarProdct(Long userId, Long productId);
	
	// 등록
	void saveStarProduct(Long userId, Long productId);
	
	// 삭제
	void deleteStarProduct(Long userId, Long productId);
	
	// 전체 조회
	List<ProductDto> findAllStarProductByUserId(Long userId);
	
	
	// === 관심 동네 ===
	// 중복 체크
	int existsStarDong(Long userId, String sggNm, String umdNm);
	
	// 등록
	void saveStarDong(Long userId, String sggNm, String umdNm);
	
	// 삭제
	void deleteStarDong(Long userId, String sggNm, String umdNm);
	
	// 전체 조회
	List<StarDongDto> findAllStarDongByUserId(Long userId);
}
