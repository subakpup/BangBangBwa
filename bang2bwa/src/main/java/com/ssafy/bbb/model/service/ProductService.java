package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.model.dto.MapResponseDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductSearchDto;

public interface ProductService {
	public Long create(ProductDto product, List<MultipartFile> files);

	public ProductDto modify(Long productId, ProductDto product, List<MultipartFile> newFiles);

	public List<ProductDto> search(ProductSearchDto request);

	public List<MapResponseDto> findAllMarkers();
}
