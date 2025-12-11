package com.ssafy.bbb.global.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.ssafy.bbb.model.dto.user.UserDto;

import lombok.Getter;

@Getter
public class CustomUserDetails extends User {
	private final Long userId;
	
	// 로그인 시 DB에서 가져온 UserDto로 생성하는 생성자
	public CustomUserDetails(UserDto userDto) {
		super(userDto.getEmail(), userDto.getPassword(),
				Collections.singleton(new SimpleGrantedAuthority(userDto.getRole().toString())));
		
		this.userId = userDto.getUserId();
	}
	
	// 토큰 파싱 후 Authentication 객체를 만들 때 사용하는 생성자 (이미 인증된 상태)
	public CustomUserDetails(Long userId, String email, List<GrantedAuthority> authorities) {
		super(email, "", authorities);
        this.userId = userId;
	}
}
