package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductSearchDto;
import com.ssafy.bbb.model.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

	/**
	 * 매물 등록 Content-Type: multipart/form-data
	 */
	@PostMapping
	public ApiResponse<Long> createProduct(@RequestPart(value = "product") ProductDto createForm,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		Long prouctId = productService.create(createForm, images);

		return ApiResponse.success(prouctId, "매물 등록이 성공하였습니다.");
	}

	/**
	 * 매물 수정 Content-Type: multipart/form-data
	 */
	@PutMapping("/{productId}")
	public ApiResponse<ProductDto> updateProduct(@PathVariable Long productId,
			@RequestPart(value = "product") ProductDto modifyForm,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		ProductDto product = productService.modify(productId, modifyForm, images);

		return ApiResponse.success(product, "매물 수정이 완료되었습니다.");
	}

	/**
	 * 매물 검색 요청 URL: GET /products/search?keyword=검색어
	 */
	@PostMapping("/search")
	public ApiResponse<List<ProductDto>> searchProduct(@RequestBody ProductSearchDto request) {

		log.info("매물 검색 요청: {}", request);

		List<ProductDto> searchList = productService.search(request);

		return ApiResponse.success(searchList, "매물 조회가 완료되었습니다.");
	}
}
