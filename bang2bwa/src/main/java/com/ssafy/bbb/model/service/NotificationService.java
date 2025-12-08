package com.ssafy.bbb.model.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final JavaMailSender javaMailSender;

	@Async
	public void sendEmail(String to, String subject, String text) {
		if (to == null || to.isEmpty()) {
			return;
		}

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);

			javaMailSender.send(message);
		} catch (Exception e) {
			log.error("이메일 발송 실패: {}", e.getMessage());
		}
	}
}
