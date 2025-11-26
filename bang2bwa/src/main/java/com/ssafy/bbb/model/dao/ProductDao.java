package com.ssafy.bbb.model.dao;

import com.ssafy.bbb.model.dto.ProductDto;

public interface ProductDao {
	public Long save(ProductDto product);
}
