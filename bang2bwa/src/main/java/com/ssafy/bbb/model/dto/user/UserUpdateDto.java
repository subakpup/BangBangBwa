package com.ssafy.bbb.model.dto.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수정 요청용 DTO
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
	private String name;
	private String phone;
	private LocalDate birth;
	
	// Agent 추가 필드
	private String ceoName;
	private String realtorAgency;
	private String businessNumber;
}
