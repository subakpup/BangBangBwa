package com.ssafy.bbb.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductImageDto;
import com.ssafy.bbb.model.enums.ReservationStatus;

public interface ProductDao {
	public Long save(ProductDto product);

	public Integer update(@Param("id") Long productId, @Param("product") ProductDto product);

	public ProductDto findById(Long productId);

	// 특정 매물의 이미지 정보 전체 조회
	public List<ProductImageDto> findImagesByProductId(Long productId);

	// 여러 이미지 저장
	public void saveImages(List<ProductImageDto> imageDtos);

	// 여러 이미지 삭제
	public void deleteImages(List<Long> imageIds);

	// 삭제할 이미지의 경로를 받아오기(실제 파일 삭제용)
	public List<String> findSavePathByIds(List<Long> imageIds);

	// 매물 예약을 위한 select for update 문 (for update를 통해 동시성 제어)
	public Map<String, Object> selectProductForUpdate(Long productId);

	// 매물 상태 변경
	public Long updateProductStatus(Long productId, ReservationStatus status);

}
