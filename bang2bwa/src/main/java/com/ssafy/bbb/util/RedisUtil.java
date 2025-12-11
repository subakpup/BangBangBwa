package com.ssafy.bbb.util;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil {
	private final StringRedisTemplate redisTemplate;
	
	// 데이터 저장
	public void setDataExpire(String key, String value, long durationSeconds) {
		Duration expireDuration = Duration.ofSeconds(durationSeconds);
		redisTemplate.opsForValue().set(key, value, expireDuration);
	}
	
	// 데이터 조회
	public String getData(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	// 데이터 삭제
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
	
	// 데이터 존재 여부 확인
	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
}
