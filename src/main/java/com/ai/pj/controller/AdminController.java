package com.ai.pj.controller;


import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String redirectToSlash() {
        return "redirect:/admin/";
    }

    @GetMapping("/")
    public String reqAdmin(Model model) {
        // 회원 현황
        // 게시판 현황
        // 관리자 메모
        // 관리자 이름

        // role = HOLD로 갖고 있는 USER 목록 및 USER 관련 companyName
        List<UserDTO.Get> userList = adminService.findByRole(User.UserRole.HOLD);

        model.addAttribute("userList", userList);

        return "admin/index";
    }
}
