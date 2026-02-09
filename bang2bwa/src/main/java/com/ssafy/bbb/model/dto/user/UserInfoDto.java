package com.ssafy.bbb.model.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.enums.Role;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 내 정보 조회용 DTO
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
	private Long userId;
	private String email;
	private String name;
	private String phone;
	private LocalDate birth;
	private Role role;
	private MyReservationDto reservation;

	// Agent 추가 필드
	private String ceoName;
	private String realtorAgency;
	private String businessNumber;
	
	
	// 예약정보 채워넣기
	public void setReservation(MyReservationDto reservation) {
		if(reservation != null) {
			this.reservation = MyReservationDto.builder()
								.type(reservation.getType())
								.name(reservation.getName())
								.address(reservation.getAddress())
								.floor(reservation.getFloor())
								.excluUseAr(reservation.getExcluUseAr())
								.description(reservation.getDescription())
								.visitDate(reservation.getVisitDate())
								.status(reservation.getStatus())
								.dealAmount(reservation.getDealAmount())
								.deposit(reservation.getDeposit())
								.monthlyRent(reservation.getMonthlyRent())
								.build();
		}
	}
	
	// 예약 정보 클래스(내부 클래스)
	@Getter
	@Builder
	static public class MyReservationDto {
		private TradeType type;
		private String name;
		private String address;
		private String floor;
		private Double excluUseAr;
		private String description;
		private LocalDateTime visitDate;
		private ReservationStatus status;

		// 가격 정보
		private Long dealAmount;
		private Long deposit;
		private Long monthlyRent;
	}
}
