package com.ssafy.bbb.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder
				.defaultSystem("당신은 '방방봐'의 똑똑한 AI 부동산 파트너입니다. 한국어로 대답하세요.")
				.build();
	}
}
