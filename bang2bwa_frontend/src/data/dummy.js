// src/data/dummy.js

export const dummyData = {
  apt: [
    { 
      productId: 101, 
      jibun: '서울시 서초구 반포동 20-43', 
      houseType: '아파트', 
      tradeType: '매매', 
      name: '반포 자이', 
      buildYear: 2009,
      excluUseAr: 84.9,
      floor: 15,
      dealAmount: 350000, // 단위: 만원
      deposit: 0,
      monthlyRent: 0,
      lat: 37.508, // 프론트 엔드용 더미
      lng: 127.010, // 프론트 엔드용 더미
      image: 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=400&q=80', // 프론트 엔드용 더미
      desc: '한강뷰, 커뮤니티 시설 최상' // 프론트 엔드용 더미
    },
    { 
      productId: 102, 
      jibun: '서울시 강남구 역삼동 755', 
      houseType: '아파트', 
      tradeType: '전세', 
      name: '역삼 래미안', 
      buildYear: 2005,
      excluUseAr: 59.9,
      floor: 8,
      dealAmount: 0,
      deposit: 120000, // 12억
      monthlyRent: 0,
      lat: 37.495, 
      lng: 127.045, 
      image: 'https://images.unsplash.com/photo-1515263487990-61b07816b324?w=400&q=80',
      desc: '역세권, 학군 우수'
    },
  ],
  oneroom: [
    { 
      productId: 201, 
      jibun: '서울시 강남구 역삼동 820-1', 
      houseType: '원룸', 
      tradeType: '월세', 
      name: '강남 프라임', 
      buildYear: 2021,
      excluUseAr: 23.1,
      floor: 3,
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
      floor: 2,
      dealAmount: 0,
      deposit: 500, 
      monthlyRent: 45, 
      lat: 37.480, 
      lng: 126.950, 
      image: 'https://images.unsplash.com/photo-1596178060671-7a8d2a6b4c03?w=400&q=80',
      desc: '서울대입구역 인근, 가성비 최고'
    },
  ],
  officetel: [
    { 
      productId: 301, 
      jibun: '서울시 서초구 서초동 1303-1', 
      houseType: '오피스텔', 
      tradeType: '전세', 
      name: '서초 파라곤', 
      buildYear: 2018,
      excluUseAr: 45.0,
      floor: 12,
      dealAmount: 0,
      deposit: 30000, // 3억
      monthlyRent: 0,
      lat: 37.490, 
      lng: 127.020, 
      image: 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=400&q=80',
      desc: '강남대로 인접, 복층 구조'
    },
  ]
};