package com.ssafy.bbb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. CSRF 보안 끄기 (POST 요청 테스트를 위해 필수)
				.csrf(AbstractHttpConfigurer::disable)

				// 2. 모든 요청에 대해 인증(로그인) 요구
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())

				// 3. Postman 테스트를 위해 Basic Auth 사용
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}
}
