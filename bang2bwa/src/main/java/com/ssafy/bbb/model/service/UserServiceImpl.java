package com.ssafy.bbb.model.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.global.jwt.JwtTokenProvider;
import com.ssafy.bbb.model.dao.RefreshTokenDao;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserDto;
import com.ssafy.bbb.model.enums.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenDao refreshTokenDao;
	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional
	public TokenInfo login(LoginRequestDto request) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
		
		try {
			// authenticate() 메서드가 실행될 때, loadUserByUsername() 실행
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
			
			refreshTokenDao.saveToken(request.getEmail(), tokenInfo.getRefreshToken());
			return tokenInfo;
			
		} catch(UsernameNotFoundException | BadCredentialsException e) {
			throw new CustomException(ErrorCode.LOGIN_FAIL);
		} catch(AuthenticationException e) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
	}
	
	@Override
	@Transactional
	public TokenInfo refresh(TokenInfo oldToken) {
		if(!jwtTokenProvider.validateToken(oldToken.getRefreshToken())) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
		
		Authentication authentication = jwtTokenProvider.getAuthentication(oldToken.getAccessToken());
		
		String savedToken = refreshTokenDao.getToken(authentication.getName());
		
		if(savedToken == null || !savedToken.equals(oldToken.getRefreshToken())) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		}
		
		TokenInfo newToken = jwtTokenProvider.createToken(authentication);
		
		refreshTokenDao.saveToken(authentication.getName(), newToken.getRefreshToken());
		return newToken;
	}
	
	@Override
	@Transactional
	public Long signup(SignupRequestDto request) {
		// email 중복 더블체크
		checkEmailDuplicate(request.getEmail());
		
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		
		UserDto userDto = UserDto.builder()
									.email(request.getEmail())
									.password(encodedPassword)
									.name(request.getName())
									.phone(request.getPhone())
									.birth(request.getBirth())
									.role(request.getRole())
									.build();
		
		userDao.save(userDto);
		
		if(request.getRole() == Role.ROLE_AGENT) {
			userDao.saveAgent(userDto.getUserId()
							, request.getCeoName()
							, request.getRealtorAgency()
							, request.getBusinessNumber());
		}
		
		return userDto.getUserId();
	}
	
	@Override
	public void checkEmailDuplicate(String email) {
		if(userDao.existsByEmail(email) > 0) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
	}
}
