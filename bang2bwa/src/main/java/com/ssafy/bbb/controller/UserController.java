package com.ssafy.bbb.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.controller.docs.UserControllerDocs;
import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.service.EmailVerificationService;
import com.ssafy.bbb.model.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
	private final UserService userService;
	private final EmailVerificationService emailVerificationService;

	@Override
	@PostMapping("/signup")
	public ApiResponse<Long> signup(@RequestBody SignupRequestDto request) {
		Long userId = userService.signup(request);
		
		return ApiResponse.success(userId, "회원가입이 완료되었습니다.");
	}
	
	@Override
	@PostMapping("/login")
	public ApiResponse<TokenInfo> login(@RequestBody LoginRequestDto request) {
		TokenInfo tokens = userService.login(request);
		
		return ApiResponse.success(tokens, "로그인 되었습니다.");
	}
	
	@Override
	@PostMapping("/refresh")
	public ApiResponse<TokenInfo> refresh(@RequestBody TokenInfo oldToken) {
		TokenInfo newToken = userService.refresh(oldToken);
		
		return ApiResponse.success(newToken, "토큰이 재발급 되었습니다.");
	}
	
	@Override
	@PostMapping("/logout")
	public ApiResponse<String> logout(@AuthenticationPrincipal CustomUserDetails user) {
		userService.logout(user.getUsername());
		
		return ApiResponse.successWithNoContent("로그아웃 되었습니다.");
	}
	
	@Override
	@GetMapping("/email-verification/duplicate")
	public ApiResponse<String> checkEmail(@RequestParam String email) {
		emailVerificationService.checkEmailDuplicate(email);
		
		return ApiResponse.successWithNoContent("사용 가능한 이메일입니다.");
	}
	
	@Override
	@PostMapping("/email-verification/request")
	public ApiResponse<String> sendEmailVerification(@RequestParam String email) {
		emailVerificationService.sendCode(email);
		
		return ApiResponse.successWithNoContent("인증 코드가 발송되었습니다.");
	}
	
	@Override
	@PostMapping("/email-verification/verify")
	public ApiResponse<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
		emailVerificationService.verifyCode(email, code);
		
		return ApiResponse.successWithNoContent("이메일 인증이 완료되었습니다. 1시간 이내로 회원가입을 마무리해 주세요.");
	}
}
