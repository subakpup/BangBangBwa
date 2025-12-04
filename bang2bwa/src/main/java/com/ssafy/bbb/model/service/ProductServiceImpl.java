package com.ssafy.bbb.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductImageDto;
import com.ssafy.bbb.util.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductDao productDao;
	private final FileStore fileStore;

	@Override
	@Transactional
	public Long create(ProductDto product, List<MultipartFile> files) {
		// 매물 정보 DB 저장
		productDao.save(product);
		Long productId = product.getProductId();

		imageUpload(productId, product, files);

		return productId;
	}

	@Override
	@Transactional
	public ProductDto modify(Long productId, ProductDto product, List<MultipartFile> newfiles) {
		// 매물 정보 수정
		productDao.update(productId, product);

		// 기존 사진 중 삭제할 사진 삭제
		imageDelete(product.getDeleteImageIds());

		// 신규 사진 처리
		imageUpload(productId, product, newfiles);

		// 수정된 매물 정보 조회
		ProductDto modifiedProduct = productDao.findById(productId);
		if (modifiedProduct == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		List<ProductImageDto> currentImages = productDao.findImagesByProductId(productId);
		modifiedProduct.setImages(currentImages);

		// 3. 반환
		return modifiedProduct;
	}

	private void imageUpload(Long productId, ProductDto product, List<MultipartFile> files) {

		if (files != null && !files.isEmpty()) {
			List<ProductImageDto> images = new ArrayList<>();

			// IOException을 catch하여 CustomException(FILE_UPLOAD_ERROR)로 변경
			try {
				// 디스크에 사진 저장 및 매물 사진 DTO(메타데이터) 생성
				for (MultipartFile image : files) {
					if (!image.isEmpty()) {
						ProductImageDto imageDto = fileStore.storeFile(image, productId);
						images.add(imageDto);
					}
				}
			} catch (IOException e) {
				log.error("파일 저장 중 실패: productID={}", product.getProductId(), e);
				throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
			}

			// 매물 사진 메타데이터 DB에 저장
			if (!images.isEmpty()) {
				productDao.saveImages(images);
			}
		}
	}

	private void imageDelete(List<Long> deleteIds) {

		if (deleteIds != null && !deleteIds.isEmpty()) {
			List<String> deletePaths = productDao.findSavePathByIds(deleteIds);

			// IOException을 catch하여 CustomException(FILE_DELETE_ERROR)로 변경
			try {
				// 디스크에서 사진 삭제
				for (String deletePath : deletePaths) {
					Path path = Paths.get(deletePath);
					Files.delete(path);
				}
			} catch (IOException e) {
				log.error("파일 삭제 중 실패: ", e);
				throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
			}

			// 디비에서 사진의 메타데이터 삭제
			productDao.deleteImages(deleteIds);
		}
	}

	@Override
	public List<ProductDto> search(String keyword, String type) {
		if (keyword == null || keyword.trim().isEmpty())
			return List.of();
		return productDao.search(keyword, type);
	}
}
