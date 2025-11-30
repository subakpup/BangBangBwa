<script setup>
import { onMounted, ref } from 'vue';

const mapContainer = ref(null); // 지도를 담을 div

const initMap= () => {
    const container = mapContainer.value;
    const options = {
        center: new window.kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청
        level: 3 // 확대 레벨
    };

    // 지도 객체 요청
    const map = new window.kakao.maps.Map(container, options);
    console.log("지도 로딩");
};

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