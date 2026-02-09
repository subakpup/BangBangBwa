package com.ssafy.bbb.model.service;

import org.springframework.stereotype.Service;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	private final RedisUtil redisUtil;
	private final NotificationService notificationService;
	private final UserDao userDao;
	
	private static final long CODE_EXPIRATION = 60 * 5L; // 5 minute
	private static final String PREFIX_CODE = "auth_code:";
	private static final String PREFIX_VERIFIED = "verified:";
	
	// 인증 코드 발송
	public void sendCode(String email) {
		// 인증 코드 생성, redis에 저장
		String code = createRandomCode();
		redisUtil.setDataExpire(PREFIX_CODE + email, code, CODE_EXPIRATION);
		
		// 인증 코드 이메일 발송
		String subject = "[방방봐] 이메일을 인증하세요.";
		String text = "인증 코드: " + code + "\n\n5분 이내에 코드를 입력해주세요.";
		notificationService.sendEmail(email, subject, text);
	}
	
	// 인증 코드 검증
	public void verifyCode(String email, String code) {
		String redisKey = PREFIX_CODE + email;
		String savedCode = redisUtil.getData(redisKey);
		
		if(savedCode == null) {
			throw new CustomException(ErrorCode.EXPIRED_CODE);
		}
		
		if(!savedCode.equals(code)) {
			throw new CustomException(ErrorCode.INVALID_CODE);
		}
		
		// 인증 성공, 코드 삭제, 인증 되었음을 1시간 동안 저장
		redisUtil.deleteData(redisKey);
		redisUtil.setDataExpire(PREFIX_VERIFIED + email, "TRUE", 60 * 60L); 
	}
	
	// 인증 여부 확인
	public boolean isVerified(String email) {
		return redisUtil.hasKey(PREFIX_VERIFIED + email);
	}
	
	// 이메일 중복 체크
	public void checkEmailDuplicate(String email) {
		if(userDao.existsByEmail(email) > 0) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
	}
	
	private String createRandomCode() {
		return String.valueOf((int)(Math.random() * 900000) + 100000);
	}
}
