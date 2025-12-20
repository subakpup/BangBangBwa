package com.ssafy.bbb.controller.docs;

import java.util.List;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.StarDongDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "찜(Favorites) API", description = "관심 매물 및 관심 동네 관리")
public interface StarControllerDocs {
	// === 관심 매물 ===
	@Operation(summary = "관심 매물 등록")
	public ApiResponse<String> addStarProduct(@Parameter(hidden=true) CustomUserDetails user, Long productId);
	
	@Operation(summary = "관심 매물 해제")
	public ApiResponse<String> removeStarProduct(@Parameter(hidden=true) CustomUserDetails user, Long productId);
	
	@Operation(summary = "내 관심 매물 목록 조회")
	public ApiResponse<List<ProductDto>> getMyStarProducts(@Parameter(hidden=true) CustomUserDetails user);
	
	// === 관심 동네 ===
	@Operation(summary = "관심 동네 등록")
	public ApiResponse<String> addStarDong(@Parameter(hidden=true) CustomUserDetails user, StarDongDto request);
	
	@Operation(summary = "관심 동네 해제")
	public ApiResponse<String> removeStarDong(@Parameter(hidden=true) CustomUserDetails user, StarDongDto request);
	
	@Operation(summary = "내 관심 동네 목록 조회")
	public ApiResponse<List<StarDongDto>> getMyStarDongs(@Parameter(hidden=true) CustomUserDetails user);
}
