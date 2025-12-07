<template>
  <div class="flex flex-col h-full w-full relative">
    
    <div class="p-4 border-b border-gray-100 flex items-center gap-3">
      <button @click="emit('close')" class="hover:bg-gray-100 p-2 rounded-full transition">
        <ArrowLeft class="w-5 h-5 text-gray-600" />
      </button>
      <h2 class="font-bold text-lg">매물 상세 정보</h2>
    </div>

    <div class="flex-1 overflow-y-auto custom-scrollbar">
      <div class="h-64 w-full bg-gray-200 relative">
        <img :src="item.image" class="w-full h-full object-cover" alt="상세 이미지">
        <div class="absolute bottom-4 right-4 bg-black/60 text-white px-3 py-1 rounded-full text-xs">
          사진 더보기 1/5
        </div>
      </div>

      <div class="p-6 space-y-6">
        <div>
          <span class="text-xs font-bold text-primary border border-primary px-2 py-0.5 rounded mb-2 inline-block">
            {{ item.houseType }}
          </span>
          <h1 class="text-2xl font-bold text-gray-800">{{ formatPrice(item) }}</h1>
          <p class="text-gray-500 mt-1">{{ item.name }}</p>
        </div>

        <hr class="border-gray-100">

        <div class="grid grid-cols-2 gap-4">
          <div class="flex items-center gap-2 text-gray-600">
            <Ruler class="w-4 h-4 text-gray-400" />
            <span class="text-sm">전용 {{ item.excluUseAr }}㎡</span>
          </div>
          <div class="flex items-center gap-2 text-gray-600">
            <Building class="w-4 h-4 text-gray-400" />
            <span class="text-sm">{{ item.floor }}</span>
          </div>
          <div class="flex items-center gap-2 text-gray-600 col-span-2">
            <MapPin class="w-4 h-4 text-gray-400" />
            <span class="text-sm truncate">{{ item.jibun }}</span>
          </div>
        </div>

        <div class="bg-gray-50 p-4 rounded-lg text-sm text-gray-600 leading-relaxed">
          <h4 class="font-bold text-gray-800 mb-2">상세 설명</h4>
          {{ item.desc }}
          <br><br>
          이 매물은 {{ item.buildYear }}년에 준공되었으며, {{ item.aptDong }}에 위치하고 있습니다.
        </div>
      </div>
    </div>

    <div class="p-4 border-t border-gray-100 flex gap-3">
      <button class="flex-1 border border-gray-300 py-3 rounded-lg font-bold text-gray-700 hover:bg-gray-50 transition">
        찜하기
      </button>
      <button class="flex-[2] bg-primary text-white py-3 rounded-lg font-bold hover:bg-[#96755e] transition flex items-center justify-center gap-2">
        <Phone class="w-4 h-4" /> 문의하기
      </button>
    </div>

  </div>
</template>

<script setup>
import { ArrowLeft, MapPin, Building, Ruler, Phone } from 'lucide-vue-next';

const props = defineProps(['item']);
const emit = defineEmits(['close']);

// 가격 포맷팅 함수
const formatPrice = (item) => {
  const type = item.tradeType; 
  let price = 0;
  let rent = 0;

  if (type === '매매' || type === 'SALE') {
    price = item.dealAmount;
  } else {
    price = item.deposit; 
    rent = item.monthlyRent;
  }

  const formatMoney = (amount) => {
    if (!amount) return '0';
    const eok = Math.floor(amount / 100000000);
    const man = Math.floor((amount % 100000000) / 10000);
    let result = '';
    if (eok > 0) result += `${eok}억`;
    if (man > 0) result += ` ${man.toLocaleString()}`;
    return result.trim();
  };

  const formattedPrice = formatMoney(price);

  if (type === '매매' || type === 'SALE') {
    return `매매 ${formattedPrice}`;
  } else if (type === '전세' || type === 'LEASE') {
    return `전세 ${formattedPrice}`;
  } else {
    const formattedRent = rent > 0 ? Math.floor(rent / 10000) : 0;
    return `월세 ${formattedPrice} / ${formattedRent}`;
  }
};
</script>

<style lang="scss" scoped>

</style>