$(document).ready(function() {
    var map;
    function initTmap() {
        map = new Tmapv2.Map("map_div", {
            center: new Tmapv2.LatLng(37.5665, 126.9780),
            zoom: 11
        });
    }

    // 페이지 로드 시 지도 초기화 실행
    initTmap();
});