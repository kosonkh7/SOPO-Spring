package com.ai.pj.restcontroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")  // 모든 경로에 /api prefix
public class MapRestController {

    @Value("${map.api.host}")
    private String mapUrl;

    private final RestTemplate restTemplate;

    public MapRestController() {
        this.restTemplate = new RestTemplate();
        // 필요하면 timeout, errorHandler 설정 가능
    }

    // 1) GET /api/stations -> Flask GET /stations
    @GetMapping("/stations")
    public ResponseEntity<?> getStationsFromFlask() {
        try {
            String flaskUrl = mapUrl + "/stations"; // Flask 서버 주소

            ResponseEntity<List> response = restTemplate.getForEntity(flaskUrl, List.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody()); // JSON 배열 그대로 반환
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Flask Error");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flask 서버 호출 실패: " + e.getMessage());
        }
    }

    // 2) POST /api/parcel-route -> Flask POST /parcel_route
    @PostMapping("/parcel-route")
    public ResponseEntity<?> parcelRoute(@RequestParam String start_station,
                                         @RequestParam double end_lat,
                                         @RequestParam double end_lon) {
        try {
            String flaskUrl = mapUrl + "/parcel_route";

            // Form-Data로 전송할 경우
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("start_station", start_station);
            params.add("end_lat", end_lat);
            params.add("end_lon", end_lon);

            // Flask는 JSON 응답 -> Map.class
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, params, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flask parcel_route 실패: " + e.getMessage());
        }
    }

    // 3) POST /api/compare-routes -> Flask POST /compare_routes
    @PostMapping("/compare-routes")
    public ResponseEntity<?> compareRoutes(@RequestParam String start_station,
                                           @RequestParam double end_lat,
                                           @RequestParam double end_lon) {
        try {
            String flaskUrl = mapUrl + "/compare_routes";

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("start_station", start_station);
            params.add("end_lat", end_lat);
            params.add("end_lon", end_lon);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, params, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flask compare_routes 실패: " + e.getMessage());
        }
    }

    // 4) POST /api/compare-route-details -> Flask POST /compare_route_details
    @PostMapping("/compare-route-details")
    public ResponseEntity<?> compareRouteDetails(@RequestParam String start_station,
                                                 @RequestParam double end_lat,
                                                 @RequestParam double end_lon) {
        try {
            String flaskUrl = mapUrl + "/compare_route_details";

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("start_station", start_station);
            params.add("end_lat", end_lat);
            params.add("end_lon", end_lon);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, params, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flask compare_route_details 실패: " + e.getMessage());
        }
    }
}