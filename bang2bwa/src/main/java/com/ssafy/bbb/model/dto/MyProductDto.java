package com.ssafy.bbb.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyProductDto {
	private String address;				// 주소
	private String name; 				// 매물의 이름
	private HouseType houseType;	 	// 매물 타입(아파트, 오피스텔, 원룸)
	private TradeType tradeType; 		// 거래 타입
	private ReservationStatus status; 	// 예약 상태
	private LocalDateTime visitDate;	// 예약 일자(예약이 되어 있다면)
	private Double excluUseAr; 			// 전용면적
	private Integer buildYear; 			// 건축년도

	private String floor;				// 층
	private String aptDong;				// 동

	// 가격으로 front 에서 합쳐서 제공
	private Long dealAmount;			// 매매가
	private Long deposit;				// 보증금
	private Long monthlyRent;			// 월세
	
	private Long productId;
	private Long reservationId;
	
	private List<ProductImageDto> images; // 매물 사진 리스트
	
	// 이미지를 설정할 setter 메서드
	public void setImages(List<ProductImageDto> images) {
		this.images = images;
	}

}
