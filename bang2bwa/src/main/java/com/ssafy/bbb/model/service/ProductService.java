package com.ssafy.bbb.model.service;

import com.ssafy.bbb.model.dto.ProductDto;

public interface ProductService {
	public Long create(ProductDto product);

	public ProductDto modify(Long productId, ProductDto product);
}
