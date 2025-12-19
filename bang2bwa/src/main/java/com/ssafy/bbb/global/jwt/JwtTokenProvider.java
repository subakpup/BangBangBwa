package com.ssafy.bbb.global.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.TokenInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
	private final SecretKey key;
	private final long accessExpiration;
	private final long refreshExpiration;
	
	public JwtTokenProvider(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-expiration}") long accessExpiration,
			@Value("${jwt.refresh-expiration}") long refreshExpiration) {
		
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessExpiration = accessExpiration;
		this.refreshExpiration = refreshExpiration;
	}
	
	// 토큰 생성 메서드
	public TokenInfo createToken(Authentication authentication) {
		// Principal을 CustomUserDetails로 캐스팅
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		// 권한 가져오기 (ROLE_USER, ROLE_AGENT)
		String authorities = authentication.getAuthorities().stream()
											.map(GrantedAuthority::getAuthority)
											.collect(Collectors.joining(","));
		
		long now = (new Date()).getTime();
		
		// Access Token 생성(1시간)
		Date accessTokenExpiresIn = new Date(now + accessExpiration);
		String accessToken = Jwts.builder()
									.subject(authentication.getName())
									.claim("userId", userDetails.getUserId())
									.claim("name", userDetails.getName())
									.claim("auth", authorities)
									.expiration(accessTokenExpiresIn)
									.signWith(key)
									.compact();
		// Refresh Token 생성(1일)
		Date refreshTokenExpiresIn = new Date(now + refreshExpiration);
		String refreshToken = Jwts.builder()
									.subject(authentication.getName())
									.expiration(refreshTokenExpiresIn)
									.signWith(key)
									.compact();
		
		return TokenInfo.builder()
						.grantType("Bearer")
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();
	}
	
	// 인증 정보 조회
	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);
		
		if(claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}
		
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
																	.map(SimpleGrantedAuthority::new)
																	.collect(Collectors.toList());
		
		Long userId = claims.get("userId", Long.class);
		String email = claims.getSubject();
		String name = claims.get("name", String.class);
		
		CustomUserDetails principal = new CustomUserDetails(userId, email, name, (List<GrantedAuthority>) authorities);
		
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}
	
	// 토큰 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch(ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch(UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch(IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		} catch(Exception e) {
			log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
		}
		
		return false;
	}
	
	
	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
		} catch(ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}
