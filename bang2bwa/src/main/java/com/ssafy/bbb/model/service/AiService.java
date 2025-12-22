package com.ssafy.bbb.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.ProductDao;
import com.ssafy.bbb.model.dto.AiRecommendDto;
import com.ssafy.bbb.model.dto.AiSearchDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiService {

    private final ChatClient chatClient;
    private final ProductDao productDao;

    public List<ProductDto> recommend(AiSearchDto request) {
    	convertUnits(request);
    	
    	log.info("🔍 검색 시작, 조건: {}", request);

        // 1. DB 1차 필터링
        List<ProductDto> candidates = productDao.aiSearchProduct(request);

        if (candidates.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND_FOR_AI);
        }

        // 2. 프롬프트 포맷팅
        String candidatesText = candidates.stream()
                .map(p -> {
                    String priceInfo = formatPrice(p);
                    String safeDesc = p.getDesc() != null ? 
                        (p.getDesc().length() > 100 ? p.getDesc().substring(0, 100) + "..." : p.getDesc()) : "설명 없음";

                    return String.format("""
                            - [매물번호: %d] %s
                              * 위치: %s %s (%s)
                              * 가격: %s
                              * 스펙: %s층, 전용 %.1f㎡, %s
                              * 상세설명: %s
                            """,
                            p.getProductId(),
                            p.getName(),
                            p.getSggNm(), p.getUmdNm(), p.getJibun(),
                            priceInfo,
                            p.getFloor(), p.getExcluUseAr(), p.getHouseType(),
                            safeDesc);
                })
                .collect(Collectors.joining("\n\n"));

        BeanOutputConverter<List<AiRecommendDto>> converter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<List<AiRecommendDto>>() {});

        // 4. 프롬프트 작성
        String prompt = String.format("""
                [고객의 요구사항]
                - 희망 지역: %s
                - 선호 옵션 및 인프라: %s (이 조건이 포함된 매물을 우선적으로 추천해줘)
                
                [분석할 매물 리스트]
                %s
                
                [지시사항]
                1. 매물 리스트를 분석하여 고객의 '선호 옵션'에 가장 부합하는 상위 5개를 선정해.
                2. 상세설명(Description)이나 위치 정보를 보고 인프라가 언급된 곳을 찾아서 가산점을 줘.
                3. 결과는 반드시 JSON 리스트 포맷으로, 필드는 'productId', 'reason'만 포함해.
                4. 'reason'은 해당 매물을 추천한 구체적인 이유를 한 문장으로 작성해.
                5. 모든 인프라는 500m 이내에 존재해야해.
                """,
                request.getLocation(),
                String.join(", ", request.getOptions()),
                candidatesText);

        // 5. AI 호출
        List<AiRecommendDto> aiResults = chatClient.prompt()
                                            .user(prompt)
                                            .call()
                                            .entity(converter);

        // 6. 결과 재조립 (AI 추천 상단 배치 + 나머지 하단 배치)
        List<ProductDto> sortedList = new ArrayList<>();
        
        // 검색 속도 향상을 위해 Map으로 변환 (List 순회 X -> Map 조회 O)
        Map<Long, ProductDto> candidateMap = candidates.stream()
                .collect(Collectors.toMap(ProductDto::getProductId, Function.identity()));

        // 6-1. AI가 추천한 애들 먼저 넣기
        for (AiRecommendDto aiItem : aiResults) {
            ProductDto p = candidateMap.get(aiItem.getProductId());
            if (p != null) {
                p.setAiRecommended(true);
                p.setAiReason(aiItem.getReason());
                sortedList.add(p);
                candidateMap.remove(aiItem.getProductId()); // 맵에서 제거 (중복 방지)
            }
        }

        // 6-2. 선택받지 못한 나머지 애들 뒤에 붙이기
        sortedList.addAll(candidateMap.values());

        return sortedList;
    }

    private String formatPrice(ProductDto p) {
        if (p.getTradeType().equals(TradeType.SALE)) {
            return String.format("매매 %d만원", p.getDealAmount());
        } else if (p.getTradeType().equals(TradeType.LEASE)) {
            return String.format("전세 %d만원", p.getDeposit());
        } else {
            return String.format("월세 보증금 %d만원 / 월 %d만원", p.getDeposit(), p.getMonthlyRent());
        }
    }
    
    private void convertUnits(AiSearchDto dto) {
        long UNIT = 10000L; 

        // 1. 매매가 (dealAmount)
        if (dto.getDealAmount() != null) {
            if (dto.getDealAmount().getMin() != null) {
                dto.getDealAmount().setMin(dto.getDealAmount().getMin() * UNIT);
            }
            if (dto.getDealAmount().getMax() != null) {
                dto.getDealAmount().setMax(dto.getDealAmount().getMax() * UNIT);
            }
        }

        // 2. 보증금 (deposit)
        if (dto.getDeposit() != null) {
            if (dto.getDeposit().getMin() != null) {
                dto.getDeposit().setMin(dto.getDeposit().getMin() * UNIT);
            }
            if (dto.getDeposit().getMax() != null) {
                dto.getDeposit().setMax(dto.getDeposit().getMax() * UNIT);
            }
        }

        // 3. 월세 (monthlyRent)
        if (dto.getMonthlyRent() != null) {
             if (dto.getMonthlyRent().getMin() != null) {
                 dto.getMonthlyRent().setMin(dto.getMonthlyRent().getMin() * UNIT);
             }
             if (dto.getMonthlyRent().getMax() != null) {
                 dto.getMonthlyRent().setMax(dto.getMonthlyRent().getMax() * UNIT);
             }
        }
    }
}
