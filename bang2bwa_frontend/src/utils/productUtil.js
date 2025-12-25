import { api } from '@/api';
import noImage from '@/assets/no-image.jpg'
import { TrainFront, Bus, Store, School, ShoppingCart, Hospital, Pill, Shirt, Siren, Trees } from 'lucide-vue-next';

// 매물 종류 매핑
export const typeMap = {
  APART: "아파트",
  ONEROOM: "원룸",
  OFFICETEL: "오피스텔",
};

// 거래 종류 매핑
export const tradeTypeMap = {
  매매: "SALE",
  전세: "LEASE",
  월세: "RENT",
};

export const tradeTypeMapen2ko = {
  SALE: "매매",
  LEASE: "전세",
  RENT: "월세",
};

// 예약 상태 매핑
export const statusMap = {
   SOLD_OUT: "계약 확정",
   QUIT: "예약 종료",
   REPORETED: "신고됨",
   RESERVED: "예약 확정",
   PENDING: "예약 대기",
   AVAILABLE: "예약 가능",
};

// 억, 만 단위 포맷팅 함수
export const formatMoney = (amount) => {
  if (!amount) return "0";

  const eok = Math.floor(amount / 100000000);
  const man = Math.floor((amount % 100000000) / 10000);

  let result = "";
  if (eok > 0) result += `${eok}억`;
  if (man > 0) result += ` ${man.toLocaleString()}`;

  return result.trim();
};

// 가격 포맷팅 함수
export const formatPrice = (item) => {
  if (!item) return "";

  const type = item.tradeType;
  let price = 0;
  let rent = 0;

  if (type === "매매" || type === "SALE") {
    price = item.dealAmount;
  } else {
    price = item.deposit;
    rent = item.monthlyRent;
  }

  const formattedPrice = formatMoney(price);

  if (type === "매매" || type === "SALE") {
    return `매매 ${formattedPrice}`;
  } else if (type === "전세" || type === "LEASE") {
    return `전세 ${formattedPrice}`;
  } else {
    const formattedRent = rent > 0 ? Math.floor(rent / 10000) : 0;
    return `월세 ${formattedPrice} / ${formattedRent}`;
  }
};

// 주소 포맷팅 함수
export const formatAddress = (item) => {
    if (!item) return '';
    const sgg = item.sggNm || '';
    const umd = item.umdNm || '';
    const jibun = item.jibun || '';
    
    // 공백이 여러 개인 경우 하나로 줄이고 앞뒤 공백 제거
    return `${sgg} ${umd} ${jibun}`.replace(/\s+/g, ' ').trim();
};

export const infraCategories = [
    { name: '지하철', code: 'SW8', type: 'category', icon: TrainFront },
    { name: '버스', keyword: '버스정류장', type: 'keyword', icon: Bus },
    { name: '편의점', code: 'CS2', type: 'category', icon: Store },
    { name: '학교', code: 'SC4', type: 'category', icon: School },
    { name: '대형마트', code: 'MT1', type: 'category', icon: ShoppingCart },
    { name: '병원', code: 'HP8', type: 'category', icon: Hospital },
    { name: '약국', code: 'PM9', type: 'category', icon: Pill },
    { name: '세탁소', keyword: '세탁소', type: 'keyword', icon: Shirt },
    { name: '치안센터', code: 'PO3', type: 'category', icon: Siren },
    { name: '공원', keyword: '공원', type: 'keyword', icon: Trees },
];

export const getProductMainImage = (item) => {
  if (!item.images || item.images.length === 0) {
    return noImage;
  }

  const img = item.images[0];
  const path = img.savePath;

  if (path) {
    const baseUrl = api.defaults.baseURL;
    return `${baseUrl}/images/${path}`; 
  }

  return noImage;
};

export const getProductMainImageForReservation = (item) => {
  if (!item.productImage) {
    return noImage;
  }
  const path = item.productImage;

  if (path) {
    const baseUrl = api.defaults.baseURL;
    return `${baseUrl}/images/${path}`;
  }

  return noImage;
};