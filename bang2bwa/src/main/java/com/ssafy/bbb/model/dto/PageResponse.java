package com.ssafy.bbb.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResponse<T> {
	private List<T> content;	// 실제 데이터 리스트
	private int totalPage;		// 전체 페이지 수
	private int totalCount;		// 전체 데이터 개수
	private int currentPage;	// 현재 페이지 수
	private int size;			// 페이지당 개수
}
