package com.ssafy.bbb.model.service;

import java.util.List;

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
import com.ssafy.bbb.model.dao.ReservationDao;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;
import com.ssafy.bbb.model.enums.Role;
import com.ssafy.bbb.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisUtil redisUtil;
	
	private final EmailVerificationService emailVerificationService;
	private final UserDao userDao;
	private final ReservationDao reservationDao;
	
	private static final String REFRESH_PREFIX = "RT:";
	private static final String BLACKLIST_PREFIX = "BL:";
	private static final long REFRESH_EXPIRE_TIME = 86400L; // 1일
	
	@Override
	@Transactional
	public TokenInfo login(LoginRequestDto request) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
		
		try {
			// authenticate() 메서드가 실행될 때, loadUserByUsername() 실행
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
			
			redisUtil.setDataExpire(REFRESH_PREFIX + request.getEmail()
								, tokenInfo.getRefreshToken()
								, REFRESH_EXPIRE_TIME);
			
			return tokenInfo;
			
		} catch(UsernameNotFoundException | BadCredentialsException e) {
			throw new CustomException(ErrorCode.LOGIN_FAIL);
		} catch(AuthenticationException e) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
	}
	
	@Override
	@Transactional
	public void logout(String email, String accessToken) {
		redisUtil.deleteData(REFRESH_PREFIX + email);
		
		Long expiration = jwtTokenProvider.getRemainExpiration(accessToken);
		if(expiration > 0) {
			redisUtil.setDataExpire(BLACKLIST_PREFIX + accessToken, "Logout", expiration/1000);
		}
	}
	
	@Override
	@Transactional
	public TokenInfo refresh(TokenInfo oldToken) {
		if(!jwtTokenProvider.validateToken(oldToken.getRefreshToken())) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
		
		Authentication authentication = jwtTokenProvider.getAuthentication(oldToken.getAccessToken());
		
		String savedToken = redisUtil.getData(REFRESH_PREFIX + authentication.getName());
		
		if(savedToken == null || !savedToken.equals(oldToken.getRefreshToken())) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		}
		
		TokenInfo newToken = jwtTokenProvider.createToken(authentication);
		
		redisUtil.setDataExpire(REFRESH_PREFIX + authentication.getName()
								, newToken.getRefreshToken()
								, REFRESH_EXPIRE_TIME);
		return newToken;
	}
	
	@Override
	@Transactional
	public Long signup(SignupRequestDto request) {
		// email 중복 더블체크
		emailVerificationService.checkEmailDuplicate(request.getEmail());
		
		// 인증받은 email 인지 체크
		if(!emailVerificationService.isVerified(request.getEmail())) {
			throw new CustomException(ErrorCode.INVALID_EMAIL);
		}
		
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
	public UserInfoDto getUserInfo(Long userId) {
		UserInfoDto user = userDao.findUserInfoById(userId)
									.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		
		user.setReservation(reservationDao.findMyResrvationInfoByUserId(userId));
		
		return user;
	}
	
	@Override
	@Transactional
	public void updateUserInfo(Long userId, UserUpdateDto request) {
		Role role = userDao.findRoleById(userId);
		
		userDao.updateUser(userId, request);
		if(role == Role.ROLE_AGENT) {
			userDao.updateAgent(userId, request);
		}
	}
	
	@Override
	@Transactional
	public void updatePassword(Long userId, PasswordUpdateDto request) {
		String curPassword = userDao.findPasswordById(userId);
		
		if(!passwordEncoder.matches(request.getCurrentPassword(), curPassword)) {
			throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
		}
		
		userDao.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
	}
	
	@Override
	@Transactional
	public void withdraw(Long userId, String email, String accessToken) {
		redisUtil.deleteData(REFRESH_PREFIX + email);
		
		Long expiration = jwtTokenProvider.getRemainExpiration(accessToken);
		if(expiration > 0) {
			redisUtil.setDataExpire(BLACKLIST_PREFIX + accessToken, "Withdraw", expiration/1000);
		}
		
		userDao.deleteUser(userId);
	}
	
	@Override
	public List<MyProductDto> myProducts(Long agentId) {
		return reservationDao.findProductByAgentId(agentId);
	}
}
