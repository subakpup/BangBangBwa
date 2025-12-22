package com.ssafy.bbb.model.dto;

import java.util.List;

import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDto {
	private Long productId; // pk

	private @NonNull String jibun; // 지번
	private @NonNull HouseType houseType; // 매물 종류
	private @NonNull TradeType tradeType; // 거래 종류
	private @NonNull ReservationStatus status; // 예약 상태 확인

	private String name; // 건물 이름
	private Integer buildYear; // 건축 년도
	private Double excluUseAr; // 전용 면적

	private String aptDong; // 동 이름
	private String floor; // 층

	private Double landAr; // 대지권 면적
	private Double totalFloorAr; // 연면적
	private Double plottageAr; // 대지면적

	private Long dealAmount; // 매매가격
	private Long deposit; // 보증금
	private Integer monthlyRent; // 월세

	private String sggNm; // 시군구 이름 (fk)
	private String umdNm; // 법정동 (fk)
	private Long agentId; // 공인중개사 (fk)

	private Double latitude; // 위도
	private Double longitude; // 경도

	private String desc; // 상세 내용

	private List<ProductImageDto> images; // 매물 사진 리스트
	private List<Long> deleteImageIds; // (수정시 사용) 삭제할 이미지의 아이디 리스트
	
	private boolean isAiRecommended; // AI 추천 여부
	private String aiReason; // AI 추천 이유

	// 이미지를 설정할 setter 메서드
	public void setImages(List<ProductImageDto> images) {
		this.images = images;
	}

	// 위도, 경도 setter 메서드
	public void setLatLng(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	
	public void setAiRecommended(boolean isAiRecommended) {
		this.isAiRecommended = isAiRecommended;
	}
	
	public void setAiReason(String aiReason) {
		this.aiReason = aiReason;
	}
	
	// 전체 주소 반환
	public String getFullAddress() {
		return sggNm + " " + umdNm + " " + jibun;
	}
}
