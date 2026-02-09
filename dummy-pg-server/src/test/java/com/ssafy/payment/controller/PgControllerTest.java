package com.ssafy.payment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.payment.dto.PaymentAuthDto;
import com.ssafy.payment.dto.PaymentCaptureDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PgControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("시나리오: 가승인 -> 부분 결제")
	void payment_scenario() throws Exception {

		// Step 1. 가승인 요청
		PaymentAuthDto authRequest = PaymentAuthDto.builder().orderId("ORD_TEST_001").amount(10000L).type("DEPOSIT")
				.build();

		ResultActions authActions = mockMvc.perform(post("/api/v1/payments/pre-auth")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest)));

		MvcResult authResult = authActions.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
				.andExpect(status().isOk()).andReturn();

		// 응답에서 paymentKey 추출
		String responseBody = authResult.getResponse().getContentAsString();
		Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
		Map<String, Object> dataMap = (Map) responseMap.get("data");
		String paymentKey = (String) dataMap.get("paymentKey");

		// Step 2. 부분 결제 요청
		PaymentCaptureDto captureRequest = PaymentCaptureDto.builder().paymentKey(paymentKey).captureAmount(1000L)
				.build();

		ResultActions captureActions = mockMvc.perform(post("/api/v1/payments/capture")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(captureRequest)));

		captureActions.andExpect(jsonPath("$.resultCode").value("SUCCESS"))
				.andExpect(jsonPath("$.data.status").value("PAID"))
				.andExpect(jsonPath("$.data.finalAmount").value(1000)).andExpect(status().isOk());
	}
}
