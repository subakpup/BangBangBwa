package com.ssafy.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.payment.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	// 결제 키로 조회
	Optional<Payment> findByPaymentKey(String paymentKey);
}
