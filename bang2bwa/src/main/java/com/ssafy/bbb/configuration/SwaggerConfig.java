package com.ssafy.bbb.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Bang-Bang-Bwa API 명세서", description = "방구할땐 멀리가지 말고 방구석에서 찾아봐", version = "v1"))
public class SwaggerConfig {

	@Bean
	GroupedOpenApi openAPI() {
		String[] paths = { "/reservations/**" };
		return GroupedOpenApi.builder().group("부동산 예약 관련 API").pathsToMatch(paths).build();
	}
}
