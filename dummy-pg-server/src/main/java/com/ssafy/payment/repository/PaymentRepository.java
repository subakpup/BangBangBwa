package com.ssafy.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.payment.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	// 결제 검증 시 orderId로 결재 내역 검색 기능 사용
	Optional<Payment> findByOrderId(String orderId);
}
