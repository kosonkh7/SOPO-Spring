$(document).ready(function() {
    // 지도 초기화
    var map = new Tmapv2.Map("map_div", {
        center: new Tmapv2.LatLng(37.5665, 126.9780),
        zoom: 11
    });

    var markers = []; // 마커를 저장
    var polylines = []; // 경로를 저장

    const initialCenter = new Tmapv2.LatLng(37.5665, 126.9780); // 초기 중심 좌표
    const initialZoom = 11; // 초기 줌 레벨

    function clearMap() {
        // 기존 마커 제거
        markers.forEach(marker => marker.setMap(null));
        markers = []; // 배열 초기화

        // 기존 경로 제거
        polylines.forEach(polyline => polyline.setMap(null));
        polylines = []; // 배열 초기화
    }

    function displayStations() {
        // 지하철역 데이터 요청
        $.getJSON('/api/stations', function(stations) {

            // 1) 지도에 역 마커 표시
            stations.forEach(station => {
                var marker = new Tmapv2.Marker({
                    position: new Tmapv2.LatLng(station.latitude, station.longitude),
                    map: map,
                    title: station.name
                });
                markers.push(marker);
            });

            // 2) <select>에 <option>들 추가
            // 먼저 기존 옵션 제거 (중복 방지)
            $("#start_station").empty();

            stations.forEach(station => {
                const optionHtml = `<option value="${station.name}">${station.name}</option>`;
                $("#start_station").append(optionHtml);
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

    // 지도 초기화 시 역 표시
    displayStations();

    function updateParcelRouteMap(data) {
        clearMap(); // 기존 지도 상태 초기화

        // 출발 Sub 터미널과 도착 Sub 터미널이 같은 경우 처리
        if (data.start_sub_terminal.name === data.end_sub_terminal.name) {
            var subTerminalPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.start_sub_terminal.lat, data.start_sub_terminal.lon),
                map: map,
                title: `출발 및 도착 Sub 터미널: ${data.start_sub_terminal.name}`
            });
            markers.push(subTerminalPin);
        } else {
            // 출발 Sub 터미널 핀
            var startSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.start_sub_terminal.lat, data.start_sub_terminal.lon),
                map: map,
                title: `출발 Sub 터미널: ${data.start_sub_terminal.name}`
            });
            markers.push(startSubPin);

            // 도착 Sub 터미널 핀
            var endSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.end_sub_terminal.lat, data.end_sub_terminal.lon),
                map: map,
                title: `도착 Sub 터미널: ${data.end_sub_terminal.name}`
            });
            markers.push(endSubPin);
        }

        // Hub 터미널 핀
        var hubPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.hub_terminal.lat, data.hub_terminal.lon),
            map: map,
            title: `Hub 터미널: ${data.hub_terminal.name}`
        });
        markers.push(hubPin);

        // 최종 배송지 핀
        var deliveryPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.end_lat, data.end_lon),
            map: map,
            title: "최종 배송지"
        });
        markers.push(deliveryPin);

        // 경로 추가
        if (data.to_hub_route && data.to_hub_route.length > 0) {
            var toHubPolyline = new Tmapv2.Polyline({
                path: data.to_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF0000", // 빨간색
                strokeWeight: 6,
                map: map
            });
            polylines.push(toHubPolyline);
        }

        if (data.from_hub_route && data.from_hub_route.length > 0) {
            var fromHubPolyline = new Tmapv2.Polyline({
                path: data.from_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#2E64FE", // 파란색
                strokeWeight: 6,
                map: map
            });
            polylines.push(fromHubPolyline);
        }

        if (data.to_destination_route && data.to_destination_route.length > 0) {
            var toDestinationPolyline = new Tmapv2.Polyline({
                path: data.to_destination_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#04B431", // 초록색
                strokeWeight: 6,
                map: map
            });
            polylines.push(toDestinationPolyline);
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 대시보드 업데이트
        $(".dashboard").empty();
        $(".dashboard").append(`<h3>기존 택배 프로세스</h3>`);
        $(".dashboard").append(`<p>출발 Sub 터미널: ${data.start_sub_terminal.name}</p>`);
        $(".dashboard").append(`<p>Hub 터미널: ${data.hub_terminal.name}</p>`);
        $(".dashboard").append(`<p>도착 Sub 터미널: ${data.end_sub_terminal.name}</p>`);
        $(".dashboard").append(`<p>총 이동 소요 시간: ${Math.round(data.total_time / 60)}분</p>`);
    }

    function updateMap(data) {
        // 기존 마커와 경로 제거
        clearMap();

        // 출발지 마커 추가
        var startMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.start_lat, data.start_lon),
            map: map,
            title: `출발지: ${data.start_station}`
        });
        markers.push(startMarker); // 배열에 저장

        // 도착지 마커 추가
        var endMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.end_lat, data.end_lon),
            map: map,
            title: "배송지"
        });
        markers.push(endMarker); // 배열에 저장

        // 경로 중심 계산
        var centerLat = (data.start_lat + data.end_lat) / 2;
        var centerLon = (data.start_lon + data.end_lon) / 2;

        // 지도 중심 이동
        map.setCenter(new Tmapv2.LatLng(centerLat, centerLon));

        // 지하철 경로 추가
        if (data.subway_route && data.subway_route.length > 0) {
            var subwayPolyline = new Tmapv2.Polyline({
                path: data.subway_route.map(coord => new Tmapv2.LatLng(coord[1], coord[0])),
                strokeColor: "#2E64FE",
                strokeWeight: 6,
                map: map
            });
            polylines.push(subwayPolyline); // 배열에 저장
        }

        // 주행 경로 추가
        if (data.driving_route && data.driving_route.length > 0) {
            var drivingPolyline = new Tmapv2.Polyline({
                path: data.driving_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#04B431",
                strokeWeight: 6,
                map: map
            });
            polylines.push(drivingPolyline); // 배열에 저장
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 대시보드 업데이트
        $(".dashboard").empty();
        $(".dashboard").append(`<h3>지하철 창고 프로세스</h3>`);

        // 지하철 예상 소요 시간 표시 (유효하지 않은 값이면 0으로 대체)
        var subwayTime = isNaN(data.subway_total_time) || data.subway_total_time <= 0 ? 0 : Math.round(data.subway_total_time / 60);
        $(".dashboard").append(`<p>지하철도 이동 소요 시간: ${subwayTime}분</p>`);

        // 자동차 예상 소요 시간 표시 (유효하지 않은 값이면 0으로 대체)
        var drivingTime = isNaN(data.driving_total_time) || data.driving_total_time <= 0 ? 0 : Math.round(data.driving_total_time / 60);
        $(".dashboard").append(`<p>자동차 이동 소요 시간: ${drivingTime}분</p>`);

        // 총 예상 소요 시간
        var totalTime = subwayTime + drivingTime;
        $(".dashboard").append(`<p>총 이동 소요 시간: ${totalTime}분</p>`);

        // 비교 결과 표시
        if (data.reason) {
            $(".dashboard").append(`<p id="comparison_result">${data.reason}</p>`);
        }
    }

    function updateComparisonRoutes(data) {
        clearMap(); // 기존 지도 상태 초기화

        // 출발 Sub 터미널과 도착 Sub 터미널이 같은 경우 처리
        if (data.parcel.start_sub_terminal.name === data.parcel.end_sub_terminal.name) {
            const subTerminalPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.start_sub_terminal.lat, data.parcel.start_sub_terminal.lon),
                map: map,
                title: `출발 및 도착 Sub 터미널: ${data.parcel.start_sub_terminal.name}`
            });
            markers.push(subTerminalPin);
        } else {
            // 출발 Sub 터미널 핀
            const startSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.start_sub_terminal.lat, data.parcel.start_sub_terminal.lon),
                map: map,
                title: `출발 Sub 터미널: ${data.parcel.start_sub_terminal.name}`
            });
            markers.push(startSubPin);

            // 도착 Sub 터미널 핀
            const endSubPin = new Tmapv2.Marker({
                position: new Tmapv2.LatLng(data.parcel.end_sub_terminal.lat, data.parcel.end_sub_terminal.lon),
                map: map,
                title: `도착 Sub 터미널: ${data.parcel.end_sub_terminal.name}`
            });
            markers.push(endSubPin);
        }

        // Hub 터미널 핀
        const hubPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.hub_terminal.lat, data.parcel.hub_terminal.lon),
            map: map,
            title: `Hub 터미널: ${data.parcel.hub_terminal.name}`
        });
        markers.push(hubPin);

        // 출발지 마커 추가
        const startMarker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.start_lat, data.parcel.start_lon),
            map: map,
            title: `출발지: ${data.parcel.start_station}`
        });
        markers.push(startMarker); // 배열에 저장

        // 최종 배송지 핀
        const deliveryPin = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(data.parcel.end_lat, data.parcel.end_lon),
            map: map,
            title: "배송지"
        });
        markers.push(deliveryPin);

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 택배 경로
        if (data.parcel.to_hub_route && data.parcel.to_hub_route.length > 0) {
            const toHubPolyline = new Tmapv2.Polyline({
                path: data.parcel.to_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF0000", // 빨간색 #FF0000
                strokeWeight: 6,
                map: map
            });
            polylines.push(toHubPolyline);
        }

        if (data.parcel.from_hub_route && data.parcel.from_hub_route.length > 0) {
            const fromHubPolyline = new Tmapv2.Polyline({
                path: data.parcel.from_hub_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF0000", // 주황색 #FFA500
                strokeWeight: 6,
                map: map
            });
            polylines.push(fromHubPolyline);
        }

        if (data.parcel.to_destination_route && data.parcel.to_destination_route.length > 0) {
            const toDestinationPolyline = new Tmapv2.Polyline({
                path: data.parcel.to_destination_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#FF0000", // 노란색 #FFFF00
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
                    strokeColor: "#2E64FE", // 파란색 #0000FF
                    strokeWeight: 6,
                    map: map
                });
                polylines.push(subwayPolyline);
            }
        }

        if (selectedRoute.driving_route && selectedRoute.driving_route.length > 0) {
            const drivingPolyline = new Tmapv2.Polyline({
                path: selectedRoute.driving_route.map(coord => new Tmapv2.LatLng(coord[0], coord[1])),
                strokeColor: "#2E64FE", // 초록색
                strokeWeight: 6,
                map: map
            });
            polylines.push(drivingPolyline);
        }

        // 지도 영역 조정
        adjustMapBounds(markers);

        // 대시보드 업데이트
        $(".dashboard").empty();
        $(".dashboard").append(`
            <h3>이동 경로 비교 결과</h3>
            <p>선택된 이동 경로: ${data.faster_route}</p>
            <table>
                <thead>
                    <tr>
                        <th>프로세스</th>
                        <th>총 거리 (km)</th>
                        <th>총 시간 (분)</th>
                        <th>비용 (₩)</th>
                        <th>탄소 배출량 (g CO₂)</th>
                        <th>단계 수</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>기존 택배</td>
                        <td>${data.parcel.distance.toFixed(2)} km</td>
                        <td>${Math.round(data.parcel.time)}분</td>
                        <td>${data.parcel.cost.toLocaleString()}₩</td>
                        <td>${data.parcel.emission.toFixed(2)} g CO₂</td>
                        <td>3단계</td>
                    </tr>
                    <tr>
                        <td>지하철 창고</td>
                        <td>${selectedRoute.distance.toFixed(2)} km</td>
                        <td>${Math.round(selectedRoute.time)}분</td>
                        <td>${selectedRoute.cost.toLocaleString()}₩</td>
                        <td>${selectedRoute.emission.toFixed(2)} g CO₂</td>
                        <td>${Math.round(selectedRoute.level)}단계</td>
                    </tr>
                </tbody>
            </table>
        `);
    }

    $("#default_btn").on("click", function() {
        var formData = $("#route_form").serialize();
        $.post("/api/parcel-route", formData, function(data) {
            console.log("택배 경로 응답 데이터:", data);
            updateParcelRouteMap(data); // 지도를 업데이트하는 함수
        }).fail(function(xhr, status, error) {
            console.error("에러:", error);
            console.error("서버 응답:", xhr.responseText);
        });
    });

    $("#subway_btn").on("click", function() {
        var formData = $("#route_form").serialize();
        $.post("/api/compare-routes", formData, function(data) {
            console.log("응답 데이터:", data); // 디버깅 로그
            updateMap(data);
        }).fail(function(xhr, status, error) {
            console.error("에러 상태:", status); // 에러 상태 확인
            console.error("에러 메시지:", error); // 에러 메시지 확인
            console.error("서버 응답:", xhr.responseText); // 서버에서 반환된 에러 메시지
        });
    });

    $("#compare_routes_btn").on("click", function() {
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
            });
    });


    $("#reset_btn").on("click", function() {
        clearMap(); // 기존 마커와 경로 제거
        displayStations(); // 지하철역 다시 표시
        map.setCenter(initialCenter); // 초기 중심 좌표로 이동
        map.setZoom(initialZoom); // 초기 줌 레벨로 복원

        // 대시보드 초기화
        $(".dashboard").empty();
        $(".dashboard").append(`<h3>Dashboard</h3><br>`);
        console.log("지도가 초기화되었습니다."); // 디버깅 로그
    });

});