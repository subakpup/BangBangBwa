package com.ssafy.bbb.controller.docs;

import java.util.List;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;

import io.swagger.v3.oas.annotations.Operation;

public interface MyPageControllerDocs {
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ApiResponse<UserInfoDto> getUserInfo(CustomUserDetails user);

    @Operation(summary = "내 정보 수정", description = "이름, 전화번호, 생년월일 등을 수정합니다.")
    public ApiResponse<String> updateUserInfo(CustomUserDetails user, UserUpdateDto request);
    
    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정합니다.")
    public ApiResponse<String> updatePassword(CustomUserDetails user, PasswordUpdateDto newPassword);

    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제합니다.")
    public ApiResponse<String> withdraw(CustomUserDetails user);
    
    @Operation(summary = "내 매물 조회(중개업자)", description = "자신이 등록한 매물을 확인합니다.")
    public ApiResponse<List<MyProductDto>> myProduct(CustomUserDetails user);
}
