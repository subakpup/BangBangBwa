package com.ssafy.bbb.model.dto.user;

import java.time.LocalDate;

import com.ssafy.bbb.model.enums.Role;

import lombok.Data;

@Data
public class SignupRequestDto {
	private String email;
	private String password;
	private String name;
	private String phone;
	private LocalDate birth;
	private Role role;
	
	// === Agent 전용 필드 === //
	private String ceoName;
	private String realtorAgency;
	private String businessNumber;
}
