package com.ssafy.bbb.model.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

	private final ChatClient chatClient;
	
	public String chat(String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.content();
	}
}
