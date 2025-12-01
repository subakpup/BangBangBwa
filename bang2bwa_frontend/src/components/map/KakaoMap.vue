<script setup>
import { onMounted, ref, watch } from 'vue';

const props = defineProps(['items']); // 부모가 던진 데이터
const mapContainer = ref(null); // 지도를 담을 div
const mapInstance = ref(null);
const markers = ref([]); // 마커 배열

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
        if (!item.lat || !item.lng) return;

        const position = new window.kakao.maps.LatLng(item.lat, item.lng);

        const marker = new window.kakao.maps.Marker({
            position: position,
            map: mapInstance.value,
            title: item.name, // 마우스 올리면 이름
        });

        markers.value.push(marker); // 배열에 저장
        bounds.extend(position); // 지도 범위에 포함
    });

    // 모든 마커가 보이도록 지도 범위 조절
    if (mapInstance.value && markers.value.length > 0) {
        mapInstance.value.setBounds(bounds);
    }
};

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

<template>
    <div ref="mapContainer" class="w-full h-full bg-gray-100"></div>
</template>