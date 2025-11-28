package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.ProductDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 비지니스 로직 테스트")
class ProductServiceTest {

	@InjectMocks
	private ProductServiceImpl productService;
	@Mock
	private ProductDao productDao;

	private ProductDto createDummyDto(Long id) {
		ProductDto dto = ProductDto.builder().jibun("서울시 강남구").houseType("APART").tradeType("SALE").name("SSAFY Apart")
				.dealAmount(10000L).build();

		if (id != null) {
			ReflectionTestUtils.setField(dto, "productId", id);
		}

		return dto;
	}

	@Nested
	@DisplayName("상품 등록 테스트")
	class CreateTest {

		@Test
		@DisplayName("[SUCCESS] 정상적인 데이터가 입력되면 저장 후 생성한 PK를 반환한다.")
		void success() {
			// given
			ProductDto requestDto = createDummyDto(null);
			Long generatedId = 14L;

			BDDMockito.willAnswer(invocation -> {
				ProductDto argument = invocation.getArgument(0);
				ReflectionTestUtils.setField(argument, "productId", generatedId);
				return 1L;
			}).given(productDao).save(ArgumentMatchers.any(ProductDto.class));

			// when
			Long resultId = productService.create(requestDto);

			// then
			assertThat(resultId).isEqualTo(generatedId);
			BDDMockito.then(productDao).should(Mockito.times(1)).save(requestDto);
		}
	}

	@Nested
	@DisplayName("상품 수정 테스트")
	class ModifyTest {

		@Test
		@DisplayName("[SUCCESS] 존재하는 매물 수정 시, 수정된 정보를 반환한다.")
		void success() {
			// given
			Long targetId = 14L;
			ProductDto updateRequest = createDummyDto(null);
			ProductDto updatedResponse = createDummyDto(targetId);

			BDDMockito.given(productDao.findById(targetId)).willReturn(updatedResponse);

			// when
			ProductDto result = productService.modify(targetId, updateRequest);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getProductId()).isEqualTo(targetId);
			assertThat(result.getName()).isEqualTo("SSAFY Apart");

			BDDMockito.then(productDao).should(Mockito.times(1)).update(eq(targetId), any(ProductDto.class));
			BDDMockito.then(productDao).should(Mockito.times(1)).findById(targetId);
		}

		@Test
		@DisplayName("[FAIL] 존재하지 않는 매물 ID로 수정 요청 시 CustomException(PRODUCT_NOT_FOUND)가 발생한다.")
		void fail_not_found() {
			// given
			Long wrongId = 999L;
			ProductDto updateReq = createDummyDto(null);

			BDDMockito.given(productDao.findById(wrongId)).willReturn(null);

			// when
			// then
			assertThatThrownBy(() -> productService.modify(wrongId, updateReq)).isInstanceOf(CustomException.class)
					.extracting("errorCode").isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

			BDDMockito.then(productDao).should(Mockito.times(1)).update(eq(wrongId), any(ProductDto.class));
			BDDMockito.then(productDao).should(Mockito.times(1)).findById(wrongId);
		}
	}
}
