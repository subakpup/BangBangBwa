package com.ssafy.bbb.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SggCodeParser {
	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) {
		// 법정동 코드 텍스트 파일
		String inputPath = "src/main/resources/tmp/code.txt";
		
		// 결과를 저장할 파일 경로
		String outputPath = "src/main/resources/tmp/result.txt";
		
		Map<String, String> regionMap = parseSggCode(inputPath);

		log.info("파싱 성공");
		
		saveAsTextFile(regionMap, outputPath);
		
		log.info("저장 성공");
	}

	public static Map<String, String> parseSggCode(String inputPath) {
		Map<String, String> resultMap = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(inputPath, java.nio.charset.Charset.forName("MS949")))) {
			String line;

			while ((line = br.readLine()) != null) {
				// 1. 헤더 라인 스킵
				if (!Character.isDigit(line.charAt(0))) {
					continue;
				}

				// 2. 폐지된 법정동은 제외
				if (line.contains("폐지")) {
					continue;
				}

				// 3. StringTokenizer로 파싱
				StringTokenizer st = new StringTokenizer(line, "\t");

				if (st.hasMoreTokens()) {
					String code = st.nextToken(); // 법정동 코드 (예: 1111000000)
					String name = st.nextToken(); // 법정동 명 (예: 서울특별시 종로구)

					// 6~10번째 자리가 "00000"인지 확인
					if (code.length() == 10 && code.substring(5).equals("00000")) {
						resultMap.put(code.substring(0, 5), name);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("파일을 읽는 중 오류");
		}

		return resultMap;
	}
	
	public static void saveAsTextFile(Map<String, String> data, String outputPath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, StandardCharsets.UTF_8))) {
            
            // Map의 모든 내용(Entry)을 하나씩 꺼내서 파일에 씀
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String line = "sggMap.put(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");";
                bw.write(line);
                bw.newLine(); // 엔터(줄바꿈)
            }
            
            log.error("저장 완료! 경로: " + outputPath);
            
        } catch (IOException e) {
            log.error("쓰기 에러: " + e.getMessage());
        }
    }
}