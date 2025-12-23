package com.ssafy.bbb.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.bbb.global.response.ApiResponse;
import com.ssafy.bbb.model.dao.VirtualBankDao;
import com.ssafy.bbb.model.dto.VirtualBankDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
@Tag(name = "Virtual Bank API", description = "결제 수단 관련 API")
public class VirtualBankController {

    private final VirtualBankDao virtualBankDao;

    @GetMapping
    @Operation(summary = "결제 수단 선택", description = "결제 가능한 은행 목록을 반환합니다.")
    public ApiResponse<List<VirtualBankDto>> getBankList() {
        List<VirtualBankDto> banks = virtualBankDao.findAll();

        List<VirtualBankDto> safeBanks = banks.stream()
                .peek(bank -> bank.setWebhookUrl(null))
                .collect(Collectors.toList());

        return ApiResponse.success(safeBanks);
    }
}