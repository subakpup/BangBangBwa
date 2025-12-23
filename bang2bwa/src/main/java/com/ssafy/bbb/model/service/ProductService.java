package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.model.dto.MapResponseDto;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductSearchDto;

public interface ProductService {
	public Long create(Long agentId, ProductDto product, List<MultipartFile> files);

	public ProductDto modify(Long productId, Long agentId, ProductDto product, List<MultipartFile> newFiles);
	
	public void delete(Long productId, Long agentId);

	public List<ProductDto> search(ProductSearchDto request);

	public List<MapResponseDto> findAllMarkers();
	
	public ProductDto findProduct(Long productId);
	
	public List<MyProductDto> findProductList(Long agentId);
}
