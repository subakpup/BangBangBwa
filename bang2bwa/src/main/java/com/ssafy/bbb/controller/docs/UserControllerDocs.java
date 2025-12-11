package com.ssafy.bbb.controller.docs;


import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API", description = "회원 기능 관련 컨트롤러")
public interface UserControllerDocs {

	@Operation(summary = "회원가입", description = "일반 회원 및 중개인 회원의 회원가입을 진행합니다.")
	public ApiResponse<Long> signup(@RequestBody SignupRequestDto request);
	
	@Operation(summary = "로그인", description = "이메일과 비밀번호를 이용해 로그인하여 token을 발급 받습니다.")
	public ApiResponse<TokenInfo> login(@RequestBody LoginRequestDto request);
	
	@Operation(summary = "이메일 중복 체크", description = "회원가입 전 중복된 이메일인지 먼저 체크합니다.")
	@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "사용 가능한 이메일",
            content = @Content(mediaType = "application/json"
            					, schema = @Schema(implementation = ApiResponse.class)
            					, examples = @ExampleObject(
            							value = "{\"success\": \"SUCCESS\", \"message\": \"사용 가능한 이메일입니다.\", \"data\": null}"
            					))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", description = "이미 사용 중인 이메일",
            content = @Content(mediaType = "application/json"
            					, schema = @Schema(implementation = ApiResponse.class)
            					, examples = @ExampleObject(
            							value = "{\"success\": \"FAIL\", \"message\": \"이미 존재하는 이메일입니다.\", \"data\": null}"
            				))
        )
    })
	public ApiResponse<String> checkEmail(@Parameter(description="중복 체크할 이메일", required=true) @RequestParam String email);
	
	@Operation(summary = "토큰 재발급", description = "구 토큰을 확인하여 valid 하다면, 새 토큰을 발급해줍니다.")
	public ApiResponse<TokenInfo> refresh(@RequestBody TokenInfo oldToken);
	
	@Operation(summary = "로그아웃", description = "서버에서 Refresh Token을 삭제합니다.")
    public ApiResponse<String> logout(CustomUserDetails user);

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ApiResponse<UserInfoDto> getUserInfo(CustomUserDetails user);

    @Operation(summary = "내 정보 수정", description = "이름, 전화번호, 생년월일 등을 수정합니다.")
    public ApiResponse<String> updateUserInfo(CustomUserDetails user, UserUpdateDto request);
    
    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정합니다.")
    public ApiResponse<String> updatePassword(CustomUserDetails user, PasswordUpdateDto newPassword);

    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제합니다.")
    public ApiResponse<String> withdraw(CustomUserDetails user);
}
