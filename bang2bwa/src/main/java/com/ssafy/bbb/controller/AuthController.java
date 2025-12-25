package com.ssafy.bbb.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1. 이메일 입력 후 전송 요청
    @PostMapping("/reset-password-request")
    public ApiResponse<Void> requestReset(@RequestBody Map<String, String> request) {
        authService.requestPasswordReset(request.get("email"));
        return ApiResponse.successWithNoContent("이메일이 전송되었습니다.");
    }

    // 2. 새 비밀번호 설정 요청
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(request.get("token"), request.get("newPassword"));
        return ApiResponse.successWithNoContent("비밀번호가 성공적으로 변경되었습니다.");
    }
}
