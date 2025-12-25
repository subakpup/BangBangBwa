package com.ssafy.bbb.model.service;

public interface AuthService {
	public void requestPasswordReset(String email);
	
	public void resetPassword(String token, String newPassword);
}
