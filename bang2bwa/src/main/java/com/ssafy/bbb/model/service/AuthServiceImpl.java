package com.ssafy.bbb.model.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.user.UserDto;
import com.ssafy.bbb.util.RedisUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
	private final UserDao userDao;
	private final NotificationService notificationService;
	private final RedisUtil redisUtil;
	private final PasswordEncoder passwordEncoder;
	
	private final String RESET_PASSWORD_PREFIX = "reset-password:";
	private final int RESET_PASSWORD_EXPIRE = 600;
	
	@Override
	@Transactional
	public void requestPasswordReset(String email) {
		UserDto user = userDao.findByEmail(email)
							.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		
		String token = UUID.randomUUID().toString();
		
		redisUtil.setDataExpire(RESET_PASSWORD_PREFIX + token, email, RESET_PASSWORD_EXPIRE);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"ko\">");
		sb.append("<p> 비밀번호 변경 링크입니다. </p>");
		sb.append("<p> 10분 내로 접속 후 비밀번호를 변경해주세요 </p>");
		sb.append("<a href='http://localhost:5173/reset-password?token=").append(token).append("'> 비밀번호 변경하러 가기 </a>");
		sb.append("</html>");
		
		notificationService.sendHtmlEmail(email, "[방방봐] 비밀번호 변경 링크입니다.", sb.toString());
	}
	
	@Override
	@Transactional
	public void resetPassword(String token, String newPassword) {
		String email = redisUtil.getData(RESET_PASSWORD_PREFIX + token);
		if(email == null) {
			throw new CustomException(ErrorCode.INVALID_CODE);
		}
		
		String encodedPassword = passwordEncoder.encode(newPassword);
		userDao.updatePasswordByEmail(email, encodedPassword);
		
		redisUtil.deleteData(RESET_PASSWORD_PREFIX + token);
	}
}
