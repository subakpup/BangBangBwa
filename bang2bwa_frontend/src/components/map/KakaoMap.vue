<template>
    <div class="relative w-full h-full">
        <div ref="mapContainer" class="w-full h-full bg-gray-100"></div>

        <div v-if="selectedItem" 
            class="absolute top-4 right-4 z-20 w-36 flex flex-col gap-2 max-h-[90%]">
            
            <div class="bg-white/95 backdrop-blur-sm p-2 rounded-lg shadow-md border border-gray-200 flex flex-col overflow-y-auto custom-scrollbar">
                <div class="text-xs font-bold text-gray-500 mb-2 text-center border-b border-gray-100 pb-2">
                    주변 편의시설
                </div>
                
                <div class="flex flex-col gap-1">
                    <button v-for="cat in infraCategories" :key="cat.name"
                            @click="searchInfrastructure(cat)"
                            class="flex items-center gap-3 p-2 hover:bg-[#ae8b72]/10 rounded-md transition-all duration-200 group text-left">
                        
                        <component 
                            :is="cat.icon" 
                            class="w-5 h-5 text-gray-400 group-hover:text-[#ae8b72] group-hover:scale-110 transition-transform" 
                            stroke-width="2"
                        />
                        
                        <span class="text-xs text-gray-700 font-medium group-hover:text-[#ae8b72]">{{ cat.name }}</span>
                    </button>
                </div>
            </div>
            
            <button @click="resetSelection" 
                    class="bg-white/90 text-red-500 text-xs font-bold py-2 px-3 rounded-lg shadow border border-red-100 hover:bg-red-50 transition flex items-center justify-center gap-1">
                <X class="w-4 h-4" /> 선택 해제
            </button>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, watch, toRaw, computed } from 'vue';
import { formatPrice, infraCategories } from '@/utils/productUtil';
import { X } from 'lucide-vue-next';

const props = defineProps(['items']); // 부모가 던진 데이터
const emit = defineEmits(['marker-click']); // 마커 클릭 이벤트

const mapContainer = ref(null); // 지도를 담을 div
const mapInstance = ref(null);
const markers = ref([]); // 마커 배열
const infraMarkers = ref([]); // 인프라 마커들 담을 배열
const selectedItem = ref(null); // 현재 선택된 매물 객체
const currentCircle = ref(null); // 현재 그려진 반경 원

// 지도 초기화
const initMap = () => {
    const container = mapContainer.value;
    const options = {
        center: new window.kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청
        level: 5 // 확대 레벨
    };

    // 지도 객체 요청
    mapInstance.value = new window.kakao.maps.Map(container, options);

    if (props.items && props.items.length > 0) {
        updateMarkers(props.items);
    }
};

// 마커 설정
const updateMarkers = (newItems) => {
    // 기존 마커 초기화
    if (markers.value.length > 0) {
        markers.value.forEach(marker => marker.setMap(null));
        markers.value = [];
    }

    // 새 리스트 로드 시 선택 상태 초기화
    resetSelection();

    // 데이터가 없으면 리턴
    if (!newItems || newItems.length === 0) return;

    // 지도 범위 재설정
    const bounds = new window.kakao.maps.LatLngBounds();
    const rawMap = toRaw(mapInstance.value);

    newItems.forEach(item => {
        if (!item.latitude || !item.longitude) return; // 좌표 없는 데이터는 패스

        const position = new window.kakao.maps.LatLng(item.latitude, item.longitude);
        const price = formatPrice(item); // 마커 내용(가격)

        const content = document.createElement('div');
        content.className = `custom-overlay`;

        // 마커
        content.innerHTML = `
            <div class="overlay-content">
                <span class="price">${price}</span>
            </div>
        `;

        // 마커 클릭 이벤트
        content.addEventListener('click', () => {
            emit('marker-click', item);
        });

        // 지도 오버레이
        const customOverlay = new window.kakao.maps.CustomOverlay({
            position: position,
            content: content,
            yAnchor: 1.5
        });

        customOverlay.setMap(rawMap);
        markers.value.push(customOverlay);
        bounds.extend(position);
    });

    // 모든 마커가 보이도록 지도 범위 조절
    if (rawMap && markers.value.length > 0) {
        rawMap.setBounds(bounds);
    }
};

// 인프라 검색 함수
const searchInfrastructure = (category) => {
    // 기존 인프라 마커 초기화
    clearInfraMarkers();

    if (!window.kakao || !window.kakao.maps.services || !selectedItem.value) {
        console.error("services 라이브러리가 로드되지 않았습니다.");
        return;
    }

    // 장소 검색 객체 생성
    const rawMap = toRaw(mapInstance.value);
    const ps = new window.kakao.maps.services.Places(rawMap);

    const lat = selectedItem.value.latitude;
    const lng = selectedItem.value.longitude;

    // 검색 옵션 (반경 500m, 중심 좌표 설정)
    const options = {
        location: new window.kakao.maps.LatLng(lat, lng),
        radius: 500,
        sort: window.kakao.maps.services.SortBy.DISTANCE // 거리순 정렬
    }

    const callback = (data, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
            displayInfraMarkers(data);
        } else {
            alert(`${category.name} 검색 결과가 없습니다.`);
        }
    };

    // 카카오 코드가 있는 것과 없는 것 구분
    if (category.type === 'category') {
        ps.categorySearch(category.code, callback, options);
    } else {
        ps.keywordSearch(category.keyword, callback, options);
    }
};

// 인프라 마커 표시 함수
const displayInfraMarkers = (places) => {
    const rawMap = toRaw(mapInstance.value);

    const markerImage1 = createMarkerImage('#ae8b72');

    const tooltipOverlay = new window.kakao.maps.CustomOverlay({ 
        zIndex: 1,
        yAnchor: 2.2
    });

    places.forEach(place => {
        const position = new window.kakao.maps.LatLng(place.y, place.x);

        const marker = new window.kakao.maps.Marker({
            map: rawMap,
            position: position,
            image: markerImage1
        });

        window.kakao.maps.event.addListener(marker, 'mouseover', () => {
            tooltipOverlay.setContent(`<div class="infra-tooltip">${place.place_name}</div>`);
            tooltipOverlay.setPosition(position);
            tooltipOverlay.setMap(rawMap);
        });

        window.kakao.maps.event.addListener(marker, 'mouseout', () => {
            tooltipOverlay.setMap(null);
        });

        infraMarkers.value.push(marker);
    });
}

// 인프라 마커 지우는 함수
const clearInfraMarkers = () => {
    infraMarkers.value.forEach(marker => marker.setMap(null));
    infraMarkers.value = [];
}

// 매물 선택 시 실행할 이벤트: 지도 이동 + 반경(원) + 인프라 버튼 활성화
const selectItem = (item) => {
    if (!mapInstance.value || !item) return;

    selectedItem.value = item; // 버튼 표시 트리거
    const rawMap = toRaw(mapInstance.value);
    const position = new window.kakao.maps.LatLng(item.latitude, item.longitude);

    // 1. 지도 중심 이동
    mapInstance.value.setLevel(4);
    mapInstance.value.panTo(position);

    // 2. 기존 원 제거
    if (currentCircle.value) {
        currentCircle.value.setMap(null);
    }

    // 3. 새로운 반경 500m 원 생성 및 표시
    const circle = new window.kakao.maps.Circle({
        center: position, // 원 중심 좌표
        radius: 500, // 원 반지름
        strokeWeight: 2, // 선 두께
        strokeColor: '#ae8b72', // 선 색깔
        strokeOpacity: 0.8,
        strokeStyle: 'solid',
        fillColor: '#ae8b72', // 채우기 색깔
        fillOpacity: 0.2
    });

    circle.setMap(rawMap);
    currentCircle.value = circle;

    // 4. 이전에 검색된 인프라 마커 초기화
    clearInfraMarkers();
}

// 선택 초기화: 뒤로가기 누를 때 원과 버튼을 지우는 함수
const resetSelection = () => {
    selectedItem.value = null; // 버튼 숨김

    // 원 지우기
    if (currentCircle.value) {
        currentCircle.value.setMap(null);
        currentCircle.value = null;
    }

    clearInfraMarkers();
}

// 마커 생성 함수
const createMarkerImage = (color) => {
    // 마커
    const svgContent = `
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="35" viewBox="0 0 24 35">
            <path fill="${color}" d="M12 0C5.373 0 0 5.373 0 12c0 9.5 12 23 12 23s12-13.5 12-23c0-6.627-5.373-12-12-12z"/>
            <circle fill="white" cx="12" cy="12" r="4"/>
        </svg>
    `;

    // svg 문자열을 Data URL로 변환(이미지처럼 쓰기 위함)
    const svgUrl = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgContent);

    // 카카오맵 마커 이미지 객체 생성
    return new window.kakao.maps.MarkerImage(
        svgUrl,
        new window.kakao.maps.Size(24, 35), // 마커 크기
        { offset: new window.kakao.maps.Point(12, 35) } // 마커 기준점
    );
};

defineExpose({
    selectItem,
    resetSelection,
    searchInfrastructure,
    clearInfraMarkers
});

watch(() => props.items, (newItems) => {
    if (mapInstance.value) {
        updateMarkers(newItems);
    }
},
    {
        deep: true
    }
);

onMounted(() => {
    if (window.kakao && window.kakao.maps) {
        window.kakao.maps.load(() => {
            initMap();
        });
    } else {
        console.error("카카오맵 로딩 오류");
    }
});

</script>
