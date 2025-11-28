package com.ssafy.bbb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.model.dto.ProductImageDto;

@Component
public class FileStore {

	@Value("${product.image.dir}")
	private String imageDir;

	// 파일 전체 경로 반환
	public String getFullPath(String filename) {
		return Paths.get(imageDir, filename).toString();
	}

	// 파일을 실제 디스크에 저장, DB에 저장할 DTO로 변환하여 반환
	public ProductImageDto storeFile(MultipartFile multipartFile, Long productId) throws IOException {
		if (multipartFile.isEmpty()) {
			return null;
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String saveName = createSaveName(originalFilename);

		// 저장 경로 존재 여부 확인(없으면 새로 만듦)
		File uploadDir = new File(imageDir);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		String savePath = getFullPath(saveName);

		// 실제 디스크에 파일 저장
		multipartFile.transferTo(new File(savePath));

		// DB에 저장할 DTO를 반환
		return ProductImageDto.builder().productId(productId).originalName(originalFilename).saveName(saveName)
				.savePath(savePath).build();
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
