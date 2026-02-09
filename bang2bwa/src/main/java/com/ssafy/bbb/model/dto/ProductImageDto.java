package com.ssafy.bbb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
	private Long imageId;
	private Long productId;
	private String originalName;
	private String saveName;
	private String savePath;
}
