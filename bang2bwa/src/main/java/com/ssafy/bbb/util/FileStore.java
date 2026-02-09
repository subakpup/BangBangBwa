package com.ssafy.bbb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.model.dto.ProductImageDto;

import jakarta.annotation.PostConstruct;

@Component
public class FileStore {

	@Value("${product.image.dir}")
	private String relativePath; // 상대 경로
	private String absolutePath; // 절대 경로

	// 빈 생성 직후 프로젝트 설정 파일에서 읽어온 경로와 프로젝트의 루트 경로를 합하여 반환.
	@PostConstruct
	public void init() {
		String projectPath = System.getProperty("user.dir"); // 현재 프로젝트의 루트 경로

		this.absolutePath = Paths.get(projectPath, relativePath).toString() + "/";
	}

	// 파일 전체 경로(물리적 경로) 반환
	public String getFullPath(String savePath) {
		return absolutePath + "/" + savePath;
	}

	// 파일을 실제 디스크에 저장, DB에 저장할 DTO로 변환하여 반환
	public ProductImageDto storeFile(MultipartFile multipartFile, Long productId) throws IOException {
		if (multipartFile.isEmpty()) {
			return null;
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String saveName = createSaveName(originalFilename);

		String folderPath = absolutePath + productId + "/";

		// 저장 경로 존재 여부 확인(없으면 새로 만듦)
		File uploadDir = new File(folderPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// 실제 디스크에 파일 저장
		String savePath = folderPath + saveName;
		multipartFile.transferTo(new File(savePath));

		// DB에 저장할 DTO를 반환
		return ProductImageDto.builder()
					.productId(productId)
					.originalName(originalFilename)
					.saveName(saveName)
					.savePath(productId + "/" + saveName)
					.build();
	}

	// UUID를 이용한 새 파일명 생성.
	private String createSaveName(String originalFilename) {
		String extension = extractExtension(originalFilename);
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + extension;
	}

	// 파일 확장자 분리
	private String extractExtension(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}
}
