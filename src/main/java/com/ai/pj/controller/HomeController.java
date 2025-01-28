package com.ai.pj.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("managerName", "Admin"); // 실제 로그인 사용자 정보를 여기에 넣을 수도 있음
        return "home"; // home.mustache 렌더링
    }

    // /home 요청을 /로 리다이렉트
    @GetMapping("/home")
    public String redirectToHome() {
        return "redirect:/"; // /home 요청을 기본 경로로 리다이렉트
    }


}
