package com.ssafy.bbb.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. CSRF 끄기 (Rest API는 안 씀)
				.csrf(csrf -> csrf.disable())

				// 2. CORS 설정 적용 (밑에 만든 설정 사용)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// 3. 세션 끄기 (우린 토큰이나 Stateless 쓸 거니까)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 4. 요청 권한 설정 (여기가 핵심!)
				.authorizeHttpRequests(auth -> auth
						// 로그인 없이도 들어갈 수 있는 곳들 (검색, 상세 조회)
						.requestMatchers("/products/search", "/products/**").permitAll()
						// Swagger 관련 설정도 모두에게 허용
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
						// 개발중에 잠시 열어둠.
						.requestMatchers("/reservations/**").permitAll()
						// 나머지는 다 로그인 해야 함
						.anyRequest().authenticated())

				// 5. 기본 로그인 폼 끄기 (팝업 뜨는 거 방지)
				.formLogin(login -> login.disable())
				.httpBasic(basic -> basic.disable());

		return http.build();
	}

	// CORS 설정을 위한 Bean 등록
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		// 프론트엔드 주소 허용 (이거 안 하면 Network Error 남!)
		config.setAllowedOrigins(List.of("http://localhost:5173"));

		// 모든 메서드 허용 (GET, POST, PUT, DELETE, OPTIONS)
		config.setAllowedMethods(List.of("*"));

		// 모든 헤더 허용
		config.setAllowedHeaders(List.of("*"));

		// 자격 증명(쿠키 등) 허용
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
