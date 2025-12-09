<template>
    <div ref="mapContainer" class="w-full h-full bg-gray-100"></div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { formatPrice } from '@/utils/productUtil';

const props = defineProps(['items']); // 부모가 던진 데이터
const emit = defineEmits(['marker-click']); // 마커 클릭 이벤트
const mapContainer = ref(null); // 지도를 담을 div
const mapInstance = ref(null);
const markers = ref([]); // 마커 배열

// 지도 초기 설정
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

    // 데이터가 없으면 리턴
    if (!newItems || newItems.length === 0) return;

    // 지도 범위 재설정
    const bounds = new window.kakao.maps.LatLngBounds();

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
            moveToCenter(item.latitude, item.longitude);
            emit('marker-click', item);
        });

        // 지도 오버레이
        const customOverlay = new window.kakao.maps.CustomOverlay({
            position: position,
            content: content,
            yAnchor: 1.5
        });

        customOverlay.setMap(mapInstance.value);
        markers.value.push(customOverlay);
        bounds.extend(position);
    });

    // 모든 마커가 보이도록 지도 범위 조절
    if (mapInstance.value && markers.value.length > 0) {
        mapInstance.value.setBounds(bounds);
    }
};

// 매물 선택 시 지도 이동 함수
const moveToCenter = (lat, lng) => {
    if (!mapInstance.value || !lat || !lng) return;

    const moveLatLng = new window.kakao.maps.LatLng(lat, lng); // 이동할 좌표

    mapInstance.value.setLevel(4); // 지도 확대 레벨
    mapInstance.value.panTo(moveLatLng); // 지도 이동(panTo: 이동 애니메이션)
};

defineExpose({
    moveToCenter
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