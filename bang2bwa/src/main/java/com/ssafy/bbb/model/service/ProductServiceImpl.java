package com.ssafy.bbb.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.ProductDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductDao productDao;

	@Override
	@Transactional
	public Long create(ProductDto product) {
		productDao.save(product);
		return product.getProductId();
	}

	@Override
	@Transactional
	public ProductDto modify(Long productId, ProductDto product) {
		// 1. 수정
		productDao.update(productId, product);
		// 2. 조회
		ProductDto modifiedProduct = productDao.findById(productId);

		if (modifiedProduct == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		// 3. 반환
		return modifiedProduct;
	}
}
