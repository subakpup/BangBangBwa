package com.ssafy.bbb.model.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.AiSearchDto;
import com.ssafy.bbb.model.dto.ProductDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

	private final ChatClient chatClient;
	private final ProductDao productDao;

	public List<ProductDto> recommend(AiSearchDto request) {
		log.info("검색 시작, 조건: {}", request);
		
		List<ProductDto> candidates = productDao.aiSearchProduct(request);
		
		if (candidates.isEmpty()) {
			log.info("조건에 맞는 매물 없음");
			return List.of();
		}
		
		log.info("1차 후보군 {}개 발견", candidates.size());
		
//		String candidatesText = candidates.stream()
//				.map(p -> String.format("", null));
		
	}
	
	public String chat(String message) {
		log.info("요청: {}", message);
		
		String response = chatClient.prompt()
							.user(message)
							.call()
							.content();
		
		log.info("응답 완료: {}", response);
		
		return response;
	}
}
