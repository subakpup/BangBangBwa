package com.ssafy.bbb.model.dao;

import org.apache.ibatis.annotations.Param;

import com.ssafy.bbb.model.dto.ProductDto;

public interface ProductDao {
	public Long save(ProductDto product);

	public Integer update(@Param("id") Long productId, @Param("product") ProductDto product);

	public ProductDto findById(Long productId);
}
