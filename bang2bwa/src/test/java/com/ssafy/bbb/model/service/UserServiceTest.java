package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.global.jwt.JwtTokenProvider;
import com.ssafy.bbb.model.dao.RefreshTokenDao;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.TokenInfo;
import com.ssafy.bbb.model.dto.user.LoginRequestDto;
import com.ssafy.bbb.model.dto.user.PasswordUpdateDto;
import com.ssafy.bbb.model.dto.user.SignupRequestDto;
import com.ssafy.bbb.model.dto.user.UserDto;
import com.ssafy.bbb.model.dto.user.UserInfoDto;
import com.ssafy.bbb.model.dto.user.UserUpdateDto;
import com.ssafy.bbb.model.enums.Role;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private RefreshTokenDao refreshTokenDao;
    @Mock private UserDao userDao;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailVerificationService emailVerificationService;

    // 공통 테스트 데이터 상수
    private final String EMAIL = "test@ssafy.com";
    private final String PASSWORD = "password1234";
    private final Long USER_ID = 1L;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("[성공] 유효한 정보로 로그인 시 토큰을 반환한다")
        void login_Success() {
            // given
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            Authentication auth = mock(Authentication.class);
            TokenInfo tokenInfo = TokenInfo.builder().accessToken("access").refreshToken("refresh").build();

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(auth);
            given(jwtTokenProvider.createToken(auth)).willReturn(tokenInfo);

            // when
            TokenInfo result = userService.login(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access");
            verify(refreshTokenDao).saveToken(EMAIL, "refresh");
        }

        @Test
        @DisplayName("[실패] 비밀번호 불일치 시 LOGIN_FAIL 예외 발생")
        void login_Fail_BadCredentials() {
            // given
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail(EMAIL);
            request.setPassword("wrongPw");

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("Bad creds"));

            // when & then
            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOGIN_FAIL);
        }

        @Test
        @DisplayName("[실패] 기타 인증 실패 시 USER_NOT_FOUND 예외 발생")
        void login_Fail_AuthException() {
            // given
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new AuthenticationException("Auth failed") {});

            // when & then
            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("[성공] 일반 회원 가입 시 기본 정보만 저장된다")
        void signup_Success_User() {
            // given
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);
            request.setRole(Role.ROLE_USER);

            willDoNothing().given(emailVerificationService).checkEmailDuplicate(EMAIL);
            given(passwordEncoder.encode(PASSWORD)).willReturn("encodedPw");
            given(emailVerificationService.isVerified(EMAIL)).willReturn(true);

            // when
            userService.signup(request);

            // then
            verify(userDao).save(any(UserDto.class));
            verify(userDao, never()).saveAgent(anyLong(), any(), any(), any());
        }

        @Test
        @DisplayName("[성공] 중개인 가입 시 Agent 정보도 함께 저장된다")
        void signup_Success_Agent() {
            // given
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);
            request.setRole(Role.ROLE_AGENT);
            request.setCeoName("CEO");
            request.setRealtorAgency("Agency");
            request.setBusinessNumber("123-45");

            willDoNothing().given(emailVerificationService).checkEmailDuplicate(EMAIL);
            given(passwordEncoder.encode(PASSWORD)).willReturn("encodedPw");
            given(emailVerificationService.isVerified(EMAIL)).willReturn(true);

            // when
            userService.signup(request);

            // then
            verify(userDao).save(any(UserDto.class));
            verify(userDao).saveAgent(any(), eq("CEO"), eq("Agency"), eq("123-45"));
        }

        @Test
        @DisplayName("[실패] 이메일 중복 시 DUPLICATE_EMAIL 예외 발생")
        void signup_Fail_DuplicateEmail() {
            // given
            SignupRequestDto request = new SignupRequestDto();
            request.setEmail(EMAIL);
            
            willThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL))
            .given(emailVerificationService).checkEmailDuplicate(EMAIL);

            // when & then
            assertThatThrownBy(() -> userService.signup(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("토큰 재발급 테스트")
    class RefreshTest {

        @Test
        @DisplayName("[성공] 유효한 리프레시 토큰으로 새 토큰 발급")
        void refresh_Success() {
            // given
            TokenInfo oldToken = TokenInfo.builder().accessToken("oldAccess").refreshToken("oldRefresh").build();
            Authentication auth = mock(Authentication.class);
            TokenInfo newToken = TokenInfo.builder().accessToken("newAccess").refreshToken("newRefresh").build();

            given(jwtTokenProvider.validateToken("oldRefresh")).willReturn(true);
            given(jwtTokenProvider.getAuthentication("oldAccess")).willReturn(auth);
            given(auth.getName()).willReturn(EMAIL);
            given(refreshTokenDao.getToken(EMAIL)).willReturn("oldRefresh");
            given(jwtTokenProvider.createToken(auth)).willReturn(newToken);

            // when
            TokenInfo result = userService.refresh(oldToken);

            // then
            assertThat(result).isEqualTo(newToken);
            verify(refreshTokenDao).saveToken(EMAIL, "newRefresh");
        }

        @Test
        @DisplayName("[실패] 리프레시 토큰 자체가 유효하지 않음")
        void refresh_Fail_InvalidToken() {
            // given
            TokenInfo oldToken = TokenInfo.builder().refreshToken("invalidRefresh").build();
            given(jwtTokenProvider.validateToken("invalidRefresh")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.refresh(oldToken))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
        }

        @Test
        @DisplayName("[실패] DB에 저장된 토큰과 불일치 (로그아웃됨)")
        void refresh_Fail_ExpiredToken() {
            // given
            TokenInfo oldToken = TokenInfo.builder().accessToken("oldAccess").refreshToken("oldRefresh").build();
            Authentication auth = mock(Authentication.class);

            given(jwtTokenProvider.validateToken("oldRefresh")).willReturn(true);
            given(jwtTokenProvider.getAuthentication("oldAccess")).willReturn(auth);
            given(auth.getName()).willReturn(EMAIL);
            given(refreshTokenDao.getToken(EMAIL)).willReturn("differentToken"); // 불일치

            // when & then
            assertThatThrownBy(() -> userService.refresh(oldToken))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_TOKEN);
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateUserInfoTest {

        @Test
        @DisplayName("[성공] 일반 회원은 기본 정보만 수정한다")
        void updateUserInfo_User() {
            // given
            UserUpdateDto request = new UserUpdateDto();
            given(userDao.findRoleById(USER_ID)).willReturn(Role.ROLE_USER);

            // when
            userService.updateUserInfo(USER_ID, request);

            // then
            verify(userDao).updateUser(USER_ID, request);
            verify(userDao, never()).updateAgent(anyLong(), any());
        }

        @Test
        @DisplayName("[성공] 중개인은 사업자 정보도 함께 수정한다")
        void updateUserInfo_Agent() {
            // given
            UserUpdateDto request = new UserUpdateDto();
            given(userDao.findRoleById(USER_ID)).willReturn(Role.ROLE_AGENT);

            // when
            userService.updateUserInfo(USER_ID, request);

            // then
            verify(userDao).updateUser(USER_ID, request);
            verify(userDao).updateAgent(USER_ID, request);
        }
    }

    @Nested
    @DisplayName("비밀번호 수정 테스트")
    class UpdatePasswordTest {

        @Test
        @DisplayName("[성공] 현재 비밀번호가 일치하면 새 비밀번호로 변경")
        void updatePassword_Success() {
            // given
            PasswordUpdateDto request = new PasswordUpdateDto();
            request.setCurrentPassword("current");
            request.setNewPassword("new");

            given(userDao.findPasswordById(USER_ID)).willReturn("encodedCurrent");
            given(passwordEncoder.matches("current", "encodedCurrent")).willReturn(true);
            given(passwordEncoder.encode("new")).willReturn("encodedNew");

            // when
            userService.updatePassword(USER_ID, request);

            // then
            verify(userDao).updatePassword(USER_ID, "encodedNew");
        }

        @Test
        @DisplayName("[실패] 현재 비밀번호 불일치 시 예외 발생")
        void updatePassword_Fail_Mismatch() {
            // given
            PasswordUpdateDto request = new PasswordUpdateDto();
            request.setCurrentPassword("wrong");
            given(userDao.findPasswordById(USER_ID)).willReturn("encodedCurrent");
            given(passwordEncoder.matches("wrong", "encodedCurrent")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.updatePassword(USER_ID, request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Test
    @DisplayName("[성공] 내 정보 조회 성공 테스트")
    void getUserInfo_Success() {
        // given
        UserInfoDto userInfoDto = new UserInfoDto();
        given(userDao.findUserInfoById(USER_ID)).willReturn(Optional.of(userInfoDto));

        // when
        UserInfoDto result = userService.getUserInfo(USER_ID);

        // then
        assertThat(result).isEqualTo(userInfoDto);
    }

    @Test
    @DisplayName("[실패] 내 정보 조회 : 존재하지 않는 유저")
    void getUserInfo_Fail_NotFound() {
        // given
        given(userDao.findUserInfoById(USER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserInfo(USER_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("[성공] 로그아웃 테스트: 리프레시 토큰 삭제")
    void logout_Success() {
        // when
        userService.logout(EMAIL);

        // then
        verify(refreshTokenDao).deleteToken(EMAIL);
    }

    @Test
    @DisplayName("[성공] 회원 탈퇴 테스트: 토큰 삭제 및 유저 삭제")
    void withdraw_Success() {
        // when
        userService.withdraw(USER_ID, EMAIL);

        // then
        verify(refreshTokenDao).deleteToken(EMAIL);
        verify(userDao).deleteUser(USER_ID);
    }
}