package com.ssafy.bbb.configuration;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${product.image.dir}")
	private String relativePath;

	/**
	 * 요청 URL을 실제 이미지 경로로 변경
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String projetPath = System.getProperty("user.dir");

		String imageDir = Paths.get(projetPath, relativePath).toString() + "/";

		// 요청 URL: http://localhost:8080/images/{파일명}
		registry.addResourceHandler("/images/**")
				// 실제 경로: file:///{imageDir}/{파일명}
				.addResourceLocations("file:///" + imageDir);
	}
}
