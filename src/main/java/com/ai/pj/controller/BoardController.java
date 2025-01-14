package com.ai.pj.controller;


import com.ai.pj.dto.BoardDTO;
import com.ai.pj.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    @GetMapping("/")
    public String reqBoard(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        model.addAttribute("userId", userId) ;

        List<BoardDTO.Get> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);
        return "/board/board";
    }

    @GetMapping("/write")
    public String reqWrite() {
        return "/board/write";
    }


    @GetMapping("/delete")
    public String reqDelete(@RequestParam Long reqNum, Model model) {

        // 삭제
        if (boardService.delete(reqNum)) {
            model.addAttribute("response", "삭제성공");
        } else {
            model.addAttribute("response", "삭제실패");
        };

        return "/board/";
    }
}
