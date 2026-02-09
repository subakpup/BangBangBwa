package com.ssafy.bbb.model.service;

import java.util.List;

import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.StarDongDto;

public interface StarService {
	// === 매물 관련 ===
	// 매물 찜 추가
	public void addStarProduct(Long userId, Long productId);
	
	// 매물 찜 해제
	public void removeStarProduct(Long userId, Long productId);

	// 내가 찜한 매물 보기
	public List<ProductDto> getAllStarProducts(Long userId);
	
	// === 동네 관련 ===
	// 동네 찜 추가
	public void addStarDong(Long userId, String sggNm, String umdNm);
	
	// 동네 찜 해제
	public void removeStarDong(Long userId, String sggNm, String umdNm);
	
	// 내가 찜한 동네 보기
	public List<StarDongDto> getAllStarDongs(Long userId);
}
