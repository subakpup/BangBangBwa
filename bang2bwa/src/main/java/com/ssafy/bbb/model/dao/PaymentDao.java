package com.ssafy.bbb.model.dao;

import java.util.Map;

import com.ssafy.bbb.model.dto.PaymentDto;

public interface PaymentDao {
	public void paymentSave(PaymentDto payment);

	public void paymentFee(Map<String, Object> paymentInfo);
}
