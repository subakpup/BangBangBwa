<script setup>
import { ref, computed, onMounted } from 'vue';
// HTML의 <i data-lucide="..."> 에 해당하는 아이콘들을 다 가져옵니다.
import { Search, Sparkles, ArrowUpDown, CheckCircle, Heart, Bot, X, Loader2 } from 'lucide-vue-next';

// --- 상태 변수 (State) ---
const showAiModal = ref(false);
const safetyLevel = ref('보통');
const isSearching = ref(false);
const mapInstance = ref(null);
const markers = ref([]);
const infowindows = ref([]);

// --- 더미 데이터 ---
const properties = ref([
  { id: 1, type: '원룸', price: '월세 1000/60', address: '강남구 역삼동', desc: '풀옵션, 역세권 도보 5분', tags: ['역세권', '치안우수'], lat: 37.500, lng: 127.035, image: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80', liked: false, aiVerified: true },
  { id: 2, type: '오피스텔', price: '전세 2억 5천', address: '서초구 서초동', desc: '신축, 고층 뷰 맛집', tags: ['신축', '주차가능'], lat: 37.495, lng: 127.020, image: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80', liked: true, aiVerified: true },
  { id: 3, type: '투룸', price: '월세 3000/80', address: '송파구 잠실동', desc: '넓은 거실, 베란다 있음', tags: ['반려동물', '공원인근'], lat: 37.510, lng: 127.080, image: 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80', liked: false, aiVerified: false },
  { id: 4, type: '아파트', price: '매매 15억', address: '강남구 도곡동', desc: '학군 우수, 대단지', tags: ['학군', '대단지'], lat: 37.490, lng: 127.050, image: 'https://images.unsplash.com/photo-1484154218962-a1c002085d2f?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80', liked: false, aiVerified: true },
  { id: 5, type: '원룸', price: '월세 500/45', address: '관악구 봉천동', desc: '가성비 최고, 서울대입구역', tags: ['저렴', '시장인근'], lat: 37.480, lng: 126.950, image: 'https://images.unsplash.com/photo-1596178060671-7a8d2a6b4c03?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80', liked: false, aiVerified: true },
]);

const filteredProperties = computed(() => properties.value);

// --- 메서드 (Methods) ---
const initMap = () => {
  const container = document.getElementById('map');
  const options = {
    center: new window.kakao.maps.LatLng(37.500, 127.035),
    level: 5
  };
  mapInstance.value = new window.kakao.maps.Map(container, options);

  // 줌 컨트롤
  const zoomControl = new window.kakao.maps.ZoomControl();
  mapInstance.value.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT);

  updateMarkers();
};

const updateMarkers = () => {
  markers.value.forEach(m => m.setMap(null));
  markers.value = [];
  infowindows.value.forEach(iw => iw.close());
  infowindows.value = [];

  properties.value.forEach(p => {
    const position = new window.kakao.maps.LatLng(p.lat, p.lng);
    const marker = new window.kakao.maps.Marker({ position: position, map: mapInstance.value });

    const iwContent = `<div class="custom-infowindow"><div style="font-weight:bold;">${p.price}</div><div>${p.type}</div></div>`;
    const infowindow = new window.kakao.maps.InfoWindow({ content: iwContent });

    window.kakao.maps.event.addListener(marker, 'click', () => {
      infowindows.value.forEach(iw => iw.close());
      infowindow.open(mapInstance.value, marker);
      focusMap(p);
    });

    markers.value.push(marker);
    infowindows.value.push(infowindow);
  });
};

const focusMap = (item) => {
  if (mapInstance.value) {
    const moveLatLon = new window.kakao.maps.LatLng(item.lat, item.lng);
    mapInstance.value.panTo(moveLatLon);

    const index = properties.value.findIndex(p => p.id === item.id);
    if (index !== -1 && markers.value[index]) {
      infowindows.value.forEach(iw => iw.close());
      infowindows.value[index].open(mapInstance.value, markers.value[index]);
    }
  }
};

const toggleLike = (item) => { item.liked = !item.liked; };

const simulateAiSearch = () => {
  if (isSearching.value) return;
  isSearching.value = true;
  setTimeout(() => {
    alert(`AI 분석 완료! 주인님, ${safetyLevel.value} 치안 수준에 맞는 최적의 매물을 찾았습니다.`);
    isSearching.value = false;
    showAiModal.value = false;
  }, 2000);
};

// --- 마운트 시 실행 ---
onMounted(() => {
  if (window.kakao && window.kakao.maps) {
    initMap();
  } else {
    const script = document.createElement('script');
    script.onload = () => window.kakao.maps.load(initMap);
    // script.src는 index.html에 있으므로 생략 가능하나, 안전장치로 로드 대기
  }
});
</script>

<template>
  <div class="flex flex-col h-full">

    <div class="bg-bg border-b border-accent px-6 py-3 flex flex-wrap items-center gap-3 shadow-sm z-20">
      <div class="relative">
        <input type="text" placeholder="지역, 지하철, 학교 검색"
          class="pl-9 pr-4 py-2 border border-secondary rounded-md text-sm focus:outline-none focus:border-primary w-64 bg-white">
        <Search class="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
      </div>

      <div class="w-px h-6 bg-secondary mx-2"></div>

      <select
        class="px-3 py-2 border border-secondary rounded-md text-sm bg-white hover:border-primary cursor-pointer text-gray-700">
        <option>원룸, 투/쓰리룸</option>
        <option>오피스텔</option>
        <option>아파트</option>
      </select>
      <select
        class="px-3 py-2 border border-secondary rounded-md text-sm bg-white hover:border-primary cursor-pointer text-gray-700">
        <option>월세/전세/매매</option>
      </select>

      <button @click="showAiModal = true"
        class="ml-auto flex items-center gap-2 bg-gradient-to-r from-primary to-secondary text-white px-4 py-2 rounded-full text-sm font-bold shadow-md hover:shadow-lg transform hover:-translate-y-0.5 transition">
        <Sparkles class="w-4 h-4" />
        AI 맞춤 추천
      </button>
    </div>

    <div class="flex flex-1 overflow-hidden relative h-full">
      <aside class="w-[400px] bg-white flex flex-col border-r border-accent shadow-xl z-10 flex-shrink-0">
        <div class="p-4 border-b border-gray-100 bg-bg flex justify-between items-center">
          <h2 class="font-bold text-gray-700">매물 리스트 <span class="text-primary">{{ filteredProperties.length }}</span>개
          </h2>
          <div class="text-xs text-gray-500 cursor-pointer hover:text-primary flex items-center">
            <ArrowUpDown class="w-3 h-3 mr-1" /> 추천순
          </div>
        </div>

        <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-3">
          <div v-for="item in filteredProperties" :key="item.id" @click="focusMap(item)"
            class="flex bg-white border border-gray-100 rounded-lg overflow-hidden cursor-pointer hover:border-primary hover:shadow-md transition group h-32">
            <div class="w-32 h-full bg-gray-200 relative overflow-hidden flex-shrink-0">
              <img :src="item.image" class="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                alt="매물 사진">
              <div v-if="item.aiVerified"
                class="absolute top-1 left-1 bg-blue-500 text-white text-[10px] px-1.5 py-0.5 rounded-full flex items-center gap-1 opacity-90">
                <CheckCircle class="w-3 h-3" /> AI검수
              </div>
            </div>
            <div class="flex-1 p-3 flex flex-col justify-between">
              <div>
                <div class="flex justify-between items-start">
                  <span class="text-[11px] text-white bg-secondary px-1.5 py-0.5 rounded">{{ item.type }}</span>
                  <Heart @click.stop="toggleLike(item)"
                    :class="item.liked ? 'fill-red-500 text-red-500' : 'text-gray-300'"
                    class="w-4 h-4 hover:scale-110 transition" />
                </div>
                <div class="font-bold text-lg text-gray-800 mt-1">{{ item.price }}</div>
                <div class="text-xs text-gray-500 truncate mt-0.5">{{ item.address }}</div>
                <div class="text-xs text-gray-500 mt-0.5">{{ item.desc }}</div>
              </div>
              <div class="flex gap-1 mt-2">
                <span v-for="tag in item.tags" :key="tag"
                  class="text-[10px] text-primary bg-bg border border-accent px-1.5 py-0.5 rounded">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <div id="map" class="flex-1 h-full bg-gray-100 relative"></div>
    </div>

    <div v-if="showAiModal" class="fixed inset-0 bg-black bg-opacity-50 z-[9999] flex items-center justify-center p-4">
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden animate-fade-in-up">
        <div class="bg-primary p-5 text-white flex justify-between items-start">
          <div>
            <h3 class="text-xl font-bold flex items-center gap-2">
              <Bot class="w-6 h-6" /> AI 맞춤 매물 추천
            </h3>
            <p class="text-sm text-accent mt-1 opacity-90">원하는 조건을 입력하면 AI가 딱 맞는 집을 찾아드려요.</p>
          </div>
          <button @click="showAiModal = false" class="text-white hover:bg-white/20 rounded-full p-1">
            <X class="w-5 h-5" />
          </button>
        </div>

        <div class="p-6 space-y-5">
          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">예산 범위</label>
            <input type="range" class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-primary"
              min="0" max="100" step="5">
            <div class="flex justify-between text-xs text-gray-500 mt-1"><span>최소</span><span>최대 10억+</span></div>
          </div>

          <div>
            <label class="block text-sm font-bold text-gray-700 mb-2">치안 민감도</label>
            <div class="flex gap-2">
              <button v-for="level in ['보통', '중요', '매우 중요']" :key="level" @click="safetyLevel = level"
                :class="safetyLevel === level ? 'bg-secondary text-white border-secondary' : 'bg-white text-gray-600 border-gray-300'"
                class="flex-1 py-2 border rounded-lg text-sm transition">
                {{ level }}
              </button>
            </div>
          </div>
        </div>

        <div class="p-5 border-t border-gray-100 bg-bg flex justify-end">
          <button @click="simulateAiSearch"
            class="bg-primary hover:bg-[#96755e] text-white px-6 py-2.5 rounded-lg font-bold shadow-lg transition flex items-center gap-2">
            <span v-if="!isSearching">추천 받기</span>
            <span v-else class="flex items-center gap-2">
              <Loader2 class="animate-spin w-4 h-4" /> 분석 중...
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>