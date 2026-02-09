package com.ssafy.bbb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualBankDto {
    private Long bankId;       // 은행 PK
    private String bankName;   // 은행 이름
    private String webhookUrl; // 웹훅 URL (중요: 프론트로 내려줄 땐 제외)
}