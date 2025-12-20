package com.ssafy.bbb.controller.docs;


import org.springframework.web.bind.annotation.RequestParam;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.global.security.CustomUserDetails;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;

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
    public ApiResponse<String> logout(@Parameter(hidden=true) CustomUserDetails user, String accessToken);

    @Operation(summary = "이메일 인증 코드 발송", description = "이메일로 6자리 인증 코드를 전송합니다.")
    public ApiResponse<String> sendEmailVerification(String email);
    
    @Operation(summary = "이메일 인증 코드 확인", description = "전송된 코드를 검증합니다.")
    public ApiResponse<String> verifyEmail(String email, String code);
}
