package com.ssafy.bbb.model.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.bbb.global.constant.SggData;
import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.HouseDao;
import com.ssafy.bbb.model.dto.HouseDealDto;
import com.ssafy.bbb.model.dto.HouseInfoDto;
import com.ssafy.bbb.model.dto.ProductDto;
import com.ssafy.bbb.model.enums.HouseType;
import com.ssafy.bbb.model.enums.ReservationStatus;
import com.ssafy.bbb.model.enums.TradeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseServiceImpl implements HouseService {

    private final HouseDao houseDao;
    private final ProductService productService;
    
    @Value("${open-api.service-key}")
    private String serviceKey;
    
    @Getter
    @AllArgsConstructor
    private enum ApiInfo {
        // 매매 (Trade)
        APT_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev", "aptNm", HouseType.APART, true),
        RH_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcRHTrade/getRTMSDataSvcRHTrade", "mhouseNm", HouseType.ONEROOM, true),
        OFFI_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcOffiTrade/getRTMSDataSvcOffiTrade", "offiNm", HouseType.OFFICETEL, true),
        SH_TRADE("https://apis.data.go.kr/1613000/RTMSDataSvcSHTrade/getRTMSDataSvcSHTrade", "houseNm", HouseType.ONEROOM, true),
        
        // 전월세 (Rent)
        APT_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent", "aptNm", HouseType.APART, false),
        RH_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcRHRent/getRTMSDataSvcRHRent", "mhouseNm", HouseType.ONEROOM, false),
        OFFI_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent", "offiNm", HouseType.OFFICETEL, false),
        SH_RENT("https://apis.data.go.kr/1613000/RTMSDataSvcSHRent/getRTMSDataSvcSHRent", "houseNm", HouseType.ONEROOM, false);

        private final String url;
        private final String typeNm;       // JSON에서 건물명을 꺼낼 키
        private final HouseType houseType; // DB에 저장할 타입
        private final boolean isTrade;     // 매매 여부 (true: 매매, false: 전월세)
    }

    @Override
    @Transactional
    public void registHouseDeal(HouseInfoDto infoDto, HouseDealDto dealDto) {
        HouseInfoDto existingHouse = houseDao.selectHouseInfo(infoDto);
        long houseInfoId;

        if (existingHouse != null) {
            houseInfoId = existingHouse.getHouseInfoId();
        } else {
            houseDao.insertHouseInfo(infoDto);
            houseInfoId = infoDto.getHouseInfoId();
        }

        dealDto.setHouseInfoId(houseInfoId);
        houseDao.insertHouseDeal(dealDto);
    }

    // 외부에서 호출하는 메서드는 모든 API를 순회하도록 변경
    @Override
    @Transactional
    public void fetchAndSaveHouseData(String lawdCd, String dealYmd) {
        for (ApiInfo api : ApiInfo.values()) {
            try {
                callApiAndSave(api, lawdCd, dealYmd);
                Thread.sleep(100); 
                
            } catch (Exception e) {
                log.error("API Error [{}]: {}", api.name(), e.getMessage());
            }
        }
    }

    // 실제 API 통신 및 파싱 로직
    private void callApiAndSave(ApiInfo api, String lawdCd, String dealYmd) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader br = null;

        try {
            StringBuilder urlBuilder = new StringBuilder(api.getUrl());
            urlBuilder.append("?serviceKey=" + serviceKey);
            urlBuilder.append("&LAWD_CD=" + lawdCd);
            urlBuilder.append("&DEAL_YMD=" + dealYmd);
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&_type=json");

            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new CustomException(ErrorCode.OPEN_API_ERROR);
            }

            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(sb.toString());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    processItem(item, api, lawdCd);
                }
            }
            
        } finally {
            if (br != null) br.close();
            if (conn != null) conn.disconnect();
        }
    }

    // 개별 아이템 파싱 및 DTO 변환
    private void processItem(JsonNode item, ApiInfo api, String lawdCd) {
        HouseInfoDto infoDto = new HouseInfoDto();
        infoDto.setHouseType(api.getHouseType());
        infoDto.setSggNm(SggData.sggMap.get(lawdCd));
        infoDto.setUmdNm(item.path("umdNm").asText());
        infoDto.setJibun(item.path("jibun").asText());
        infoDto.setBuildName(item.path(api.getTypeNm()).asText());
        infoDto.setBuildYear(item.path("buildYear").asInt(0));

        HouseDealDto dealDto = new HouseDealDto();
        
        // 매매 vs 전월세 금액 파싱 로직 분기
        if (api.isTrade()) {
            String amount = item.path("dealAmount").asText().replace(",", "").trim();
            dealDto.setDealAmount(parseLongSafe(amount) * 10000L);
        } else {
        	String deposit = item.path("deposit").asText("0").replace(",", "").trim();
            String monthly = item.path("monthlyRent").asText("0").replace(",", "").trim();
            
            dealDto.setDealAmount(parseLongSafe(deposit) * 10000L); 
            dealDto.setMonthlyRent(parseLongSafe(monthly) * 10000L);
        }

        Integer year = item.path("dealYear").asInt();
        Integer month = item.path("dealMonth").asInt();
        Integer day = item.path("dealDay").asInt();
        dealDto.setDealDate(LocalDate.of(year, month, day));

        String floor = item.path("floor").asText();
        if (floor != null && !floor.isEmpty() && 
            !floor.contains("층") && !floor.contains("반지하")) floor += "층";
        dealDto.setFloor(floor);

        Double excluUseAr = item.path("excluUseAr").asDouble(0.0);
        dealDto.setExcluUseAr(excluUseAr);

        registHouseDeal(infoDto, dealDto);
        
        registProduct(infoDto, dealDto, api);
    }
    
    /**
     * 공공데이터를 ProductDto 필드에 맞춰 변환하여 등록
     */
    private void registProduct(HouseInfoDto info, HouseDealDto deal, ApiInfo api) {
        // 1. 관리자(시스템) 계정 ID
        Long SYSTEM_AGENT_ID = 1L; 

        // 2. 거래 종류(TradeType) 매핑
        TradeType tradeType;
        if (api.isTrade()) {
            tradeType = TradeType.SALE; // 매매
        } else {
            // 월세가 있으면 월세, 없으면 전세
            if (deal.getMonthlyRent() != null && deal.getMonthlyRent() > 0) {
                tradeType = TradeType.RENT;
            } else {
                tradeType = TradeType.LEASE;
            }
        }

        // 3. 가격 정보 매핑
        Long dealAmount = 0L;
        Long deposit = 0L;
        Integer monthlyRent = 0;

        if (api.isTrade()) {
            dealAmount = deal.getDealAmount(); // 매매가
        } else {
            deposit = deal.getDealAmount(); // 보증금/전세금
            if (deal.getMonthlyRent() != null) {
                monthlyRent = deal.getMonthlyRent().intValue();
            }
        }
        
        // 4. 상세 설명 (desc) 구성
        String description = String.format(
            "%s %s %s 매물 (건축년도: %d년, 전용면적: %.2f㎡)",
            info.getSggNm(), info.getUmdNm(), info.getBuildName(), 
            info.getBuildYear(), deal.getExcluUseAr()
        );

        if (!StringUtils.hasText(info.getSggNm()) || 
        	!StringUtils.hasText(info.getUmdNm()) || 
        	!StringUtils.hasText(info.getJibun()) ||
        	info.getHouseType() == null || // Enum은 null 체크
        	tradeType == null) {
        	    
        	    log.warn("필수 정보 누락으로 매물 생성 건너뜀: {}", info);
        	    return;
        }
        
        // 5. ProductDto 생성 (DTO 필드 기준)
        ProductDto product = ProductDto.builder()
                // --- @NonNull 필수 필드 ---
                .sggNm(info.getSggNm())
                .umdNm(info.getUmdNm())
                .jibun(info.getJibun())
                .houseType(info.getHouseType()) 
                .tradeType(tradeType)           
                .status(ReservationStatus.AVAILABLE) // 기본 상태
                
                // --- 일반 필드 ---
                .name(info.getBuildName())      // 건물 이름
                .desc(description)              // 상세 내용
                .buildYear(info.getBuildYear())
                .excluUseAr(deal.getExcluUseAr())
                .floor(deal.getFloor())
                
                // --- 가격 정보 ---
                .dealAmount(dealAmount)
                .deposit(deposit)
                .monthlyRent(monthlyRent)
                
                // --- 기타 (기본값 설정) ---
                .aptDong("")    
                .landAr(0.0)    
                .totalFloorAr(0.0)
                .plottageAr(0.0)
                .latitude(0.0)  
                .longitude(0.0) 
                .isAiRecommended(false)
                .build();

        // 6. DB 저장
        try {
            productService.create(SYSTEM_AGENT_ID, product, null); 
        } catch (Exception e) {
            log.warn("가상 매물 등록 실패: {}", e.getMessage());
        }
    }
    
    // 숫자 파싱용 유틸 (NumberFormatException 방지)
    private long parseLongSafe(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public void fetchSggData(String dealYmd) {
        Set<String> sggCodes = SggData.sggMap.keySet();
        for (String lawdCd : sggCodes) {
            try {
                fetchAndSaveHouseData(lawdCd, dealYmd);
                Thread.sleep(1000); 
            } catch (Exception e) {
                log.error("SGG Error [{}]: {}", lawdCd, e.getMessage());
            }
        }
    }
}