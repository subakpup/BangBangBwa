package com.ssafy.bbb.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.service.AiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;
	
	@PostMapping("/chat")
	public ApiResponse<String> chat(@RequestBody Map<String, String> request) {
		String userMessage = request.get("message");
		String response = aiService.chat(userMessage);
		
		return ApiResponse.success(response);
	}
}
