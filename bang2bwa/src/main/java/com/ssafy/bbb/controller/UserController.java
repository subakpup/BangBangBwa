package com.ssafy.bbb.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;
import com.ssafy.bbb.model.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
	private final UserService userService;

	@Override
	@PostMapping("/signup")
	public ApiResponse<Long> signup(@RequestBody SignupRequestDto request) {
		Long userId = userService.signup(request);
		
		return ApiResponse.success(userId, "회원가입이 완료되었습니다.");
	}
	
	@Override
	@GetMapping("/check-email")
	public ApiResponse<String> checkEmail(@RequestParam String email) {
		userService.checkEmailDuplicate(email);
		
		return ApiResponse.successWithNoContent("사용 가능한 이메일입니다.");
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
	@GetMapping("/info")
	public ApiResponse<UserInfoDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails user) {
		UserInfoDto userInfo = userService.getUserInfo(user.getUserId());
		
		return ApiResponse.success(userInfo, "회원 정보 조회 성공");
	}
	
	@Override
	@PatchMapping("/info")
	public ApiResponse<String> updateUserInfo(@AuthenticationPrincipal CustomUserDetails user, @RequestBody UserUpdateDto request) {
		userService.updateUserInfo(user.getUserId(), request);
		
		return ApiResponse.successWithNoContent("회원 정보가 수정되었습니다.");
	}
	
	@Override
	@PatchMapping("/info/password")
	public ApiResponse<String> updatePassword(@AuthenticationPrincipal CustomUserDetails user, @RequestBody PasswordUpdateDto request) {
		userService.updatePassword(user.getUserId(), request);
		
		return ApiResponse.successWithNoContent("비밀번호가 변경되었습니다.");
	}
	
	@Override
	@DeleteMapping
	public ApiResponse<String> withdraw(@AuthenticationPrincipal CustomUserDetails user) {
		userService.withdraw(user.getUserId(), user.getUsername());
		
		return ApiResponse.successWithNoContent("회원 탈퇴가 완료되었습니다.");
	}
}
