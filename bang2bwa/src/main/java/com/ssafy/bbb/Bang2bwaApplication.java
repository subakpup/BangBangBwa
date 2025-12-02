package com.ssafy.bbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Bang2bwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(Bang2bwaApplication.class, args);
	}

}
