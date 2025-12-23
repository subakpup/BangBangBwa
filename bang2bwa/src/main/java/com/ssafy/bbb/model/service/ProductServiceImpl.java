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
import com.ssafy.bbb.model.dto.MapResponseDto;
import com.ssafy.bbb.model.dto.MyProductDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductImageDto;
import com.ssafy.bbb.model.dto.ProductSearchDto;
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
	private final GeocodingService geocodingService;

	@Override
	@Transactional
	public Long create(Long agentId, ProductDto product, List<MultipartFile> files) {
		// 지번 주소를 이용해 좌표 설정
		setLatLng(product);
		product.setAgentId(agentId); // 등록 agent 설정

		// 매물 정보 DB 저장
		productDao.save(product);
		Long productId = product.getProductId();

		imageUpload(productId, product, files);

		return productId;
	}

	@Override
	@Transactional
	public ProductDto modify(Long productId, Long agentId, ProductDto product, List<MultipartFile> newfiles) {
		ProductDto existingProduct = productDao.findById(productId);
		if(existingProduct == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		if(existingProduct.getAgentId() != agentId) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
		
		// 혹시 모를 데이터 오염을 방지하기 위해, 데이터 강제 삽입
		product.setProductId(productId);
		product.setAgentId(agentId);
		
		// 지번 주소를 이용해 좌표 설정
		setLatLng(product);

		// 매물 정보 수정
		productDao.update(productId, product);

		// 기존 사진 중 삭제할 사진 삭제
		imageDelete(product.getDeleteImageIds());

		// 신규 사진 처리
		imageUpload(productId, product, newfiles);

		// 수정된 매물 정보 조회
		ProductDto modifiedProduct = productDao.findById(productId);
		List<ProductImageDto> currentImages = productDao.findImagesByProductId(productId);
		modifiedProduct.setImages(currentImages);

		// 3. 반환
		return modifiedProduct;
	}
	
	@Override
	@Transactional
	public void delete(Long productId, Long agentId) {
		ProductDto product = productDao.findById(productId);
		
		if (product == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		if (!product.getAgentId().equals(agentId)) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
		
		// 삭제할 이미지 목록
		List<ProductImageDto> images = productDao.findImagesByProductId(productId);
		
		if (images != null && !images.isEmpty()) {
			List<Long> imageIds = new ArrayList<>();
			for (ProductImageDto img : images) {
				imageIds.add(img.getImageId());
			}
			
			imageDelete(imageIds);
		}
		
		productDao.delete(productId);
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
					Path path = Paths.get(fileStore.getFullPath(deletePath));
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
	public List<ProductDto> search(ProductSearchDto request) {
		return productDao.search(request);
	}

	// 지번 주소를 이용해 좌표(위도, 경도) 설정하는 메서드
	private void setLatLng(ProductDto product) {
		String address = product.getSggNm() + " " + product.getUmdNm() + " " + product.getJibun();

		if (address != null && !address.trim().isEmpty()) {
			double[] coords = geocodingService.getCoordinate(address);

			if (coords[0] != 0.0 && coords[1] != 0.0) {
				product.setLatLng(coords[0], coords[1]);
			}
		}
	}

	@Override
	public List<MapResponseDto> findAllMarkers() {
		List<ProductDto> products = productDao.findAllMarkers();
		List<MapResponseDto> markers = new ArrayList<>();

		for (ProductDto product : products) {
			markers.add(MapResponseDto.builder()
					.productId(product.getProductId())
					.houseType(product.getHouseType())
					.tradeType(product.getTradeType())
					.dealAmount(product.getDealAmount())
					.deposit(product.getDeposit())
					.monthlyRent(product.getMonthlyRent())
					.latitude(product.getLatitude())
					.longitude(product.getLongitude())
					.build());
		}
		return markers;
	}

	
	@Override
	public ProductDto findProduct(Long productId) {
		ProductDto product = productDao.findById(productId);
		if(product == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}
		
		product.setImages(productDao.findImagesByProductId(productId));
		
		return product;
	}
	
	@Override
	public List<MyProductDto> findProductList(Long agentId) {
		return productDao.findMyProductList(agentId);
	}
}
