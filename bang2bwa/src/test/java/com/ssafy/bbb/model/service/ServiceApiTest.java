package com.ssafy.bbb.model.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class ServiceApiTest {

	@Test
	void publicDataApiTest() {
		try {
			// 1. 주인님이 보내주신 "성공한 URL"을 그대로 문자열로 넣습니다.
			// 주의: 이미 인코딩된 키가 들어있으므로, 자바에서 또 인코딩하면 안 됩니다!
			String serviceKey = "88L1UjILz99hAGjeVnOQKq1QEKhwNJzs4suztNk60jJlfM7xRA7YMR%2BYqS%2BLDxOJ65KtuAL8MZolG5Qn77XShg%3D%3D";

			// StringBuilder로 파라미터를 하나씩 붙이지 말고, 완성된 URL을 한 번에 만듭니다.
			String urlStr = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade"
					+ "?serviceKey=" + serviceKey + "&LAWD_CD=11110" // 종로구
					+ "&DEAL_YMD=202401"; // 2024년 1월

			System.out.println("요청 URL: " + urlStr);

			// 2. 연결 시작
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");

			System.out.println("Response code: " + conn.getResponseCode());

			// 3. 데이터 읽기
			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); // 한글 깨짐 방지 UTF-8 추가
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
			}

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();

			// 4. 결과 출력
			System.out.println("결과 데이터:\n" + sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}