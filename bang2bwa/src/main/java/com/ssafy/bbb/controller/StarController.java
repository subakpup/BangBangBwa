package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.controller.docs.StarControllerDocs;
import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.StarDongDto;
import com.ssafy.bbb.model.service.StarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stars")
@RequiredArgsConstructor
public class StarController implements StarControllerDocs {
	private final StarService starService;
	
	@Override
	@PostMapping("/products/{productId}")
	public ApiResponse<String> addStarProduct(@AuthenticationPrincipal CustomUserDetails user
											, @PathVariable Long productId) {
		
		starService.addStarProduct(user.getUserId(), productId);
		return ApiResponse.successWithNoContent("관심 매물로 등록하였습니다.");
	}
	
	@Override
	@DeleteMapping("/products/{productId}")
	public ApiResponse<String> removeStarProduct(@AuthenticationPrincipal CustomUserDetails user
												, @PathVariable Long productId) {

		starService.removeStarProduct(user.getUserId(), productId);
		return ApiResponse.successWithNoContent("관심 매물에서 삭제하였습니다.");
	}
	
	@Override
	@GetMapping("/products")
	public ApiResponse<List<ProductDto>> getMyStarProducts(@AuthenticationPrincipal CustomUserDetails user) {
		
		List<ProductDto> myStarProducts = starService.getAllStarProducts(user.getUserId());
		return ApiResponse.success(myStarProducts);
	}
	
	@Override
	@PostMapping("/dongs")
	public ApiResponse<String> addStarDong(@AuthenticationPrincipal CustomUserDetails user
										, @RequestBody StarDongDto request) {
		
		starService.addStarDong(user.getUserId(), request.getSggNm(), request.getUmdNm());
		return ApiResponse.successWithNoContent("관심 동네에 추가하였습니다.");
	}
	
	@Override
	@DeleteMapping("/dongs")
	public ApiResponse<String> removeStarDong(@AuthenticationPrincipal CustomUserDetails user
											, @RequestBody StarDongDto request) {
		
		starService.removeStarDong(user.getUserId(), request.getSggNm(), request.getUmdNm());
		return ApiResponse.successWithNoContent("관심 동네에서 해제하였습니다.");
	}
	
	@Override
	@GetMapping("/dongs")
	public ApiResponse<List<StarDongDto>> getMyStarDongs(@AuthenticationPrincipal CustomUserDetails user) {
		
		List<StarDongDto> myStarDongs = starService.getAllStarDongs(user.getUserId());
		return ApiResponse.success(myStarDongs);
	}
}
