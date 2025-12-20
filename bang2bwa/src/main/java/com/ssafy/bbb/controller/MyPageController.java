package com.ssafy.bbb.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.controller.docs.MyPageControllerDocs;
import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;
import com.ssafy.bbb.model.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/my-page")
@RequiredArgsConstructor
public class MyPageController implements MyPageControllerDocs {
	private final UserService userService;
	
	@Override
	@GetMapping
	public ApiResponse<UserInfoDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails user) {
		UserInfoDto userInfo = userService.getUserInfo(user.getUserId());
		
		return ApiResponse.success(userInfo, "회원 정보 조회 성공");
	}
	
	@Override
	@PatchMapping
	public ApiResponse<String> updateUserInfo(@AuthenticationPrincipal CustomUserDetails user, @RequestBody UserUpdateDto request) {
		userService.updateUserInfo(user.getUserId(), request);
		
		return ApiResponse.successWithNoContent("회원 정보가 수정되었습니다.");
	}
	
	@Override
	@PatchMapping("/change-password")
	public ApiResponse<String> updatePassword(@AuthenticationPrincipal CustomUserDetails user, @RequestBody PasswordUpdateDto request) {
		userService.updatePassword(user.getUserId(), request);
		
		return ApiResponse.successWithNoContent("비밀번호가 변경되었습니다.");
	}
	
	@Override
	@DeleteMapping
	public ApiResponse<String> withdraw(@AuthenticationPrincipal CustomUserDetails user
									, @RequestHeader("Authorization") String accessToken) {
		
		userService.withdraw(user.getUserId(), user.getUsername(), accessToken.substring(7));
		
		return ApiResponse.successWithNoContent("회원 탈퇴가 완료되었습니다.");
	}

	@Override
	@GetMapping("/products")
	public ApiResponse<List<MyProductDto>> myProduct(@AuthenticationPrincipal CustomUserDetails user) {
		List<MyProductDto> myProducts = userService.myProducts(user.getUserId());
		
		return ApiResponse.success(myProducts);
	}
}
