package com.ai.pj.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @Value("${tmap.api.key:}")
    private String tmapKey;

    // 브라우저에서 http://localhost:8080/map 로 접속하면,
    // "map"이라는 이름의 Mustache 템플릿 반환 (즉 mapView.mustache)
    @GetMapping("/map")
    public String showMapPage(Model model) {
        // 모델에 tmapKey를 담아서 Mustache 템플릿이 사용할 수 있게 함
        model.addAttribute("tmapKey", tmapKey);

        // "map/mapView" -> src/main/resources/templates/map/mapView.mustache
        return "map/mapView";
    }


    // 필요하다면 "/" 로도 매핑할 수 있음
//    @GetMapping("/")
//    public String home() {
//        // 예: 홈 화면을 map.mustache로 통일한다면
//        return "map";
//    }
}