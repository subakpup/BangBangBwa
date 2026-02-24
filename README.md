# 🏠 방방봐

![Project Banner](https://img.shields.io/badge/Project-BangBangBwa-AE8B72?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?style=for-the-badge&logo=springboot)
![Vue.js](https://img.shields.io/badge/Vue.js-3-4FC08D?style=for-the-badge&logo=vue.js)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)

> "**방** 구하러 멀리 가지 말고 **방**구석에서 찾아 **봐**!"

## 📅 프로젝트 개요
- **진행 기간**: 2025.11.14 ~ 2025.12.26
- **개발 인원**: 2명

## 👨‍💻 팀원 소개

<div align="left">
  <table style="width: 100%; max-width: 800px; border-collapse: collapse;">
    <tr>
      <td align="center" width="50%">
        <a href="https://github.com/subakpup">
          <img src="https://github.com/subakpup.png" width="120" height="120" style="border-radius: 50%;" alt="함지수"/>
          <br />
          <b>👑 함지수</b>
        </a>
      </td>
      <td align="center" width="50%">
        <a href="https://github.com/Clarus23">
          <img src="https://github.com/Clarus23.png" width="120" height="120" style="border-radius: 50%;" alt="정혜원"/>
          <br />
          <b>🧑‍💻 정혜원</b>
        </a>
      </td>
    </tr>
    <tr>
      <td align="center">
        <b>Full Stack</b>
      </td>
      <td align="center">
        <b>Full Stack</b>
      </td>
    </tr>
  </table>
</div>

## ✨ 주요 기능

### 1. 🗺️ 지도 기반 매물 검색
- **Kakao Maps API**를 활용한 직관적인 지도 서비스
- **반경 검색**: 현재 내 위치 기준 반경 500m 이내 매물 필터링 (하버사인 공식 적용)
- **마커 클러스터링**: 다량의 매물 정보를 깔끔하게 시각화
- **편의시설 찾기**: 내 주변 버스정류장, 편의점, 지하철역 등 인프라 정보 제공

### 2. 🤖 AI 맞춤 매물 추천
- **Spring AI (OpenAI/LLM)** 연동
- 사용자가 원하는 인프라(예: 편의점, 학교, 지하철역 등)를 선택하면 AI가 최적의 매물을 분석하여 추천
- 추천된 매물 위치로 지도 자동 이동 및 하이라이팅

### 3. 🔍 스마트 필터링
- 거래 유형(매매/전세/월세), 가격대, 면적, 층수 등 상세 조건 검색
- 실시간 데이터 반영 및 리스트/지도 동기화

### 4. 💸 결제 시스템
- 방방봐 프로젝트만의 자체 가상 결제 서버 구축
- 원하는 결제 방식으로 가상 결제 진행 가능

### 5. 🧐 노쇼 방지 보증금
- 미끼 상품 및 노쇼 방지를 위한 보증금 시스템
- 노쇼 발생 시 신고를 통해 보증금 몰수 처리

### 6. 😎 동네 커뮤니티
- 동네 주민들만의 커뮤니티 게시판
- 지역을 선택해 해당 지역에 관한 소식 작성 및 공유

## 🛠️ 기술 스택

### **Backend**
| Tech | Detail |
| :-- | :-- |
| **Language** | Java 17 (Liberica JDK) |
| **Framework** | Spring Boot 3.5.9 |
| **Database** | MySQL 8.0 |
| **ORM** | MyBatis |
| **Security** | JWT, Spring Security |

### **Frontend**
| Tech | Detail |
| :-- | :-- |
| **Framework** | Vue.js 3 |
| **State Mngt** | Pinia |
| **Styling** | Tailwind CSS |
| **Map** | Kakao Maps SDK |
| **Icons** | Lucide Vue |

### **Collaboration**
- **IDE**: Visual Studio Code, Spring Tool Suite
- **VCS**: Git, GitHub
- **Communication**: Mattermost, Notion
- **Design**: Figma

## 📐 시스템 아키텍처 & ERD

### System Architecture
```mermaid
graph TB
    User((사용자))
    
    subgraph Frontend ["Frontend (Client Tier)"]
        Vue["Vue.js 3<br>Composition API"]
        Pinia["Pinia<br>State Management"]
        Tailwind["Tailwind CSS<br>UI/UX"]
        KakaoMap["Kakao Maps SDK<br>지도, 마커, 반경 검색"]
        
        Vue <--> Pinia
        Vue <--> Tailwind
        Vue <--> KakaoMap
    end
    
    subgraph Backend ["Backend (Application Tier)"]
        Security["Spring Security & JWT<br>인증 및 인가"]
        
        subgraph Controllers ["Controllers (API Gateway)"]
            PropertyAPI["매물/지도 API"]
            AuthAPI["회원 API"]
            BoardAPI["커뮤니티 API"]
            PayAPI["예약/결제 API"]
        end
        
        subgraph Services ["Service Layer (Business Logic)"]
            SearchLogic["하버사인 검색/필터링 로직"]
            AILogic["Spring AI 추천 로직"]
            TradeLogic["예약/보증금 관리 로직"]
        end
        
        MyBatis["MyBatis<br>Data Access Layer"]
        
        Security --> Controllers
        PropertyAPI --> SearchLogic
        PropertyAPI --> AILogic
        PayAPI --> TradeLogic
        
        SearchLogic --> MyBatis
        AILogic --> MyBatis
        TradeLogic --> MyBatis
        AuthAPI --> MyBatis
        BoardAPI --> MyBatis
    end
    
    subgraph DatabaseLayer ["Data Tier"]
        MySQL[(MySQL 8.0<br>Database)]
    end
    
    subgraph ExternalServices ["External Integrations"]
        OpenAI["OpenAI API<br>LLM 맞춤 추천"]
        PaymentServer["가상 결제 서버"]
        MailServer["SMTP 메일 서버<br>예약 확인 메일"]
    end
    
    User <-->|HTTP/HTTPS| Frontend
    Frontend <-->|REST API JSON| Security
    
    MyBatis <-->|SQL| MySQL
    
    AILogic <-->|Prompt / Response| OpenAI
    TradeLogic <-->|결제 요청 및 검증| PaymentServer
    TradeLogic -->|메일 발송| MailServer

```

### ERD

<div align="center">
    <img src="https://velog.velcdn.com/images/subakpup/post/875a2125-ea15-4341-afa7-b28e86b36822/image.png" alt="ERD" width="80%">
</div>

## 💻 실행 방법

### 1. Backend (Spring Boot)

```bash
# Repository Clone
git clone [Github 주소]

# application.properties 설정 (필수 키 값)
kakao.api.key=YOUR_KAKAO_KEY
openai.api.key=YOUR_OPENAI_KEY

# Run Application
./gradlew bootRun

```

### 2. Frontend (Vue.js)

```bash
# 의존성 설치
pnpm install

# 개발 서버 실행
pnpm dev

```

## 📷 스크린샷

<table width="100%">
  <tr>
    <td align="center" width="50%">
      <img src="https://velog.velcdn.com/images/subakpup/post/d3bc3159-0ea9-496f-8ee8-d6cf68c45b63/image.png" width="400"/><br>
      <b>메인 페이지</b>
    </td>
    <td align="center" width="50%">
      <img src="https://velog.velcdn.com/images/subakpup/post/7025e0f3-bd66-453b-bb45-103380ba6a45/image.png" width="400"/><br>
      <b>매물 리스트</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/1dd7e285-6455-4770-935b-a8f34150eba6/image.png" width="400"/><br>
      <b>매물 상세</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/56ec687b-c256-4159-aa3a-8ef9c13bb4ce/image.png" width="400"/><br>
      <b>인프라 확인</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/071c2739-3080-4aa6-af85-55c517918dd7/image.png" width="400"/><br>
      <b>AI 매물 추천</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/32323b80-5312-4a2b-bbb5-62f9cfa5cd0d/image.png" width="400"/><br>
      <b>AI 추천 모달</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/82b99315-1a5e-4b75-8535-9cb4a0e5ebe8/image.png" width="400"/><br>
      <b>찜</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/46d7785a-66f9-4710-ae46-2bbb7a043ebc/image.png" width="400"/><br>
      <b>찜 목록</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/9ee3923a-64f7-41a1-bcba-ae76d49f039e/image.png" width="400"/><br>
      <b>예약하기</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/14ce5ea8-8798-4a63-9264-81bb5adfb1dc/image.png" width="400"/><br>
      <b>예약금 결제</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/f790ad1a-7c31-4e73-b9d0-f34dc8dd9160/image.png" width="400"/><br>
      <b>마이페이지</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/4383577a-4cbb-4f04-87b5-2463eb843c10/image.png" width="400"/><br>
      <b>내 정보 수정</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/68b8dc31-7f7e-486e-80d8-8a81849ea2ee/image.png" width="400"/><br>
      <b>내 매물 리스트(중개인)</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/9c319a0a-cd29-42f4-a783-45750fbe3cb3/image.png" width="400"/><br>
      <b>예약 확인</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/7f0985e9-7104-4cec-993c-64f42d0592a2/image.png" width="400"/><br>
      <b>예약 확인 메일</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/d2b1c7ff-3cb2-40d8-8e4a-84a5224a54e2/image.png" width="400"/><br>
      <b>예약 관리</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/0ce1524d-376d-430c-a857-3018def8813a/image.png" width="400"/><br>
      <b>게시판</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/0899022f-b5bf-448a-ad32-726895a96be3/image.png" width="400"/><br>
      <b>게시판 검색</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/7c03276a-c0d4-4c4d-8c5d-69ea21d6dd72/image.png" width="400"/><br>
      <b>게시글</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/27d20a9f-25fe-4643-936e-68fe92e77110/image.png" width="400"/><br>
      <b>로그인</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/c452a685-5db6-4ea1-8122-e19d2a791f5a/image.png" width="400"/><br>
      <b>회원가입</b>
    </td>
    <td align="center">
      <img src="https://velog.velcdn.com/images/subakpup/post/5f900c72-e949-4a3d-b74c-7844541082fe/image.png" width="400"/><br>
      <b>비밀번호 찾기</b>
    </td>
  </tr>
</table>
