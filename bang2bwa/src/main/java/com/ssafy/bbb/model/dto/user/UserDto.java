package com.ssafy.bbb.model.dto.user;

import java.time.LocalDate;

import com.ssafy.bbb.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	private Long userId;
	private String email;
	private String password;
	private String phone;
	private String name;
	private LocalDate birth;
	private Role role;
}
