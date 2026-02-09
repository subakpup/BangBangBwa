package com.ssafy.bbb.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.ssafy.bbb.util.RedisUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// header 에서 token 추출
		String token = resolveToken((HttpServletRequest) request);
		
		// 토큰 유효성 검사
		if(token != null && jwtTokenProvider.validateToken(token)) {
			String blackList = redisUtil.getData("BL:" + token);
			
			if(blackList == null) {
				// 토큰이 유효하다면 인증 객체(Authentication) 생성하여 SEcurityContext에 저장
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				log.warn("This token is in BlackList");
			}
		}
		
		chain.doFilter(request, response);
	}
	
	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		
		return null;
	}
}
