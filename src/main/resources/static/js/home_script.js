$(document).ready(function () {
    var map;
    var markers = []; // 마커 배열
    var baseSize = 35; // 기본 마커 크기

    // 마커 데이터 배열
    var markerData = [
        {name: "가락시장", lat: 37.49239, lon: 127.118104},
        {name: "고속터미널", lat: 37.505187, lon: 127.004797},
        {name: "군자", lat: 37.557416, lon: 127.079572},
        {name: "길음", lat: 37.603415, lon: 127.024904},
        {name: "김포공항", lat: 37.562294, lon: 126.801375},
        {name: "독산", lat: 37.466035, lon: 126.889512},
        {name: "목동", lat: 37.526115, lon: 126.864263},
        {name: "불광", lat: 37.610664, lon: 126.929822},
        {name: "석계", lat: 37.615167, lon: 127.065758},
        {name: "신내", lat: 37.612872, lon: 127.103202},
        {name: "신림", lat: 37.484274, lon: 126.929669},
        {name: "양재", lat: 37.484187, lon: 127.034374},
        {name: "온수", lat: 37.492191, lon: 126.823629},
        {name: "용산", lat: 37.529898, lon: 126.964774},
        {name: "창동", lat: 37.652913, lon: 127.047924},
        {name: "천호", lat: 37.538773, lon: 127.123324},
        {name: "화계", lat: 37.634237, lon: 127.017491}
    ];

    // 지도 초기화 함수
    function initTmap() {
        map = new Tmapv2.Map("map_div", {
            center: new Tmapv2.LatLng(37.5665, 126.9780),
            zoom: 11
        });

        // 모든 마커 지도에 추가
        markerData.forEach(station => {
            addMarker(station.lat, station.lon, station.name, baseSize);
        });

        // 줌 이벤트 감지하여 마커 크기 변경
        map.addListener("zoom_changed", function () {
            updateMarkerSize();
        });
    }

    // 마커 추가 함수
    function addMarker(lat, lon, name, size) {
        var marker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(lat, lon),
            map: map,
            title: name,
            icon: "/img/h_marker.png",
            iconSize: new Tmapv2.Size(size, size)
        });
        markers.push(marker);
    }

    // 줌 변경 시 마커 크기 조정 함수
    function updateMarkerSize() {
        var zoomLevel = map.getZoom(); // 현재 줌 레벨 가져오기
        var newSize = baseSize * (zoomLevel / 10); // 줌 레벨에 따라 크기 조정

        markers.forEach(marker => {
            marker.setIcon("/img/h_marker.png", new Tmapv2.Size(newSize, newSize));
        });
    }

    // 페이지 로드 시 지도 초기화 실행
    initTmap();

    // 하이라이트 기능 (추가 코드 - 유지)
    let steps = document.querySelectorAll(".process-step");
    let index = 0;

    function highlightStep() {
        steps.forEach(step => step.classList.remove("highlight"));
        steps[index].classList.add("highlight");
        index = (index + 1) % steps.length;
    }

    setInterval(highlightStep, 1000);
});