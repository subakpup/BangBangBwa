package com.ssafy.bbb.configuration;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {

	// 시스템 프롬프트 파일
    @Value("classpath:/prompts/system-prompt.txt")
    private Resource systemPrompt;

    /**
     * 1. RestClient 커스텀 설정
     * SSAFY GMS 등 일부 프록시 환경에서 청크 인코딩 문제 해결을 위해 버퍼링 팩토리 사용
     */
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    /**
     * 2. ChatClient 빈 등록 (핵심!)
     * - OpenAiChatModel은 application.properties 설정에 따라 Spring이 자동으로 주입해 줌
     * - 여기서 우리는 ChatClient라는 사용하기 편한 인터페이스를 조립하기만 하면 됨
     */
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(spec -> spec.text(systemPrompt)
                        .params(Map.of("name", "방방봐 AI"))) // 프롬프트 내 {name} 치환
                .build();
    }
}
