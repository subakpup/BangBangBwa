package com.ssafy.bbb.model.dto;

import lombok.Data;

@Data
public class ProductSearchDto {
    private String keyword; // 검색어
    private String houseType; // 매물 종류
    private String tradeType; // 거래 종류
    private String excluUseAr; // 전용 면적
    private String floor; // 층
    
    private Double minLat; // 남서쪽 위도
    private Double minLng; // 남서쪽 경도
    private Double maxLat; // 북동쪽 위도
    private Double maxLng; // 북동쪽 경도

    private Double userLat; // 내 위치 위도 (중심점)
    private Double userLng; // 내 위치 경도 (중심점)
    private Double radius;  // 반경 (km 단위, 예: 3.0)
    
    private int page = 1;       // 현재 페이지 (기본값 1)
    private int limit = 20;     // 한 번에 가져올 개수 (기본값 20)
    
    public int getOffset() {
        return (this.page - 1) * this.limit;
    }
}