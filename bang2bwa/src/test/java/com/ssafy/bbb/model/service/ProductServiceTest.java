package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.util.FileStore;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 비지니스 로직 테스트")
class ProductServiceTest {

	@InjectMocks
	private ProductServiceImpl productService;
	@Mock
	private ProductDao productDao;

	@Mock
	private FileStore fileStore; // 파일 등록, 수정 기능을 테스트 하기위한 Mock 추가

	// 더미 DTO 생성
	private ProductDto createDummyDto(Long id) {
		ProductDto dto = ProductDto.builder().jibun("서울시 강남구").houseType("APART").tradeType("SALE").name("SSAFY Apart")
				.dealAmount(10000L).build();

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
			Long resultId = productService.create(requestDto, files);

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

			willAnswer(invocation -> {
				ProductDto arg = invocation.getArgument(0);
				ReflectionTestUtils.setField(arg, "productId", generatedId);
				return 1L;
			}).given(productDao).save(any(ProductDto.class));

			// when
			Long resultId = productService.create(requestDto, emptyFiles);

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

			// (A) 파일 삭제 로직 Mocking
			// Service 내부에서 Files.delete(path)를 호출하므로, 실제 존재하는 임시 파일을 만들어 경로를 넘겨줘야 에러가 안남.
			Path tempFile = Files.createTempFile("test_delete", ".jpg");
			List<String> deletePaths = List.of(tempFile.toString());

			given(productDao.findSavePathByIds(deleteImageIds)).willReturn(deletePaths);

			// (B) 파일 추가 로직 Mocking
			given(newFile.isEmpty()).willReturn(false);
			given(fileStore.storeFile(any(), eq(targetId))).willReturn(createDummyImageDto(targetId));

			// (C) 최종 조회 Mocking
			given(productDao.findById(targetId)).willReturn(updatedResponse);
			// 조회된 매물에 이미지 리스트 세팅 (빈 리스트라도)
			given(productDao.findImagesByProductId(targetId)).willReturn(new ArrayList<>());

			// when
			ProductDto result = productService.modify(targetId, updateRequest, newFiles);

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
			assertThatThrownBy(() -> productService.modify(wrongId, updateReq, files))
					.isInstanceOf(CustomException.class).extracting("errorCode").isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("상품 검색 테스트")
	class SearchTest {

		@Test
		@DisplayName("[SUCCESS] 타입과 검색어가 모두 있을 때 정상 조회된다.")
		void success_with_keyword_and_type() {
			// given
			String keyword = "강남";
			String type = HouseType.APART.toString();

			// 검색 결과 더미 데이터
			ProductDto p1 = createDummyDto(100L);
			ProductDto p2 = createDummyDto(200L);
			List<ProductDto> list = List.of(p1, p2);

			// Mocking
			given(productDao.search(keyword, type)).willReturn(list);

			// when
			List<ProductDto> result = productService.search(keyword, type);

			// then
			assertThat(result).hasSize(2);
			assertThat(result).contains(p1, p2);

			// Verify
			then(productDao).should(times(1)).search(keyword, type);
		}

		@Test
		@DisplayName("[SUCCESS] 검색어가 없으면 빈 리스트를 반환한다. (Service 내부 정책)")
		void success_no_keyword_returns_empty() {
			// given
			String keyword = ""; // 빈 문자열
			String type = null;

			// when
			List<ProductDto> result = productService.search(keyword, type);

			// then
			assertThat(result).isEmpty();

			// Verify: DAO 호출 없이 바로 리턴했는지 확인
			then(productDao).should(never()).search(any(), any());
		}

		@Test
		@DisplayName("[SUCCESS] 검색어가 Null이면 빈 리스트를 반환한다.")
		void success_null_keyword_returns_empty() {
			// given
			String keyword = null;
			String type = HouseType.ONEROOM.toString();

			// when
			List<ProductDto> result = productService.search(keyword, type);

			// then
			assertThat(result).isEmpty();

			// Verify
			then(productDao).should(never()).search(any(), any());
		}

		@Test
		@DisplayName("[SUCCESS] 검색 결과가 없을 경우 빈 리스트를 반환한다.")
		void success_no_result() {
			// given
			String keyword = "없는아파트";
			String type = HouseType.APART.toString();

			// Mocking: DAO가 빈 리스트 반환
			given(productDao.search(keyword, type)).willReturn(Collections.emptyList());

			// when
			List<ProductDto> result = productService.search(keyword, type);

			// then
			assertThat(result).isEmpty();

			// Verify
			then(productDao).should(times(1)).search(keyword, type);
		}

	}
}
