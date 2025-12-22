package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;

import com.ssafy.bbb.model.enums.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponseDto {
    private Long reservationId;
    private String productName;   // 매물 이름 (예: 역삼자이)
    private String productAddress; // 주소
    private String productImage;   // 매물 대표 이미지
    private LocalDateTime visitDate; // 방문 일시
    private String agentName;      // 담당 중개사 이름
    private ReservationStatus status;         // 예약 상태 (만남 완료 여부 확인용)
}