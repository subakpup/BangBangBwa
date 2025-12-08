package com.ssafy.bbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class Bang2bwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(Bang2bwaApplication.class, args);
	}

}
