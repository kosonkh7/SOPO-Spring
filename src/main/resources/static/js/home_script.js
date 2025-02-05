$(document).ready(function() {
    var map;
    var markers = []; // 마커 배열

    function initTmap() {
        map = new Tmapv2.Map("map_div", {
            center: new Tmapv2.LatLng(37.5665, 126.9780),
            zoom: 11
        });

        // 마커 데이터 배열
        var markerData = [
            { name: "가락시장", lat: 37.49239, lon: 127.118104 },
            { name: "고속터미널", lat: 37.505187, lon: 127.004797 },
            { name: "군자", lat: 37.557416, lon: 127.079572 },
            { name: "길음", lat: 37.603415, lon: 127.024904 },
            { name: "김포공항", lat: 37.562294, lon: 126.801375 },
            { name: "독산", lat: 37.466035, lon: 126.889512 },
            { name: "목동", lat: 37.526115, lon: 126.864263 },
            { name: "불광", lat: 37.610664, lon: 126.929822 },
            { name: "석계", lat: 37.615167, lon: 127.065758 },
            { name: "신내", lat: 37.612872, lon: 127.103202 },
            { name: "신림", lat: 37.484274, lon: 126.929669 },
            { name: "양재", lat: 37.484187, lon: 127.034374 },
            { name: "온수", lat: 37.492191, lon: 126.823629 },
            { name: "용산", lat: 37.529898, lon: 126.964774 },
            { name: "창동", lat: 37.652913, lon: 127.047924 },
            { name: "천호", lat: 37.538773, lon: 127.123324 },
            { name: "화계", lat: 37.634237, lon: 127.017491 }
        ];

        // 마커 추가 함수
        function addMarker(lat, lon, name) {
            var marker = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(lat, lon),
                map: map,
                title: name,
                icon: "/img/h_warehouse.png", // 마커 아이콘 (필요시 변경 가능)
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(marker); // 배열에 저장
        }

        // 모든 마커 지도에 추가
        markerData.forEach(station => {
            addMarker(station.lat, station.lon, station.name);
        });
    }

    // 페이지 로드 시 지도 초기화 실행
    initTmap();
});