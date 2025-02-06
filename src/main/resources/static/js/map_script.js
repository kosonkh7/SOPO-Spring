$(document).ready(function() {
    var tmapKey = $("#tmap-script").attr("data-tmap-key"); // HTML에서 API 키 가져오기

    // 지도 초기화
    var map;

    var markers = []; // 마커를 저장
    var polylines = []; // 경로를 저장
    var selectedMarker = null; // 사용자가 선택한 마커를 저장

    const initialCenter = new Tmapv2.LatLng(37.5665, 126.9780); // 초기 중심 좌표
    const initialZoom = 11; // 초기 줌 레벨

    function initTmap() {
        map = new Tmapv2.Map("map_div", {
            center: new Tmapv2.LatLng(37.5665, 126.9780),
            zoom: 11
        });

        displayStations(); // 지도 생성 후 마커 표시
    }

    // 리버스 지오코딩 함수 (Tmap API 활용)
    function reverseGeocode(lat, lon) {
        var apiUrl = `https://apis.openapi.sk.com/tmap/geo/reversegeocoding?version=1&lat=${lat}&lon=${lon}&coordType=WGS84GEO&addressType=road&appKey=${tmapKey}`;

        $.getJSON(apiUrl, function (data) {
            if (data && data.addressInfo) {
                var address = data.addressInfo.fullAddress;
                $("#address_input").val(address); // 주소 입력칸에 표시
                console.log("리버스 지오코딩 주소:", address);
            } else {
                console.warn("⚠️ 주소 정보를 가져올 수 없음");
            }
        }).fail(function () {
            console.error("❌ 리버스 지오코딩 요청 실패");
        });
    }

    var initialLat = parseFloat($("#end_lat").val());
    var initialLon = parseFloat($("#end_lon").val());
    if (!isNaN(initialLat) && !isNaN(initialLon)) {
        reverseGeocode(initialLat, initialLon); // ✅ 초기 값 리버스 지오코딩 실행
    }

    // 초기 상태 대시보드 템플릿 정의
    const initialDashboardTemplate = `
        <div class="cards-container">
            <!-- 카드 1: 거리 비교 -->
            <div class="card">
                <h4>🚛 거리 비교</h4>
                <p>기존 택배: <span id="distance_original">--</span> km</p>
                <p>지하철 창고: <span id="distance_subway">--</span> km</p>
                <p>📉 절감률: <span id="distance_reduction">--</span>%</p>
            </div>

            <!-- 카드 2: 시간 비교 -->
            <div class="card">
                <h4>⏳ 시간 비교</h4>
                <p>기존 택배: <span id="time_original">--</span> 분</p>
                <p>지하철 창고: <span id="time_subway">--</span> 분</p>
                <p>📉 절감률: <span id="time_reduction">--</span>%</p>
            </div>

            <!-- 카드 3: 비용 비교 -->
            <div class="card">
                <h4>💰 비용 비교</h4>
                <p>기존 택배: <span id="cost_original">--</span> 원</p>
                <p>지하철 창고: <span id="cost_subway">--</span> 원</p>
                <p>📉 절감률: <span id="cost_reduction">--</span>%</p>
            </div>

            <!-- 카드 4: 탄소 배출 비교 -->
            <div class="card">
                <h4>🌱 탄소 배출 비교</h4>
                <p>기존 택배: <span id="carbon_original">--</span> g CO₂</p>
                <p>지하철 창고: <span id="carbon_subway">--</span> g CO₂</p>
                <p>📉 절감률: <span id="carbon_reduction">--</span>%</p>
            </div>
        </div>
    `;

    function clearMap() {
        // 기존 마커 제거, selectedMarker는 유지
        markers.forEach(marker => {
            if (marker !== selectedMarker) {
                marker.setMap(null);
            }
        });

        markers = selectedMarker ? [selectedMarker] : [];

        // 기존 경로 제거
        polylines.forEach(polyline => polyline.setMap(null));
        polylines = []; // 배열 초기화
    }

    function displayStations() {
        // 1) stations 데이터 한 번만 불러옴
        $.getJSON("/api/stations", function(stations) {

            // 2) 지도에 마커 표시
            stations.forEach(station => {
                var marker = new Tmapv2.Marker({
                    position: new Tmapv2.LatLng(station.latitude, station.longitude),
                    map: map,
                    title: station.name,
                    icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                    iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                    iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
                });
                markers.push(marker);
            });

            // 3) <select> 박스 갱신 (초기화 후 옵션 추가)
            const $select = $("#start_station");
            $select.empty();

            stations.forEach(station => {
                const optionHtml = `<option value="${station.name}">${station.name}</option>`;
                $select.append(optionHtml);
            });
        });
    }

    function adjustMapBounds(markers) {
        if (markers.length === 0) return; // 마커가 없는 경우 종료

        const bounds = new Tmapv2.LatLngBounds(); // 지도 영역 객체 생성

        // 모든 마커의 위치를 영역에 추가
        markers.forEach(marker => bounds.extend(marker.getPosition()));

        // 지도 중심과 줌을 영역에 맞춤
        map.fitBounds(bounds);
    }

    // 로딩 화면 보이기
    function showLoading() {
        $("#loadingPopup").fadeIn(300);
    }

    // 로딩 화면 숨기기
    function hideLoading() {
        $("#loadingPopup").fadeOut(300);
    }

    // 지도 초기화 실행
    initTmap();

    function updateParcelRouteMap(data) {
        clearMap(); // 기존 지도 상태 초기화
        $(".map .map-legend").remove();

        // 출발 Sub 터미널과 도착 Sub 터미널이 같은 경우 처리
        if (data.start_sub_terminal.name === data.end_sub_terminal.name) {
            var subTerminalPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.start_sub_terminal.lat, data.start_sub_terminal.lon),
                map: map,
                title: `출발 및 도착 Sub 터미널: ${data.start_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(subTerminalPin);
        } else {
            // 출발 Sub 터미널 핀
            var startSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.start_sub_terminal.lat, data.start_sub_terminal.lon),
                map: map,
                title: `출발 Sub 터미널: ${data.start_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(startSubPin);

            // 도착 Sub 터미널 핀
            var endSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.end_sub_terminal.lat, data.end_sub_terminal.lon),
                map: map,
                title: `도착 Sub 터미널: ${data.end_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(endSubPin);
        }

        // Hub 터미널 핀
        var hubPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.hub_terminal.lat, data.hub_terminal.lon),
            map: map,
            title: `Hub 터미널: ${data.hub_terminal.name}`,
            icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(hubPin);

        // 최종 배송지 핀
        var deliveryPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.end_lat, data.end_lon),
            map: map,
            title: "최종 배송지",
            icon: "/img/map_end.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(deliveryPin);

        // 지도 왼쪽 위에 범례 추가
        $(".map").append(`
            <div class="map-legend">
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #FF5733;"></span> Sub → Hub
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #3388FF;"></span> Hub → Sub
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #28A745;"></span> Sub → 배송지
                </div>
            </div>
        `);

        // 경로 추가
        if (data.to_hub_route && data.to_hub_route.length > 0) {
            var toHubPolyline = new Tmapv2.Polyline({
                path: data.to_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF5733", // 빨간색
                strokeWeight: 6,
                //strokeOpacity: 0.8,  // 반투명 효과 추가
                map: map
            });
            polylines.push(toHubPolyline);
        }

        if (data.from_hub_route && data.from_hub_route.length > 0) {
            var fromHubPolyline = new Tmapv2.Polyline({
                path: data.from_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#3388FF", // 파란색
                strokeWeight: 6,
                //strokeOpacity: 0.8,  // 반투명 효과 추가
                map: map
            });
            polylines.push(fromHubPolyline);
        }

        if (data.to_destination_route && data.to_destination_route.length > 0) {
            var toDestinationPolyline = new Tmapv2.Polyline({
                path: data.to_destination_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#28A745", // 초록색
                strokeWeight: 6,
                //strokeOpacity: 0.8,  // 반투명 효과 추가
                map: map
            });
            polylines.push(toDestinationPolyline);
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 대시보드 업데이트
        $(".dashboard").empty();
        $(".dashboard").append(`
            <div class="process-container">
                <h3>📦 기존 택배 프로세스</h3>
                
                <div class="process-card startsub-card">
                    <h4>🚚 출발 Sub 터미널</h4>
                    <p>${data.start_sub_terminal.name}</p>
                </div>
                
                <div class="process-card hub-card">
                    <h4>🏭 Hub 터미널</h4>
                    <p>${data.hub_terminal.name}</p>
                </div>
                
                <div class="process-card endsub-card">
                    <h4>🚚 도착 Sub 터미널</h4>
                    <p>${data.end_sub_terminal.name}</p>
                </div>
                
                <div class="process-card time-card">
                    <h4>⏳ 총 이동 시간</h4>
                    <p><span class="time-highlight">${Math.round(data.total_time / 60)}</span> 분</p>
                </div>
            </div>
        `);
        $(".dashboard").append(`
            <p style="color: gray; font-size: 12px; margin-top: 10px;">
                🚚 <strong>Sub 터미널 선택:</strong> 각각 선택한 출발 창고, 배송지와 가장 가까운 Sub 터미널을 자동으로 선택합니다.<br>
                ℹ️ <strong>등록된 터미널:</strong> CJ 대한통운 Sub 터미널 중 서울 지역 내 터미널을 기준으로 경로를 계산합니다.
            </p>
        `);
    }

    function updateMap(data) {
        // 기존 마커와 경로, 이미지 제거
        clearMap();
        $(".map .map-legend").remove();

        // 출발지 마커 추가
        var startMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.start_lat, data.start_lon),
            map: map,
            title: `출발지: ${data.start_station}`,
            icon: "/img/map_start.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(startMarker); // 배열에 저장

        // 도착지 마커 추가
        var endMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.end_lat, data.end_lon),
            map: map,
            title: "배송지",
            icon: "/img/map_end.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(endMarker); // 배열에 저장

        // 지도 왼쪽 위에 범례 추가
        $(".map").append(`
            <div class="map-legend">
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #3388FF;"></span> 지하철도 경로
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #FF5733;"></span> 주행 경로
                </div>
            </div>
        `);

        // 경로 중심 계산
        // var centerLat = (data.start_lat + data.end_lat) / 2;
        // var centerLon = (data.start_lon + data.end_lon) / 2;
        //
        // // 지도 중심 이동
        // map.setCenter(new Tmapv2.LatLng(centerLat, centerLon));

        // 지하철 경로 추가
        if (data.subway_route && data.subway_route.length > 0) {
            var subwayPolyline = new Tmapv2.Polyline({
                path: data.subway_route.map(coord => new Tmapv2.LatLng(coord[1], coord[0])),
                strokeColor: "#3388FF",
                strokeWeight: 6,
                map: map
            });
            polylines.push(subwayPolyline); // 배열에 저장
        }

        // 주행 경로 추가
        if (data.driving_route && data.driving_route.length > 0) {
            var drivingPolyline = new Tmapv2.Polyline({
                path: data.driving_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF5733",
                strokeWeight: 6,
                map: map
            });
            polylines.push(drivingPolyline); // 배열에 저장
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 대시보드 업데이트
        $(".dashboard").empty();

        // 지하철 예상 소요 시간 표시 (유효하지 않은 값이면 0으로 대체)
        var subwayTime = isNaN(data.subway_total_time) || data.subway_total_time <= 0 ? 0 : Math.round(data.subway_total_time / 60);
        // 자동차 예상 소요 시간 표시 (유효하지 않은 값이면 0으로 대체)
        var drivingTime = isNaN(data.driving_total_time) || data.driving_total_time <= 0 ? 0 : Math.round(data.driving_total_time / 60);
        // 총 예상 소요 시간
        var totalTime = subwayTime + drivingTime;

        $(".dashboard").append(`
            <div class="process-container">
                <h3>🚇 지하철 창고 프로세스</h3>
                
                <div class="process-card subway-card">
                    <h4>🚆 지하철 이동 소요 시간</h4>
                    <p><span class="time-highlight">${subwayTime}</span> 분</p>
                </div>
                
                <div class="process-card driving-card">
                    <h4>🏍️ 오토바이 이동 소요 시간</h4>
                    <p><span class="time-highlight">${drivingTime}</span> 분</p>
                </div>
                
                <div class="process-card total-card">
                    <h4>⏳ 총 이동 소요 시간</h4>
                    <p><span class="time-highlight">${totalTime}</span> 분</p>
                </div>
            </div>
        `);

        $(".dashboard").append(`
            <p style="color: gray; font-size: 12px; margin-top: 10px;">
                🚆 <strong>도착 지하 창고:</strong> 배송지와 가장 가까운 지하 창고를 자동으로 선택합니다. 동일한 지하 창고일 경우 해당 정보는 생략됩니다.<br>
                🏍️ 출발 지하 창고와 도착 지하 창고가 동일할 경우, <strong>별도 경로 없이 주행 경로만 출력</strong>됩니다.<br>
                ℹ️ 주행 시간이 <strong>20분 이내</strong>로 차이가 없다면, <strong>주행 경로만 출력</strong>됩니다.
            </p>
        `);

        // 비교 결과 표시
        // if (data.reason) {
        //     $(".dashboard").append(`<p id="comparison_result">${data.reason}</p>`);
        // }
    }

    function updateComparisonRoutes(data) {
        clearMap(); // 기존 지도 상태 초기화
        $(".map .map-legend").remove();

        // 출발 Sub 터미널과 도착 Sub 터미널이 같은 경우 처리
        if (data.parcel.start_sub_terminal.name === data.parcel.end_sub_terminal.name) {
            const subTerminalPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.start_sub_terminal.lat, data.parcel.start_sub_terminal.lon),
                map: map,
                title: `출발 및 도착 Sub 터미널: ${data.parcel.start_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(subTerminalPin);
        } else {
            // 출발 Sub 터미널 핀
            const startSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.start_sub_terminal.lat, data.parcel.start_sub_terminal.lon),
                map: map,
                title: `출발 Sub 터미널: ${data.parcel.start_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(startSubPin);

            // 도착 Sub 터미널 핀
            const endSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.end_sub_terminal.lat, data.parcel.end_sub_terminal.lon),
                map: map,
                title: `도착 Sub 터미널: ${data.parcel.end_sub_terminal.name}`,
                icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
                iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
                iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
            });
            markers.push(endSubPin);
        }

        // Hub 터미널 핀
        const hubPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.hub_terminal.lat, data.parcel.hub_terminal.lon),
            map: map,
            title: `Hub 터미널: ${data.parcel.hub_terminal.name}`,
            icon: "/img/h_warehouse.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(hubPin);

        // 출발지 마커 추가
        const startMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.start_lat, data.parcel.start_lon),
            map: map,
            title: `출발지: ${data.parcel.start_station}`,
            icon: "/img/map_start.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(startMarker); // 배열에 저장

        // 최종 배송지 핀
        const deliveryPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.end_lat, data.parcel.end_lon),
            map: map,
            title: "배송지",
            icon: "/img/map_end.png",  // Spring 서버에서 제공하는 정적 경로
            iconSize: new Tmapv2.Size(30, 30), // 마커 크기 설정
            iconAnchor: new Tmapv2.Point(15, 30) // 마커 중심 조정 (선택 사항)
        });
        markers.push(deliveryPin);

        // 지도 왼쪽 위에 범례 추가
        $(".map").append(`
            <div class="map-legend">
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #3388FF;"></span> 기존 경로
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background-color: #FF5733;"></span> 지하창고 경로
                </div>
            </div>
        `);

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 택배 경로
        if (data.parcel.to_hub_route && data.parcel.to_hub_route.length > 0) {
            const toHubPolyline = new Tmapv2.Polyline({
                path: data.parcel.to_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#3388FF",
                strokeWeight: 6,
                map: map
            });
            polylines.push(toHubPolyline);
        }

        if (data.parcel.from_hub_route && data.parcel.from_hub_route.length > 0) {
            const fromHubPolyline = new Tmapv2.Polyline({
                path: data.parcel.from_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#3388FF",
                strokeWeight: 6,
                map: map
            });
            polylines.push(fromHubPolyline);
        }

        if (data.parcel.to_destination_route && data.parcel.to_destination_route.length > 0) {
            const toDestinationPolyline = new Tmapv2.Polyline({
                path: data.parcel.to_destination_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#3388FF",
                strokeWeight: 6,
                map: map
            });
            polylines.push(toDestinationPolyline);
        }

        // 선택된 경로
        const selectedRoute = data.subway;
        if (selectedRoute.type === "지하철도+주행 경로") {
            if (selectedRoute.subway_route && selectedRoute.subway_route.length > 0) {
                const subwayPolyline = new Tmapv2.Polyline({
                    path: selectedRoute.subway_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                    strokeColor: "#FF5733",
                    strokeWeight: 6,
                    map: map
                });
                polylines.push(subwayPolyline);
            }
        }

        if (selectedRoute.driving_route && selectedRoute.driving_route.length > 0) {
            const drivingPolyline = new Tmapv2.Polyline({
                path: selectedRoute.driving_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF5733",
                strokeWeight: 6,
                map: map
            });
            polylines.push(drivingPolyline);
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 기존 택배 데이터
        const parcelDistance = data.parcel.distance.toFixed(2);
        const parcelTime = Math.round(data.parcel.time);
        const parcelCost = data.parcel.cost.toFixed(1);
        const parcelEmission = data.parcel.emission.toFixed(2);

        // 지하철 창고 데이터
        const subwayDistance = selectedRoute.distance.toFixed(2);
        const subwayTime = Math.round(selectedRoute.time);
        const subwayCost = selectedRoute.cost.toFixed(1);
        const subwayEmission = selectedRoute.emission.toFixed(2);


        // 대시보드 업데이트
        $(".dashboard").empty();
        // $(".dashboard").append(`<h3>이동 경로 비교 결과</h3>`);

        // 감소율 계산 함수
        function calculateReduction(original, newValue) {
            return ((original - newValue) / original * 100).toFixed(2); // 감소율 계산 (소수점 2자리)
        }

        // 각 지표의 감소율 계산
        const distanceReduction = calculateReduction(parcelDistance, subwayDistance); // 거리 감소율
        const timeReduction = calculateReduction(parcelTime, subwayTime); // 시간 감소율
        const costReduction = calculateReduction(parcelCost, subwayCost); // 비용 감소율
        const emissionReduction = calculateReduction(parcelEmission, subwayEmission); // 탄소 배출량 감소율

        // 대시보드에 결과 추가
        const comparisonData = {
            distance: { original: parcelDistance, subway: subwayDistance, reduction: distanceReduction },
            time: { original: parcelTime, subway: subwayTime, reduction: timeReduction },
            cost: { original: parcelCost.toLocaleString(), subway: subwayCost.toLocaleString(), reduction: costReduction },
            carbon: { original: parcelEmission.toLocaleString(), subway: subwayEmission.toLocaleString(), reduction: emissionReduction },
        };

        $(".dashboard").append(initialDashboardTemplate);

        const updateDashboard = (data) => {
            // 데이터를 DOM에 업데이트
            document.getElementById("distance_original").textContent = data.distance.original;
            document.getElementById("distance_subway").textContent = data.distance.subway;
            document.getElementById("distance_reduction").textContent = data.distance.reduction;

            document.getElementById("time_original").textContent = data.time.original;
            document.getElementById("time_subway").textContent = data.time.subway;
            document.getElementById("time_reduction").textContent = data.time.reduction;

            document.getElementById("cost_original").textContent = data.cost.original;
            document.getElementById("cost_subway").textContent = data.cost.subway;
            document.getElementById("cost_reduction").textContent = data.cost.reduction;

            document.getElementById("carbon_original").textContent = data.carbon.original;
            document.getElementById("carbon_subway").textContent = data.carbon.subway;
            document.getElementById("carbon_reduction").textContent = data.carbon.reduction;
        }

        // 업데이트 호출
        updateDashboard(comparisonData);

        $(".dashboard").append(`
            <p style="color: gray; font-size: 12px; margin-top: 10px;">
                ℹ️ <strong>안내:</strong> 해당 데이터는 <strong>운행 시간</strong>과 <strong>운행 비용</strong>만 고려된 값입니다.<br>
                ⚠️ <strong>참고 사항:</strong> 본 정보는 단순 참고용이며, 실제 운행 시 비용이 달라질 수 있습니다.
            </p>
        `);

        // 대시보드에 차트 추가
        $(".dashboard").append(`
            <div class="chart-container">
                <canvas id="distanceTimeChart"></canvas>
            </div>
        `);
        $(".dashboard").append(`
            <div class="chart-container">
                <canvas id="costEmissionChart"></canvas>
            </div>
        `);

        // 이동 거리 & 시간 비교 (막대 그래프)
        const distanceTimeCtx = document.getElementById("distanceTimeChart").getContext("2d");
        new Chart(distanceTimeCtx, {
            type: "bar",
            data: {
                labels: ["기존 택배", "지하철 창고"],
                datasets: [
                    {
                        label: "총 거리 (km)",
                        data: [data.parcel.distance.toFixed(2), data.subway.distance.toFixed(2)],
                        backgroundColor: "rgba(255, 99, 132, 0.7)"
                    },
                    {
                        label: "총 시간 (분)",
                        data: [Math.round(data.parcel.time), Math.round(data.subway.time)],
                        backgroundColor: "rgba(54, 162, 235, 0.7)"
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: { position: "top" }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // 비용 & 탄소 배출 비교 (막대 그래프)
        const costEmissionCtx = document.getElementById("costEmissionChart").getContext("2d");
        new Chart(costEmissionCtx, {
            type: "bar",
            data: {
                labels: ["기존 택배", "지하철 창고"],
                datasets: [
                    {
                        label: "비용 (₩)",
                        data: [data.parcel.cost, data.subway.cost],
                        backgroundColor: "rgba(255, 206, 86, 0.7)"
                    },
                    {
                        label: "탄소 배출량 (g CO₂)",
                        data: [data.parcel.emission.toFixed(2), data.subway.emission.toFixed(2)],
                        backgroundColor: "rgba(75, 192, 192, 0.7)"
                    }
                ]
            },
            options: {
                responsive: true,
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: { position: "top" }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // 위치 선택 버튼 클릭 → 지도에 마커 표시
    $(document).on("click", "#add_marker_btn", function (event) {
        event.preventDefault(); // 기본 제출 방지

        // 기존 마커 삭제
        if (selectedMarker !== null) {
            console.log("🔴 기존 마커 삭제:", selectedMarker);
            selectedMarker.setMap(null);  // 기존 마커 제거
            selectedMarker = null;
        }

        // 지도 중심에 마커 추가
        selectedMarker = new Tmapv2.Marker({
            position: map.getCenter(),
            map: map,
            draggable: true, // 드래그 가능
            icon: "/img/map_end.png", // 사용자 선택 마커 아이콘
            iconSize: new Tmapv2.Size(40, 40) // 사용자 마커 크기 설정
        });

        selectedMarker.setMap(map);
        markers.push(selectedMarker); // selectedMarker도 markers 배열에 추가

        console.log("🟢 새로운 마커 추가됨:", selectedMarker);
        alert("마커를 원하는 위치로 드래그하세요!");
    });

    // 위치 확인 버튼 클릭 → 마커 위치를 입력 칸에 자동 입력
    $(document).on("click", "#confirm_location_btn", function (event) {
        event.preventDefault(); // 기본 동작(새로고침) 방지

        if (!selectedMarker) {
            alert("❗ 마커를 먼저 추가해주세요.");
            return;
        }

        var position = selectedMarker.getPosition();
        $("#end_lat").val(position.lat());
        $("#end_lon").val(position.lng());

        // 리버스 지오코딩 실행
        reverseGeocode(parseFloat(position.lat()), parseFloat(position.lng()));

        console.log(`✅ 선택한 위치: 위도 ${position.lat()}, 경도 ${position.lng()}`);
        alert(`선택한 위치:\n위도: ${position.lat()}\n경도: ${position.lng()}`);
    });

    // 경로 비교 버튼을 클릭하면 선택된 마커 삭제 & 버튼 비활성화
    function disableSelectionButtons() {
        if (selectedMarker !== null) {
            console.log("🔴 기존 선택 마커 삭제:", selectedMarker);
            selectedMarker.setMap(null); // 기존 마커 삭제
            selectedMarker = null;
        }

        // 배송지 선택 & 위치 확인 버튼 비활성화
        $("#add_marker_btn").prop("disabled", true);
        $("#confirm_location_btn").prop("disabled", true);
    }

    // 경로 계산 버튼을 클릭하면 선택된 마커 제거 & 버튼 비활성화
    $("#default_btn, #subway_btn, #compare_routes_btn").on("click", function() {
        disableSelectionButtons();
    });

    $("#default_btn").on("click", function() {
        showLoading(); // 로딩 화면 표시

        var formData = $("#route_form").serialize();
        $.post("/api/parcel-route", formData, function(data) {
            console.log("택배 경로 응답 데이터:", data);
            updateParcelRouteMap(data); // 지도를 업데이트하는 함수
        }).fail(function(xhr, status, error) {
            console.error("에러:", error);
            console.error("서버 응답:", xhr.responseText);
        }).always(function() {
            hideLoading(); // 요청이 끝나면 로딩 화면 숨기기
        });
    });

    $("#subway_btn").on("click", function() {
        showLoading(); // 로딩 화면 표시

        var formData = $("#route_form").serialize();
        $.post("/api/compare-routes", formData, function(data) {
            console.log("응답 데이터:", data); // 디버깅 로그
            updateMap(data);
        }).fail(function(xhr, status, error) {
            console.error("에러 상태:", status); // 에러 상태 확인
            console.error("에러 메시지:", error); // 에러 메시지 확인
            console.error("서버 응답:", xhr.responseText); // 서버에서 반환된 에러 메시지
        }).always(function() {
            hideLoading(); // 요청이 끝나면 로딩 화면 숨기기
        });
    });

    $("#compare_routes_btn").on("click", function() {
        showLoading(); // 로딩 화면 표시
        const formData = $("#route_form").serialize(); // 폼 데이터를 가져옴

        // 서버로 요청 보내기
        $.post("/api/compare-route-details", formData)
            .done(function(data) {
                console.log("경로 비교 응답 데이터:", data);

                // 응답 데이터 검증
                if (data && data.parcel && data.subway) {
                    updateComparisonRoutes(data); // 비교 결과를 지도와 대시보드에 업데이트
                } else {
                    console.error("응답 데이터가 유효하지 않습니다.", data);
                    alert("경로 비교에 실패했습니다. 다시 시도해주세요.");
                }
            })
            .fail(function(xhr, status, error) {
                console.error("에러:", error);
                console.error("에러 메시지:", error);
                console.error("서버 응답:", xhr.responseText);
                alert("서버와 통신 중 문제가 발생했습니다. 다시 시도해주세요.");
            }).always(function() {
            hideLoading(); // 요청이 끝나면 로딩 화면 숨기기
        });
    });

    $("#reset_btn").on("click", function() {
        clearMap(); // 기존 마커와 경로 제거
        displayStations(); // 지하철역 다시 표시
        map.setCenter(initialCenter); // 초기 중심 좌표로 이동
        map.setZoom(initialZoom); // 초기 줌 레벨로 복원

        // 대시보드를 초기 템플릿 상태로 복원
        $(".dashboard").empty();
        // $(".dashboard").append(`<h3>Dashboard</h3>`);
        $(".dashboard").append(initialDashboardTemplate);

        // 범례 제거
        $(".map .map-legend").remove();
        console.log("지도가 초기화되었습니다."); // 디버깅 로그

        // 초기화 후 다시 선택 가능하도록 버튼 활성화
        $("#add_marker_btn").prop("disabled", false);
        $("#confirm_location_btn").prop("disabled", false);
    });

});