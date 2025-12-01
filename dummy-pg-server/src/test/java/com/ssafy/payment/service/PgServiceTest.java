package com.ssafy.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.payment.domain.Payment;
import com.ssafy.payment.dto.PaymentAuthDto;
import com.ssafy.payment.dto.PaymentCaptureDto;
import com.ssafy.payment.dto.PaymentResponseDto;
import com.ssafy.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PgServiceTest {

	@InjectMocks
	private PgService pgService; // 테스트 대상

	@Mock
	private PaymentRepository paymentRepository; // 가짜 DB

	@Test
	@DisplayName("가승인 요청 시 AUTHORIZED 상태로 저장되어야 한다.")
	void preAuth_success() {
		// given
		PaymentAuthDto request = PaymentAuthDto.builder().orderId("ORD_001").amount(10000L).type("DEPOSIT").build();

		given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		PaymentResponseDto response = pgService.requestPreAuth(request);

		// then
		assertEquals(response.getResultCode(), "SUCCESS");

		Payment savedPayment = (Payment) response.getData();
		assertEquals(savedPayment.getStatus(), "AUTHORIZED");
		assertEquals(savedPayment.getOriginalAmount(), 10000L);
	}

	@Test
	@DisplayName("부분 결제시 상태가 PAID로 변경되고 결제가 확정되어야한다.")
	void capture_success() {
		// given
		String paymentKey = "test_key";
		Payment authorizedPayment = Payment.builder().paymentKey(paymentKey).amount(10000L).status("AUTHORIZED")
				.build();

		given(paymentRepository.findByPaymentKey(paymentKey)).willReturn(Optional.of(authorizedPayment));

		PaymentCaptureDto request = PaymentCaptureDto.builder().paymentKey(paymentKey).captureAmount(1000L).build();

		// when
		PaymentResponseDto response = pgService.capturePayment(request);

		// then
		assertEquals(response.getResultCode(), "SUCCESS");

		Payment capturedPayment = (Payment) response.getData();
		assertEquals(capturedPayment.getStatus(), "PAID");
		assertEquals(capturedPayment.getFinalAmount(), 1000L);
	}

	@Test
	@DisplayName("원금보다 큰 금액을 결제하려 하면 실패해야 한다")
	void capture_fail_status_exceeded() {
		// given
		String paymentKey = "test_key";
		Payment authorizedPayment = Payment.builder().paymentKey(paymentKey).amount(10000L).status("AUTHORIZED")
				.build();

		given(paymentRepository.findByPaymentKey(paymentKey)).willReturn(Optional.of(authorizedPayment));

		PaymentCaptureDto request = PaymentCaptureDto.builder().paymentKey(paymentKey).captureAmount(20000L) // 2만원 매입
				.build();

		// when
		PaymentResponseDto response = pgService.capturePayment(request);

		// then
		assertEquals(response.getResultCode(), "FAIL");
		assertEquals(response.getMessage(), "수수료가 원금보다 크면 안됩니다.");
	}

	@Test
	@DisplayName("AUTHORIZED 상태만 결제 되어야 합니다.")
	void capture_fail_amount_exceeded() {
		// given
		String paymentKey_paid = "paid_key";
		String paymentKey_cancled = "cancled_key";

		Payment authorizedPayment_paid = Payment.builder().paymentKey(paymentKey_paid).amount(10000L).status("PAID")
				.build();
		Payment authorizedPayment_cancled = Payment.builder().paymentKey(paymentKey_cancled).amount(10000L)
				.status("CANCLED").build();

		given(paymentRepository.findByPaymentKey(paymentKey_paid)).willReturn(Optional.of(authorizedPayment_paid));
		given(paymentRepository.findByPaymentKey(paymentKey_cancled))
				.willReturn(Optional.of(authorizedPayment_cancled));

		PaymentCaptureDto request_paid = PaymentCaptureDto.builder().paymentKey(paymentKey_paid).captureAmount(100L)
				.build();
		PaymentCaptureDto request_cancled = PaymentCaptureDto.builder().paymentKey(paymentKey_cancled)
				.captureAmount(100L).build();

		// when
		PaymentResponseDto response_paid = pgService.capturePayment(request_paid);
		PaymentResponseDto response_cancled = pgService.capturePayment(request_cancled);

		// then
		assertEquals(response_paid.getResultCode(), "FAIL");
		assertEquals(response_paid.getMessage(), "가승인 상태의 결제만 매입할 수 있습니다.");

		assertEquals(response_cancled.getResultCode(), "FAIL");
		assertEquals(response_cancled.getMessage(), "가승인 상태의 결제만 매입할 수 있습니다.");
	}
}
