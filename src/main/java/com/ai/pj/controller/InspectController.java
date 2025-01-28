package com.ai.pj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inspect") // /inspect 요청을 처리
public class InspectController {

    @GetMapping
    public String redirectToInspect() {
        return "redirect:/inspect/"; // /inspect로 접근하면 /inspect/로 리다이렉트
    }

    @GetMapping("/")
    public String showInspectPage() {
        return "inspect/inspect"; // templates/inspect/inspect.mustache 로 매핑
    }
}