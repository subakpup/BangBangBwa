package com.ssafy.bbb.model.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

//@SpringBootTest
public class ApiTest {

	@Test
	@DisplayName("공공데이터포털 API Test")
	void publicDataApiTest() {
		// 공공데이터포털 인증키
		String publicDataKey = "88L1UjILz99hAGjeVnOQKq1QEKhwNJzs4suztNk60jJlfM7xRA7YMR%2BYqS%2BLDxOJ65KtuAL8MZolG5Qn77XShg%3D%3D";

		try {
			// 검색어 및 URL 설정
			String urlStr = "https://apis.data.go.kr/1613000/RTMSDataSvcSHTrade/getRTMSDataSvcSHTrade" + "?serviceKey="
					+ publicDataKey + "&LAWD_CD=11110" // 종로구
					+ "&DEAL_YMD=202401"; // 2024년 1월

			System.out.println("요청 URL: " + urlStr);

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			BufferedReader rd;
			if (responseCode >= 200 && responseCode <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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

			System.out.println("결과 데이터\n" + sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("WMS API Test")
	void safemapWmsApiTest() {
		// 안전지도 WMS 인증키
		String safeMapWmsKey = "1QNJTH0P-1QNJ-1QNJ-1QNJ-1QNJTH0PS9";
		HttpURLConnection conn = null;
		try {
			// URL 설정
			String urlStr = "https://www.safemap.go.kr/openApiService/wms/getLayerData.do" + "?apikey=" + safeMapWmsKey
					+ "&service=WMS&version=1.1.1&request=GetMap"
					+ "&layers=A2SM_CRMNLHSPOT_TOT&styles=A2SM_CrmnlHspot_Tot_Rape" + "&srs=EPSG:4326"
					+ "&bbox=127.0000,37.5000,127.0500,37.5500"
					+ "&width=500&height=500&format=image/png&transparent=true";

			System.out.println("🚩 [시작] 요청 URL: " + urlStr);

			// 리다이렉트
			boolean redirect = true;
			int redirectCount = 0;

			while (redirect && redirectCount < 5) {
				URL url = new URL(urlStr);
				conn = (HttpURLConnection) url.openConnection();

				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
				conn.setInstanceFollowRedirects(false); // 수동으로 처리하기 위해 false 설정
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.setRequestMethod("GET");

				int status = conn.getResponseCode();
				System.out.println(redirectCount + "회차 응답 코드: " + status);

				if (status != HttpURLConnection.HTTP_OK) {
					// 301, 302, 307 등 리다이렉트 발생 시
					if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER || status == 307) {

						// 이동할 주소
						String newUrl = conn.getHeaderField("Location");

						System.out.println("리다이렉트 발생! 이동할 주소: " + newUrl);

						// 연결 해제 후 새 주소로 설정
						conn.disconnect();
						urlStr = newUrl;
						redirectCount++;

					} else {
						// 400, 500 등 에러 발생 시
						System.out.println("❌ 에러 발생! 응답 메시지: " + conn.getResponseMessage());
						redirect = false;
					}
				} else {
					// 접속 성공
					System.out.println("서버 접속 성공!");
					redirect = false;

					// 파일 저장
					File file = new File("test_wms_image.png");
					try (InputStream in = conn.getInputStream()) {
						Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}

					System.out.println("저장 완료!");
				}
			}

		} catch (Exception e) {
			System.out.println("예외 발생");
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	@Test
	@DisplayName("Naver Search API Test")
	void rawStringTest() {
		String CLIENT_ID = "qhDVbmJBXjTuXVrvqjuQ";
		String CLIENT_SECRET = "zsVF7zdfKS";

		// 검색어 및 URL 설정
		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/news.json")
				.queryParam("query", "광주 부동산").queryParam("display", 5).queryParam("start", 1).queryParam("sort", "sim")
				.encode(StandardCharsets.UTF_8).build().toUri();

		// 헤더 설정
		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", CLIENT_ID)
				.header("X-Naver-Client-Secret", CLIENT_SECRET).build();

		// API 호출
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(req, String.class);

		// 결과 출력
		System.out.println("=== 응답 상태 코드: " + response.getStatusCode() + " ===");
		System.out.println("=== 응답 본문 (JSON) ===");
		System.out.println(response.getBody());

		// 검증
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).contains("items");
	}
}