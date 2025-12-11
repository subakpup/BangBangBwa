package com.ssafy.bbb.model.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.bbb.global.exception.CustomException;
import com.ssafy.bbb.global.exception.ErrorCode;
import com.ssafy.bbb.model.dao.HouseDao;
import com.ssafy.bbb.model.dto.HouseDealDto;
import com.ssafy.bbb.model.dto.HouseInfoDto;
import com.ssafy.bbb.model.enums.HouseType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseServiceImpl implements HouseService {

	private final HouseDao houseDao;

	@Override
	@Transactional
	public void registHouseDeal(HouseInfoDto infoDto, HouseDealDto dealDto) {
		// DB에 이미 존재하는 아파트인지 확인
		HouseInfoDto existingHouse = houseDao.selectHouseInfo(infoDto);

		long houseInfoId;

		if (existingHouse != null) {
			// 이미 존재한다면 기존 아파트의 ID를 가져옴
			houseInfoId = existingHouse.getHouseInfoId();
		} else {
			// 존재하지 않다면 아파트 등록
			houseDao.insertHouseInfo(infoDto);
			houseInfoId = infoDto.getHouseInfoId();
		}

		// 거래 정보 등록
		dealDto.setHouseInfoId(houseInfoId); // 아이디를 거래 정보에 세팅
		houseDao.insertHouseDeal(dealDto);
	}

	@Override
	@Transactional
	public void fetchAndSaveHouseData(String lawdCd, String dealYmd) {
		HttpURLConnection conn;
		BufferedReader br;
		try {
			// 공공데이터 API URL
			String serviceKey = "88L1UjILz99hAGjeVnOQKq1QEKhwNJzs4suztNk60jJlfM7xRA7YMR%2BYqS%2BLDxOJ65KtuAL8MZolG5Qn77XShg%3D%3D";

			// 받으려는 매물 타입
			String typeNm = "aptNm";
			HouseType type = HouseType.APART;

			// 공공데이터 API URL
			StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade");
			urlBuilder.append("?serviceKey=" + serviceKey);
			urlBuilder.append("&LAWD_CD=" + lawdCd);
			urlBuilder.append("&DEAL_YMD=" + dealYmd);
			urlBuilder.append("&_type=json");

			// 요청
			URL url = new URL(urlBuilder.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");

			// 응답 읽기
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

			br.close();
			conn.disconnect();

			// JSON 파싱 및 DB 저장
			String jsonResult = sb.toString();
			ObjectMapper mapper = new ObjectMapper();

			JsonNode root = mapper.readTree(jsonResult);
			JsonNode items = root.path("response").path("body").path("items").path("item");

			if (items.isArray()) {
				for (JsonNode item : items) {
					HouseInfoDto infoDto = new HouseInfoDto(); // 파싱한 데이터를 DTO에 매핑

					// house_type, sgg_nm, umd_nm, jibun, build_year, build_name, plottage_ar, total_floor_ar, land_ar
					infoDto.setHouseType(type);
					infoDto.setBuildName(item.path(typeNm).asText());
					infoDto.setUmdNm(item.path("umdNm").asText());
					infoDto.setJibun(item.path("jibun").asText());
					infoDto.setBuildYear(item.path("buildYear").asInt(0));

					Integer buildYear = item.path("buildYear").asInt(0);
					infoDto.setBuildYear(buildYear);

					// 거래 정보
					HouseDealDto dealDto = new HouseDealDto();

					// 거래 금액
					String dealAmount = item.path("dealAmount").asText().replace(",", "").trim();
					dealDto.setDealAmount(Long.parseLong(dealAmount) * 10000L);

					// 거래 날짜
					Integer year = item.path("dealYear").asInt();
					Integer month = item.path("dealMonth").asInt();
					Integer day = item.path("dealDay").asInt();
					dealDto.setDealDate(LocalDate.of(year, month, day));

					// 층수
					String floor = item.path("floor").asText();
					if (floor != null && !floor.isEmpty() && 
							!floor.contains("층") && !floor.contains("반지하") && !floor.contains("옥탑")) floor += "층";
					dealDto.setFloor(floor);

					// 면적
					Double excluUseAr = item.path("excluUseAr").asDouble(0.0);
					dealDto.setExcluUseAr(excluUseAr);

					// 서비스 저장 로직 호출
					registHouseDeal(infoDto, dealDto);
				}
			}

		} catch (CustomException e) {
            throw e; 
		} catch (Exception e) {
			throw new CustomException(ErrorCode.DATA_PARSING_ERROR);
		}
	}
}
