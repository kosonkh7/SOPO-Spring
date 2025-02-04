package com.ai.pj.controller;

import com.ai.pj.config.TmapConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TmapConfig tmapConfig;

    @Autowired
    public HomeController(TmapConfig tmapConfig) {
        this.tmapConfig = tmapConfig;
    }

    // application.properties에 저장된 Tmap API Key 가져오기
//    @Value("${tmap.api.key:}")
//    private String tmapKey;

    @GetMapping("/")
    public String home(Model model){
        // TmapConfig에서 API 키 가져오기
        String tmapKey = tmapConfig.getTmapApiKey();

        // Tmap API Key를 모델에 추가하여 Mustache에서 사용할 수 있도록 설정
        model.addAttribute("tmapKey", tmapKey);

        return "home"; // home.mustache 렌더링
    }

    // /home 요청을 /로 리다이렉트
    @GetMapping("/home")
    public String redirectToHome() {
        return "redirect:/"; // /home 요청을 기본 경로로 리다이렉트
    }

}
