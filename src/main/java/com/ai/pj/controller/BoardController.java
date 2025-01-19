package com.ai.pj.controller;


import com.ai.pj.dto.BoardDTO;
import com.ai.pj.dto.CommentDTO;
import com.ai.pj.service.BoardService;
import com.ai.pj.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

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

    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, Model model){
        BoardDTO.Get board = boardService.getBoardById(id);
        List<CommentDTO.Get> comments = commentService.getCommentsByBoard(id);

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);

        return "/board/detail";
    }

    @GetMapping("/edit/{id}")
    public String editBoard(@PathVariable Long id, Model model){
        BoardDTO.Get board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "/board/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBoard(
            @PathVariable Long id,
            @ModelAttribute BoardDTO.Post updatedPost,
            RedirectAttributes redirectAttributes){
        boardService.update(id, updatedPost);
        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
        return "redirect:/board/{id}";
    }



}
