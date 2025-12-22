package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dto.AiSearchDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.service.AiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;
	
	@PostMapping("/recommend")
    public ApiResponse<List<ProductDto>> recommend(@RequestBody AiSearchDto request) {
        
        List<ProductDto> results = aiService.recommend(request);
        
        return ApiResponse.success(results);
    }
}
