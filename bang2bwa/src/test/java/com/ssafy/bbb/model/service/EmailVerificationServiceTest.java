package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.util.RedisUtil;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks private EmailVerificationService emailService;
    @Mock private RedisUtil redisUtil;
    @Mock private NotificationService notificationService;
    @Mock private UserDao userDao;

    private final String EMAIL = "test@ssafy.com";
    private final String AUTH_CODE = "123456";
    private final String REDIS_CODE_KEY = "auth_code:" + EMAIL;
    private final String REDIS_VERIFIED_KEY = "verified:" + EMAIL;

    @Nested
    @DisplayName("인증 코드 발송 (sendCode)")
    class SendCodeTest {

        @Test
        @DisplayName("[성공] 인증 코드를 생성하여 Redis에 저장하고 이메일을 발송한다")
        void sendCode_Success() {
            // given

            // when
            emailService.sendCode(EMAIL);

            // then
            // 1. Redis 저장 호출 검증 (키, 임의의 문자열, 만료시간 5분)
            verify(redisUtil).setDataExpire(eq(REDIS_CODE_KEY), anyString(), eq(60 * 5L));
            
            // 2. 이메일 발송 호출 검증
            verify(notificationService).sendEmail(eq(EMAIL), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("인증 코드 검증 (verifyCode)")
    class VerifyCodeTest {

        @Test
        @DisplayName("[성공] 코드가 일치하면 인증 성공 처리(verified 저장)된다")
        void verifyCode_Success() {
            // given
            given(redisUtil.getData(REDIS_CODE_KEY)).willReturn(AUTH_CODE);

            // when
            emailService.verifyCode(EMAIL, AUTH_CODE);

            // then
            // 1. 기존 인증 코드 삭제
            verify(redisUtil).deleteData(REDIS_CODE_KEY);
            
            // 2. 인증 완료 키 저장 (1시간 유효)
            verify(redisUtil).setDataExpire(eq(REDIS_VERIFIED_KEY), eq("TRUE"), eq(60 * 60L));
        }

        @Test
        @DisplayName("[실패] Redis에 저장된 코드가 없음 (만료되었거나 요청 안 함) -> EXPIRED_CODE 예외")
        void verifyCode_Fail_Expired() {
            // given
            given(redisUtil.getData(REDIS_CODE_KEY)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> emailService.verifyCode(EMAIL, AUTH_CODE))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_CODE);

            // 저장 로직 실행 안 됨
            verify(redisUtil, never()).setDataExpire(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("[실패] 코드가 일치하지 않음 -> INVALID_CODE 예외")
        void verifyCode_Fail_Mismatch() {
            // given
            given(redisUtil.getData(REDIS_CODE_KEY)).willReturn(AUTH_CODE);
            String wrongCode = "999999";

            // when & then
            assertThatThrownBy(() -> emailService.verifyCode(EMAIL, wrongCode))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CODE);

            // 저장 로직 실행 안 됨
            verify(redisUtil, never()).setDataExpire(anyString(), anyString(), anyLong());
        }
    }

    @Nested
    @DisplayName("이메일 중복 체크 (checkEmailDuplicate)")
    class CheckEmailDuplicateTest {

        @Test
        @DisplayName("[성공] 중복된 이메일이 없으면 아무 일도 일어나지 않는다")
        void checkEmail_Success() {
            // given
            given(userDao.existsByEmail(EMAIL)).willReturn(0);

            // when
            emailService.checkEmailDuplicate(EMAIL);

            // then
            verify(userDao).existsByEmail(EMAIL);
            // 예외가 발생하지 않으면 성공
        }

        @Test
        @DisplayName("[실패] 이미 가입된 이메일이면 DUPLICATE_EMAIL 예외 발생")
        void checkEmail_Fail_Duplicate() {
            // given
            given(userDao.existsByEmail(EMAIL)).willReturn(1);

            // when & then
            assertThatThrownBy(() -> emailService.checkEmailDuplicate(EMAIL))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("인증 여부 확인 (isVerified)")
    class IsVerifiedTest {

        @Test
        @DisplayName("[성공] Redis에 verified 키가 존재하면 true 반환")
        void isVerified_True() {
            // given
            given(redisUtil.hasKey(REDIS_VERIFIED_KEY)).willReturn(true);

            // when
            boolean result = emailService.isVerified(EMAIL);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[실패] Redis에 verified 키가 없으면 false 반환")
        void isVerified_False() {
            // given
            given(redisUtil.hasKey(REDIS_VERIFIED_KEY)).willReturn(false);

            // when
            boolean result = emailService.isVerified(EMAIL);

            // then
            assertThat(result).isFalse();
        }
    }
}