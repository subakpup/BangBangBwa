package com.ssafy.bbb.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

	@PostMapping
	public ApiResponse<Long> createProduct(@RequestBody ProductDto createForm) {
		Long prouctId = productService.create(createForm);

		return ApiResponse.success(prouctId, "매물 등록이 성공하였습니다.");
	}

	@PutMapping("/{productId}")
	public ApiResponse<ProductDto> updateProduct(@PathVariable Long productId, @RequestBody ProductDto modifyForm) {
		ProductDto product = productService.modify(productId, modifyForm);

		return ApiResponse.success(product, "매물 수정이 완료되었습니다.");
	}
}
