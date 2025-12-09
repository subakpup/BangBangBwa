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

export const infraCategories = [
    { name: '지하철', code: 'SW8', type: 'category', icon: '🚇' },
    { name: '버스', keyword: '버스정류장', type: 'keyword', icon: '🚌' },
    { name: '편의점', code: 'CS2', type: 'category', icon: '🏪' },
    { name: '학교', code: 'SC4', type: 'category', icon: '🏫' },
    { name: '대형마트', code: 'MT1', type: 'category', icon: '🛒' },
    { name: '병원', code: 'HP8', type: 'category', icon: '🏥' },
    { name: '약국', code: 'PM9', type: 'category', icon: '💊' },
    { name: '세탁소', keyword: '세탁소', type: 'keyword', icon: '👕' },
    { name: '치안센터', code: 'PO3', type: 'category', icon: '👮' }, // PO3는 공공기관
    { name: '공원', keyword: '공원', type: 'keyword', icon: '🌳' },
];
