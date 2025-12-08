// src/data/dummy.js

export const dummyData = {
  // 1. 아파트 (apt)
  apart: [
    { 
      productId: 101, 
      jibun: '서울시 서초구 반포동 20-43', 
      houseType: '아파트', 
      tradeType: '매매', 
      name: '반포 자이', 
      buildYear: 2009,
      excluUseAr: 84.9,
      floor: '15층', // String 타입
      dealAmount: 350000, // 35억
      deposit: 0,
      monthlyRent: 0,
      lat: 37.508, 
      lng: 127.010, 
      image: 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&q=80',
      desc: '한강뷰, 커뮤니티 시설 최상, 랜드마크'
    },
    { 
      productId: 102, 
      jibun: '서울시 강남구 역삼동 755', 
      houseType: '아파트', 
      tradeType: '전세', 
      name: '역삼 래미안', 
      buildYear: 2005,
      excluUseAr: 59.9,
      floor: '8층',
      dealAmount: 0,
      deposit: 120000, // 12억
      monthlyRent: 0,
      lat: 37.495, 
      lng: 127.045, 
      image: 'https://images.unsplash.com/photo-1515263487990-61b07816b324?w=400&q=80',
      desc: '역세권, 학군 우수, 도곡초 배정'
    },
    { 
      productId: 103, 
      jibun: '서울시 강남구 도곡동 527', 
      houseType: '아파트', 
      tradeType: '매매', 
      name: '도곡 렉슬', 
      buildYear: 2006,
      excluUseAr: 119.5,
      floor: '21층',
      dealAmount: 420000, // 42억
      deposit: 0,
      monthlyRent: 0,
      lat: 37.492, 
      lng: 127.050, 
      image: 'https://images.unsplash.com/photo-1460317442991-0ec2aa42a16b?w=400&q=80',
      desc: '매봉역 도보 3분, 대단지 프리미엄'
    }
  ],

  // 2. 원룸 (oneroom)
  oneroom: [
    { 
      productId: 201, 
      jibun: '서울시 강남구 역삼동 820-1', 
      houseType: '원룸', 
      tradeType: '월세', 
      name: '강남 프라임빌', 
      buildYear: 2021,
      excluUseAr: 23.1,
      floor: '3층',
      dealAmount: 0,
      deposit: 1000, // 1000만원
      monthlyRent: 80, // 80만원
      lat: 37.498, 
      lng: 127.028, 
      image: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400&q=80',
      desc: '강남역 도보 5분, 신축 풀옵션'
    },
    { 
      productId: 202, 
      jibun: '서울시 관악구 봉천동 100-1', 
      houseType: '원룸', 
      tradeType: '월세', 
      name: '관악 드림타운', 
      buildYear: 2015,
      excluUseAr: 18.5,
      floor: '반지하', // String 타입 반영
      dealAmount: 0,
      deposit: 300, 
      monthlyRent: 35, 
      lat: 37.480, 
      lng: 126.950, 
      image: 'https://images.unsplash.com/photo-1596178060671-7a8d2a6b4c03?w=400&q=80',
      desc: '서울대입구역 인근, 가성비 최고, 리모델링 완료'
    },
    { 
      productId: 203, 
      jibun: '서울시 강남구 논현동 123-4', 
      houseType: '원룸', 
      tradeType: '전세', 
      name: '논현 스위트', 
      buildYear: 2019,
      excluUseAr: 26.4,
      floor: '옥탑', // String 타입 반영
      dealAmount: 0,
      deposit: 15000, // 1억 5천
      monthlyRent: 0, 
      lat: 37.510, 
      lng: 127.025, 
      image: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400&q=80',
      desc: '루프탑 단독 사용, 낭만 가득'
    }
  ],

  // 3. 오피스텔 (officetel)
  officetel: [
    { 
      productId: 301, 
      jibun: '서울시 서초구 서초동 1303-1', 
      houseType: '오피스텔', 
      tradeType: '전세', 
      name: '서초 파라곤', 
      buildYear: 2018,
      excluUseAr: 45.0,
      floor: '12층',
      dealAmount: 0,
      deposit: 30000, // 3억
      monthlyRent: 0,
      lat: 37.490, 
      lng: 127.020, 
      image: 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=400&q=80',
      desc: '강남대로 인접, 복층 구조, 주차 편리'
    },
    { 
      productId: 302, 
      jibun: '서울시 강남구 삼성동 159', 
      houseType: '오피스텔', 
      tradeType: '월세', 
      name: '삼성 더샵', 
      buildYear: 2020,
      excluUseAr: 33.0,
      floor: '5층',
      dealAmount: 0,
      deposit: 2000, 
      monthlyRent: 150,
      lat: 37.505, 
      lng: 127.060, 
      image: 'https://images.unsplash.com/photo-1484154218962-a1c002085d2f?w=400&q=80',
      desc: '코엑스몰 연결, 보안 철저, 풀옵션'
    }
  ]
};