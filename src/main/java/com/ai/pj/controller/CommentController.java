package com.ai.pj.controller;

import com.ai.pj.dto.CommentDTO;
import com.ai.pj.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 덧글 작성 처리
    @PostMapping
    public String addComment(
            @RequestParam Long boardId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes){
        CommentDTO.Post commentDTO = new CommentDTO.Post();
        commentDTO.setBoardId(boardId);
        commentDTO.setContent(content);
        commentDTO.setUserId("admin"); // 현재 로그인된 사용자 ID를 설정

        commentService.addComment(commentDTO);
        redirectAttributes.addFlashAttribute("message", "덧글이 작성되었습니다.");
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(
            @PathVariable Long id,
            @RequestParam Long boardID,
            RedirectAttributes redirectAttributes){
        commentService.deleteComment(id);
        redirectAttributes.addFlashAttribute("message", "덧글이 삭제되었습니다.");
        return "redirect:/board/" + boardID;
    }

}
