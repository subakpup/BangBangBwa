package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.dto.ProductImageDto;
import com.ssafy.bbb.model.dto.ProductSearchDto;
import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.enums.TradeType;
import com.ssafy.bbb.util.FileStore;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 비지니스 로직 테스트")
class ProductServiceTest {

	@InjectMocks private ProductServiceImpl productService;
	@Mock private ProductDao productDao; 
	@Mock private FileStore fileStore; // 파일 등록, 수정 기능을 테스트 하기위한 Mock 추가
	@Mock private GeocodingService geocordingService;

	// 더미 DTO 생성
	private ProductDto createDummyDto(Long id) {
		ProductDto dto = ProductDto.builder().jibun("서울시 강남구").houseType(HouseType.APART).tradeType(TradeType.SALE).status(ReservationStatus.AVAILABLE)
				.name("SSAFY Apart").dealAmount(10000L).agentId(1L).build();

		if (id != null) {
			ReflectionTestUtils.setField(dto, "productId", id);
		}

		return dto;
	}

	// 더미 이미지 DTO 생성
	private ProductImageDto createDummyImageDto(Long productId) {
		return ProductImageDto.builder().productId(productId).originalName("test.jpg").saveName("uuid.jpg")
				.savePath("/tmp/uuid.jpg").build();
	}

	@Nested
	@DisplayName("상품 등록 테스트")
	class CreateTest {

		@Test
		@DisplayName("[SUCCESS] 상품 정보와 이미지가 모두 정상 저장된다.")
		void success_with_images() throws IOException {
			// given
			ProductDto requestDto = createDummyDto(null);
			Long generatedId = 14L;
			
			given(geocordingService.getCoordinate(anyString()))
		    .willReturn(new double[] { 37.5665, 126.9780 });

			List<MultipartFile> files = new ArrayList<>();
			MultipartFile mockFile1 = mock(MultipartFile.class);
			MultipartFile mockFile2 = mock(MultipartFile.class);
			files.add(mockFile1);
			files.add(mockFile2);
			
			

			// productDao.save => ID 주입
			willAnswer(invocation -> {
				ProductDto argument = invocation.getArgument(0);
				ReflectionTestUtils.setField(argument, "productId", generatedId);
				return 1L;
			}).given(productDao).save(any(ProductDto.class));

			// FileStore => 파일 저장(2번)
			given(mockFile1.isEmpty()).willReturn(false);
			given(mockFile2.isEmpty()).willReturn(false);
			given(fileStore.storeFile(any(), eq(generatedId))).willReturn(createDummyImageDto(generatedId));

			// when
			Long resultId = productService.create(1L, requestDto, files);

			// then
			assertThat(resultId).isEqualTo(generatedId);

			// 각 함수들이 몇번 불렸는지 테스트하는 함수.
			then(productDao).should(times(1)).save(requestDto);
			then(fileStore).should(times(2)).storeFile(any(), eq(generatedId));
			then(productDao).should(times(1)).saveImages(anyList());
		}

		@Test
		@DisplayName("[SUCCESS] 파일 없이 텍스트만 있어도 등록에 성공한다.")
		void success_no_files() throws IOException {
			// given
			ProductDto requestDto = createDummyDto(null);
			Long generatedId = 15L;
			List<MultipartFile> emptyFiles = Collections.emptyList();
			
			given(geocordingService.getCoordinate(anyString()))
		    .willReturn(new double[] { 37.5665, 126.9780 });

			willAnswer(invocation -> {
				ProductDto arg = invocation.getArgument(0);
				ReflectionTestUtils.setField(arg, "productId", generatedId);
				return 1L;
			}).given(productDao).save(any(ProductDto.class));

			// when
			Long resultId = productService.create(1L, requestDto, emptyFiles);

			// then
			assertThat(resultId).isEqualTo(generatedId);
			then(fileStore).should(never()).storeFile(any(), any()); // 파일 저장은 호출되면 안됨
			then(productDao).should(never()).saveImages(any()); // 이미지 DB 저장도 호출되면 안됨
		}
	}

	@Nested
	@DisplayName("상품 수정 테스트")
	class ModifyTest {

		@Test
		@DisplayName("[SUCCESS] 이미지 삭제 및 추가가 포함된 수정 요청이 성공한다.")
		void success_with_image_modify() throws IOException {
			// given
			Long targetId = 14L;

			// 1. 삭제할 이미지 ID 세팅
			List<Long> deleteImageIds = List.of(100L, 101L);
			ProductDto updateRequest = createDummyDto(null);
			ReflectionTestUtils.setField(updateRequest, "deleteImageIds", deleteImageIds);

			// 2. 추가할 새 파일 세팅
			List<MultipartFile> newFiles = new ArrayList<>();
			MultipartFile newFile = mock(MultipartFile.class);
			newFiles.add(newFile);

			// 3. 응답용 더미 데이터
			ProductDto updatedResponse = createDummyDto(targetId);

			// --- Mocking Start ---
			
			given(geocordingService.getCoordinate(anyString()))
		    .willReturn(new double[] { 37.5665, 126.9780 });

			// (A) 파일 삭제 로직 Mocking
			// Service 내부에서 Files.delete(path)를 호출하므로, 실제 존재하는 임시 파일을 만들어 경로를 넘겨줘야 에러가 안남.
			Path tempFile = Files.createTempFile("test_delete", ".jpg");
			List<String> deletePaths = List.of(tempFile.toString());

			given(productDao.findSavePathByIds(deleteImageIds)).willReturn(deletePaths);
			given(fileStore.getFullPath(anyString())).willReturn(tempFile.toString());

			// (B) 파일 추가 로직 Mocking
			given(newFile.isEmpty()).willReturn(false);
			given(fileStore.storeFile(any(), eq(targetId))).willReturn(createDummyImageDto(targetId));

			// (C) 최종 조회 Mocking
			given(productDao.findById(targetId)).willReturn(updatedResponse);
			// 조회된 매물에 이미지 리스트 세팅 (빈 리스트라도)
			given(productDao.findImagesByProductId(targetId)).willReturn(new ArrayList<>());

			// when
			ProductDto result = productService.modify(targetId, 1L, updateRequest, newFiles);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getProductId()).isEqualTo(targetId);

			// Verify 1: 매물 정보 업데이트
			then(productDao).should(times(1)).update(eq(targetId), any(ProductDto.class));

			// Verify 2: 파일 경로 조회 (삭제 전)
			then(productDao).should(times(1)).findSavePathByIds(deleteImageIds);

			// Verify 3: DB 이미지 삭제 (Bulk Delete)
			then(productDao).should(times(1)).deleteImages(deleteImageIds);

			// Verify 4: 새 파일 저장 (FileStore)
			then(fileStore).should(times(1)).storeFile(any(), eq(targetId));

			// Verify 5: 새 이미지 DB 저장
			then(productDao).should(times(1)).saveImages(anyList());

			// Cleanup: 테스트용 임시 파일 삭제 (혹시 남아있다면)
			Files.deleteIfExists(tempFile);
		}

		@Test
		@DisplayName("[FAIL] 존재하지 않는 매물 ID로 수정 요청 시 CustomException(PRODUCT_NOT_FOUND)가 발생한다.")
		void fail_not_found() {
			// given
			Long wrongId = 999L;
			ProductDto updateReq = createDummyDto(null);
			List<MultipartFile> files = Collections.emptyList();

			// Mocking: findById가 null 리턴
			given(productDao.findById(wrongId)).willReturn(null);

			// when & then
			assertThatThrownBy(() -> productService.modify(wrongId, 1L, updateReq, files))
					.isInstanceOf(CustomException.class).extracting("errorCode").isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
		}
	}
	
	@Nested
	@DisplayName("상품 삭제 테스트")
	class DeleteTest {

		@Test
		@DisplayName("[SUCCESS] 이미지 파일이 포함된 매물 삭제가 정상적으로 수행된다.")
		void success_with_images() throws IOException {
			// given
			Long targetId = 100L;
			Long agentId = 1L;

			// 1. 매물 정보 Mock
			ProductDto product = createDummyDto(targetId);
			product.setAgentId(agentId);

			// 2. 이미지 정보 Mock (2개 생성)
			ProductImageDto img1 = createDummyImageDto(targetId);
			ProductImageDto img2 = createDummyImageDto(targetId);
			
			ReflectionTestUtils.setField(img1, "imageId", 10L); 
			ReflectionTestUtils.setField(img2, "imageId", 11L);
			List<ProductImageDto> images = List.of(img1, img2);

			Path tempFile1 = Files.createTempFile("test_delete_1", ".jpg");
			Path tempFile2 = Files.createTempFile("test_delete_2", ".jpg");
			
			// Service 로직에서 이 리스트를 순회함
			List<String> deletePaths = List.of("path/1", "path/2");

			// --- Mocking ---
			given(productDao.findById(targetId)).willReturn(product);
			given(productDao.findImagesByProductId(targetId)).willReturn(images);
			
			// imageDelete 내부 로직 Mocking
			given(productDao.findSavePathByIds(anyList())).willReturn(deletePaths);
			
			// [중요] 첫 번째 호출 땐 tempFile1, 두 번째 호출 땐 tempFile2를 리턴하도록 설정 (Chaining)
			given(fileStore.getFullPath(anyString()))
				.willReturn(tempFile1.toString()) // 첫 번째 루프용
				.willReturn(tempFile2.toString());// 두 번째 루프용

			// when
			productService.delete(targetId, agentId);

			// then
			then(productDao).should(times(1)).findById(targetId);
			then(productDao).should(times(1)).findImagesByProductId(targetId);
			then(productDao).should(times(1)).findSavePathByIds(anyList());
			then(productDao).should(times(1)).deleteImages(anyList());
			then(productDao).should(times(1)).delete(targetId);

			// Cleanup: 테스트 후 파일 정리
			Files.deleteIfExists(tempFile1);
			Files.deleteIfExists(tempFile2);
		}

		@Test
		@DisplayName("[SUCCESS] 이미지가 없는 매물도 정상적으로 삭제된다.")
		void success_no_images() {
			// given
			Long targetId = 200L;
			Long agentId = 1L;

			ProductDto product = createDummyDto(targetId);
			product.setAgentId(agentId);

			// Mocking
			given(productDao.findById(targetId)).willReturn(product);
			given(productDao.findImagesByProductId(targetId)).willReturn(Collections.emptyList());

			// when
			productService.delete(targetId, agentId);

			// then
			then(productDao).should(times(1)).findById(targetId);
			then(productDao).should(times(1)).findImagesByProductId(targetId);
			
			// 이미지가 없으므로 아래 로직은 실행되지 않아야 함
			then(productDao).should(never()).findSavePathByIds(anyList());
			then(fileStore).should(never()).getFullPath(anyString());
			then(productDao).should(never()).deleteImages(anyList());

			// 매물 삭제는 실행되어야 함
			then(productDao).should(times(1)).delete(targetId);
		}

		@Test
		@DisplayName("[FAIL] 존재하지 않는 매물 삭제 시 예외가 발생한다.")
		void fail_not_found() {
			// given
			Long wrongId = 999L;
			Long agentId = 1L;

			given(productDao.findById(wrongId)).willReturn(null);

			// when & then
			assertThatThrownBy(() -> productService.delete(wrongId, agentId))
					.isInstanceOf(CustomException.class)
					.extracting("errorCode")
					.isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
		}

		@Test
		@DisplayName("[FAIL] 타인의 매물을 삭제하려 하면 예외가 발생한다.")
		void fail_forbidden() {
			// given
			Long targetId = 100L;
			Long ownerId = 1L;
			Long hackerId = 2L; // 다른 사람

			ProductDto product = createDummyDto(targetId);
			product.setAgentId(ownerId); // 주인은 1번

			given(productDao.findById(targetId)).willReturn(product);

			// when & then (2번 해커가 1번 매물을 지우려 함)
			assertThatThrownBy(() -> productService.delete(targetId, hackerId))
					.isInstanceOf(CustomException.class)
					.extracting("errorCode")
					.isEqualTo(ErrorCode.FORBIDDEN_USER);
		}
	}

	@Nested
	@DisplayName("상품 검색 테스트")
	class SearchTest {

		@Test
		@DisplayName("[SUCCESS] 검색 조건(DTO)이 있을 때 정상 조회된다.")
		void success_with_keyword_and_type() {
			// given
			ProductSearchDto request = new ProductSearchDto();
			request.setKeyword("강남");
			request.setHouseType("아파트"); // 프론트와 맞춘 값
			request.setTradeType("매매");

			// 검색 결과 더미 데이터
			ProductDto p1 = createDummyDto(100L);
			ProductDto p2 = createDummyDto(200L);
			List<ProductDto> list = List.of(p1, p2);

			// Mocking
			given(productDao.search(request)).willReturn(list);

			// when
			List<ProductDto> result = productService.search(request);

			// then
			assertThat(result).hasSize(2);
			assertThat(result).contains(p1, p2);

			// Verify
			then(productDao).should(times(1)).search(request);
		}

		@Test
		@DisplayName("[SUCCESS] 검색 결과가 없을 경우 빈 리스트를 반환한다.")
		void success_no_keyword_returns_empty() {
			// given
			ProductSearchDto request = new ProductSearchDto();
			request.setKeyword("없는아파트");

			// when
			List<ProductDto> result = productService.search(request);

			// then
			assertThat(result).isEmpty();

			// Verify
			then(productDao).should(times(1)).search(request);
		}

		@Test
		@DisplayName("[SUCCESS] 필터 조건이 비어있어도(Null/Empty) DAO를 호출한다.")
		void success_null_keyword_returns_empty() {
			// MyBatis 동적 쿼리에서 null 처리를 하므로 Service는 그냥 넘겨야 함

			// given
			ProductSearchDto request = new ProductSearchDto();

			// Mocking
			given(productDao.search(request)).willReturn(Collections.emptyList());

			// when
			productService.search(request);

			// then
			// DAO가 호출되었는지 확인 (Service에서 null 체크 후 리턴해버리는 로직이 없다면 호출되어야 함)
			then(productDao).should(times(1)).search(request);
		}

	}
}
