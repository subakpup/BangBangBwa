package com.ssafy.bbb.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ssafy.bbb.global.jwt.JwtAuthenticationFilter;
import com.ssafy.bbb.global.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // CSRF 끄기 (Rest API는 안 씀)
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용 (밑에 만든 설정 사용)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 끄기 (우린 토큰이나 Stateless 쓸 거니까)
			.formLogin(login -> login.disable()) // 기본 로그인 폼 끄기 (팝업 뜨는 거 방지)
			.httpBasic(basic -> basic.disable())
			// 요청 권한 설정 (여기가 핵심!)
			.authorizeHttpRequests(auth -> auth 
					.requestMatchers("/products/search", "/products/**").permitAll() // 로그인 없이도 들어갈 수 있는 곳들 (검색, 상세 조회)
					.requestMatchers("/users/signup", "users/login", "users/refresh").permitAll() // 회원가입, 로그인, 토큰 재발급은 모두에게 허용
					.requestMatchers("/users/email-verification/**").permitAll() // 이메일 체크 관련 모두에게 허용
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger 관련 설정도 모두에게 허용
					.requestMatchers("/reservations/**").permitAll() // 개발중에 잠시 열어둠.
					.anyRequest().authenticated()) // 나머지는 다 로그인 해야 함
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
		
		return http.build();
	}

	// CORS 설정을 위한 Bean 등록
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
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
	
	// 비밀번호 암호화를 위한 Bean 등록
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
