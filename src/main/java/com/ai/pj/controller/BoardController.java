package com.ai.pj.controller;


import com.ai.pj.dto.BoardDTO;
import com.ai.pj.dto.CommentDTO;
import com.ai.pj.service.BoardService;
import com.ai.pj.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    // /board 요청을 /board/로 리다이렉트
    @GetMapping
    public String redirectToSlash() {
        return "redirect:/board/";
    }

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

    // 게시글 작성 처리
    @PostMapping("/write")
    public String writeBoard(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile imageFile, // 이미지 파일 선택적
            RedirectAttributes redirectAttributes) {
            try {
                String imageUrl = null;
                if (imageFile != null && !imageFile.isEmpty()) {
                    imageUrl = boardService.saveImage(imageFile); // 이미지 저장
                }

                BoardDTO.Post post = new BoardDTO.Post();
                post.setTitle(title);
                post.setContent(content);
                post.setUserId("testUser"); // 로그인된 사용자 ID 설정

                boardService.save(post, imageUrl); // 이미지 URL 전달 (null 가능)
                redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
                return "redirect:/board/";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "게시글 작성 중 오류가 발생했습니다.");
                return "redirect:/board/write";
            }
    }


//    @GetMapping("/delete")
//    public String reqDelete(@RequestParam Long reqNum, Model model) {
//
//        // 삭제
//        if (boardService.delete(reqNum)) {
//            model.addAttribute("response", "삭제성공");
//        } else {
//            model.addAttribute("response", "삭제실패");
//        };
//
//        return "/board/";
//    }

    @PostMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            boardService.delete(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e){
            redirectAttributes.addFlashAttribute("error", "해당 게시글을 찾을 수 없습니다.");
        }
        return "redirect:/board/";
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
    public String editBoard(@PathVariable Long id, Model model) {
        BoardDTO.Get board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "/board/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBoard(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile imageFile, // 이미지 파일 선택적
            RedirectAttributes redirectAttributes) {
            try {
                String imageUrl = null;
                if (imageFile != null && !imageFile.isEmpty()) {
                    imageUrl = boardService.saveImage(imageFile); // 이미지 저장
                }

                BoardDTO.Post updatedPost = new BoardDTO.Post();
                updatedPost.setTitle(title);
                updatedPost.setContent(content);
                updatedPost.setUserId("testUser"); // 로그인된 사용자 ID 설정

                boardService.update(id, updatedPost, imageUrl); // 이미지 URL 포함
                redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
                return "redirect:/board/" + id;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
                return "redirect:/board/edit/" + id;
        }
    }


}
