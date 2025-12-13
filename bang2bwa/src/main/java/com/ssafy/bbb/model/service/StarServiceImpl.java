package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.StarDao;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.StarDongDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StarServiceImpl implements StarService {
	private final StarDao starDao;
	
	// === 매물 관련 ===
	@Override
	@Transactional
	public void addStarProduct(Long userId, Long productId) {
		if(starDao.existsStarProdct(userId, productId) > 0) {
			throw new CustomException(ErrorCode.ALERDAY_BOOKMARK);
		}
		
		starDao.saveStarProduct(userId, productId);
	}
	
	@Override
	@Transactional
	public void removeStarProduct(Long userId, Long productId) {
		starDao.deleteStarProduct(userId, productId);
	}
	
	@Override
	public List<ProductDto> getAllStarProducts(Long userId) {
		return starDao.findAllStarProductByUserId(userId);
	}
	
	
	// === 동네 관련 ===
	@Override
	@Transactional
	public void addStarDong(Long userId, String sggNm, String umdNm) {
		if(starDao.existsStarDong(userId, sggNm, umdNm) > 0) {
			throw new CustomException(ErrorCode.ALERDAY_BOOKMARK);
		}
		
		starDao.saveStarDong(userId, sggNm, umdNm);
	}
	
	@Override
	@Transactional
	public void removeStarDong(Long userId, String sggNm, String umdNm) {
		starDao.deleteStarDong(userId, sggNm, umdNm);
	}
	
	@Override
	public List<StarDongDto> getAllStarDongs(Long userId) {
		return starDao.findAllStarDongByUserId(userId);
	}
}
