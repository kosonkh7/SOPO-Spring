package com.ai.pj.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/")
    public String reqAdmin() {

        // 회원 현황.
        // 게시판 현황.
        // 관리자 메모.
        // 관리자 이름.
        // role = HOlD로 갖고 있는 USER 목록 및 USER 관련 company Name
        return "admin/index";
    }
}
